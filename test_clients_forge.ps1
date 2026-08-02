$originalBranch = git rev-parse --abbrev-ref HEAD
$branches = @(
    "forge/1.20.1"
    "forge/1.20.2"
    "forge/1.20.3"
    "forge/1.20.4"
    "forge/1.20.5"
    "forge/1.20.6"
    "forge/1.21.1"
    "forge/1.21.2"
    "forge/1.21.3"
    "forge/1.21.4"
    "forge/1.21.5"
    "forge/1.21.6"
    "forge/1.21.7"
    "forge/1.21.8"
    "forge/1.21.9"
    "forge/1.21.10"
    "forge/1.21.11"
)

# Forge 1.20.1 requires JDK 17 — find it if not already set
if ($env:JAVA_HOME -notmatch "jdk-?17") {
    $jdk17 = @(
        "$env:LOCALAPPDATA\Programs\Eclipse Adoptium\jdk-17*",
        "$env:ProgramFiles\Eclipse Adoptium\jdk-17*",
        "C:\Program Files\Java\jdk-17*",
        "C:\Program Files\Eclipse Adoptium\jdk-17*",
        "$env:TEMP\jdk17_unzipped\jdk-17*"
    ) | ForEach-Object { Get-ChildItem $_ -ErrorAction SilentlyContinue } | Select-Object -First 1
    if ($jdk17) {
        $env:JAVA_HOME = $jdk17.FullName
        Write-Host "Using JDK 17: $env:JAVA_HOME" -ForegroundColor Cyan
    } else {
        Write-Host "WARNING: JDK 17 not found. Forge client may fail." -ForegroundColor Yellow
    }
}

foreach ($b in $branches) {
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "  [$b]" -ForegroundColor Yellow
    Write-Host "========================================" -ForegroundColor Cyan

    git stash push -m "auto-stash" 2>$null
    git checkout $b
    if ($LASTEXITCODE -ne 0) {
        Write-Host "FAILED: $b" -ForegroundColor Red
        pause
        exit 1
    }

    Write-Host "`nLaunching Minecraft $b ..." -ForegroundColor Green
    Write-Host "Close the game to proceed.`n" -ForegroundColor Green

    & .\gradlew runClient
    Write-Host "`nFinished $b`n" -ForegroundColor Green
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  All done" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
git checkout $originalBranch
git stash pop 2>$null
pause
