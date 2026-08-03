param(
    [Parameter(Mandatory = $true)]
    [ValidateSet("fabric", "forge", "neoforge")]
    [string]$Loader,

    [Parameter(Mandatory = $true)]
    [ValidateSet("build", "runClient")]
    [string]$Task,

    [Parameter(Mandatory = $true)]
    [string[]]$Versions
)

$ErrorActionPreference = "Stop"
$repoRoot = $PSScriptRoot
$branches = @($Versions | ForEach-Object { "$Loader/$_" })
$outputDir = Join-Path $repoRoot "build-output"
$worktreeDir = Join-Path $env:TEMP ("adachirei-nui-{0}-{1}" -f $Loader, [Guid]::NewGuid().ToString("N"))
$originalJavaHome = $env:JAVA_HOME
$originalPath = $env:Path
$failedBranches = New-Object System.Collections.Generic.List[string]
$worktreeAdded = $false

function Find-Jdk([int]$Major) {
    $patterns = @(
        (Join-Path $env:LOCALAPPDATA "Programs\Eclipse Adoptium\jdk-$Major*"),
        (Join-Path $env:ProgramFiles "Eclipse Adoptium\jdk-$Major*"),
        (Join-Path $env:ProgramFiles "Java\jdk-$Major*")
    )

    return $patterns |
        ForEach-Object { Get-ChildItem -Path $_ -Directory -ErrorAction SilentlyContinue } |
        Sort-Object Name -Descending |
        Select-Object -First 1
}

function Set-JdkForVersion([string]$MinecraftVersion) {
    $requiredMajor = if ([Version]$MinecraftVersion -ge [Version]"1.20.5") { 21 } else { 17 }
    $jdk = Find-Jdk $requiredMajor

    if ($jdk) {
        $env:JAVA_HOME = $jdk.FullName
        $env:Path = (Join-Path $jdk.FullName "bin") + ";" + $originalPath
        Write-Host "  Java $requiredMajor -> $($jdk.FullName)" -ForegroundColor DarkCyan
    } else {
        $env:JAVA_HOME = $originalJavaHome
        $env:Path = $originalPath
        Write-Host "  WARNING: JDK $requiredMajor was not found; using the current Java." -ForegroundColor Yellow
    }
}

function Resolve-BranchRef([string]$Branch) {
    & git -C $repoRoot show-ref --verify --quiet "refs/heads/$Branch"
    if ($LASTEXITCODE -eq 0) {
        return $Branch
    }

    & git -C $repoRoot show-ref --verify --quiet "refs/remotes/origin/$Branch"
    if ($LASTEXITCODE -eq 0) {
        return "origin/$Branch"
    }

    throw "Branch not found locally or on origin: $Branch"
}

try {
    if ($Task -eq "build" -and !(Test-Path -LiteralPath $outputDir)) {
        New-Item -ItemType Directory -Path $outputDir | Out-Null
    }

    Write-Host "Creating isolated worktree..." -ForegroundColor DarkCyan
    $firstBranchRef = Resolve-BranchRef $branches[0]
    & git -C $repoRoot worktree add --detach $worktreeDir $firstBranchRef
    if ($LASTEXITCODE -ne 0) {
        throw "Failed to create the temporary worktree."
    }
    $worktreeAdded = $true

    foreach ($index in 0..($branches.Count - 1)) {
        $branch = $branches[$index]
        $version = $Versions[$index]

        Write-Host "========================================" -ForegroundColor Cyan
        Write-Host "  $Task [$branch]" -ForegroundColor Yellow
        Write-Host "========================================" -ForegroundColor Cyan

        if ($index -gt 0) {
            $branchRef = Resolve-BranchRef $branch
            & git -C $worktreeDir checkout --detach $branchRef
            if ($LASTEXITCODE -ne 0) {
                Write-Host "  CHECKOUT FAILED" -ForegroundColor Red
                $failedBranches.Add($branch)
                continue
            }
        }

        Set-JdkForVersion $version
        Push-Location $worktreeDir
        try {
            # Windows PowerShell turns native stderr into a terminating
            # NativeCommandError when the script-wide preference is Stop.
            $ErrorActionPreference = "Continue"
            if ($Task -eq "runClient") {
                Write-Host "  Close Minecraft to continue to the next branch." -ForegroundColor Green
                & .\gradlew.bat runClient --no-daemon
            } else {
                $logFile = Join-Path $env:TEMP ("adachirei-nui-build-{0}-{1}.log" -f $Loader, $version)
                & .\gradlew.bat build --no-daemon --console=plain *> $logFile
            }
            $gradleExitCode = $LASTEXITCODE

            # Minecraft can show a mod-loading error screen and still let the
            # Gradle runClient task exit successfully. Inspect the game log so
            # those runs are reported as failures by this script.
            if ($Task -eq "runClient" -and $gradleExitCode -eq 0) {
                $latestLog = Join-Path $worktreeDir "run\logs\latest.log"
                if (Test-Path -LiteralPath $latestLog) {
                    $loadingErrors = Select-String -LiteralPath $latestLog -Pattern @(
                        "constructed 0 mods",
                        "Failed to initialize mod containers",
                        "Caught exception during event",
                        "ModLoadingException",
                        "Could not execute entrypoint",
                        "Loading errors encountered"
                    )
                    if ($loadingErrors) {
                        Write-Host "  MOD LOADING ERROR" -ForegroundColor Red
                        $loadingErrors | Select-Object -Last 20 | ForEach-Object { Write-Host "  $($_.Line)" -ForegroundColor DarkRed }
                        $gradleExitCode = 1
                    }
                }
            }
        }
        finally {
            $ErrorActionPreference = "Stop"
            Pop-Location
        }

        if ($gradleExitCode -ne 0) {
            Write-Host "  FAILED" -ForegroundColor Red
            $failedBranches.Add($branch)
            if ($Task -eq "build" -and (Test-Path -LiteralPath $logFile)) {
                Get-Content -LiteralPath $logFile -Tail 80
            }
            continue
        }

        if ($Task -eq "build") {
            Get-ChildItem -Path (Join-Path $worktreeDir "build\libs") -Filter "AdachiRei-nui-*.jar" -ErrorAction SilentlyContinue |
                Where-Object { $_.Name -notmatch "-(sources|dev|shadow)\.jar$" } |
                Copy-Item -Destination $outputDir -Force
        }

        Write-Host "  OK" -ForegroundColor Green
    }
}
finally {
    $env:JAVA_HOME = $originalJavaHome
    $env:Path = $originalPath

    if ($worktreeAdded) {
        & git -C $repoRoot worktree remove --force $worktreeDir 2>$null
        if ($LASTEXITCODE -ne 0) {
            Write-Host "WARNING: Could not remove temporary worktree: $worktreeDir" -ForegroundColor Yellow
        }
    }
}

Write-Host "========================================" -ForegroundColor Cyan
if ($failedBranches.Count -eq 0) {
    if ($Task -eq "build") {
        Write-Host "All builds passed. Jars: $outputDir" -ForegroundColor Green
    } else {
        Write-Host "All client runs finished." -ForegroundColor Green
    }
    exit 0
}

Write-Host "Failed branches:" -ForegroundColor Red
$failedBranches | ForEach-Object { Write-Host "  $_" -ForegroundColor Red }
exit 1
