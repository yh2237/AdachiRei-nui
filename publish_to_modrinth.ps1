param(
    [string]$ProjectId = "adachirei-nui",
    [string]$VersionNumber = "",
    [string]$Changelog = "",
    [string]$ApiToken = ""
)

if (-not $ApiToken) {
    $ApiToken = [System.Environment]::GetEnvironmentVariable("MODRINTH_TOKEN")
}
if (-not $ApiToken) {
    $ApiToken = Read-Host "Enter Modrinth API token"
}
if (-not $ApiToken) {
    Write-Host "ERROR: No API token" -ForegroundColor Red; pause; exit 1
}

$outputDir = "build-output"
if (!(Test-Path $outputDir)) {
    Write-Host "ERROR: $outputDir not found. Run build_all.ps1 first." -ForegroundColor Red
    pause; exit 1
}

$jars = Get-ChildItem "$outputDir\AdachiRei-nui-*.jar" | Where-Object { $_.Name -notlike "*-sources*" }
if ($jars.Count -eq 0) {
    Write-Host "ERROR: No jar files in $outputDir" -ForegroundColor Red
    pause; exit 1
}

if (-not $VersionNumber) {
    $VersionNumber = Read-Host "Enter version number (e.g. 1.1.4)"
}

$tempDir = Join-Path $env:TEMP ([System.IO.Path]::GetRandomFileName())
New-Item -ItemType Directory -Path $tempDir | Out-Null

$mcVersions = @()
$fileArgs = @()
$i = 0
foreach ($jar in $jars) {
    $i++
    $mcVer = ($jar.BaseName -replace '^AdachiRei-nui-mc', '') -replace '-v[0-9.]+$', ''
    $mcVersions += $mcVer
    $fileName = "AdachiRei-nui-${VersionNumber}+mc${mcVer}.jar"
    Copy-Item $jar.FullName (Join-Path $tempDir $fileName) -Force
    $fileArgs += @{Field="file-$i"; Path=(Join-Path $tempDir $fileName)}
}

$mcVersions = $mcVersions | Sort-Object -Unique
$fileParts = 1..$jars.Count | ForEach-Object { "file-$_" }

$metadata = @{
    project_id    = $ProjectId
    name          = "v${VersionNumber}"
    version_number = $VersionNumber
    changelog     = $Changelog
    game_versions  = $mcVersions
    version_type  = "release"
    loaders       = @("fabric")
    featured      = $false
    status        = "listed"
    file_parts    = $fileParts
    primary_file  = $fileParts[0]
    dependencies  = @()
}

$metadataJson = $metadata | ConvertTo-Json -Compress
$metadataFile = Join-Path $tempDir "metadata.json"
Set-Content $metadataFile -Value $metadataJson -Encoding UTF8

Write-Host "Uploading $($jars.Count) jar(s) for MC $($mcVersions -join ', ') ..." -ForegroundColor Yellow

$curlArgs = @(
    "-s", "-X", "POST"
    "https://api.modrinth.com/v2/project/$ProjectId/version"
    "-H", "Authorization: $ApiToken"
    "-H", "User-Agent: yh2237/AdachiRei-nui/1.0"
    "-F", "data=@$metadataFile;type=application/json"
)
foreach ($fa in $fileArgs) {
    $curlArgs += "-F"
    $curlArgs += "$($fa.Field)=@$($fa.Path);type=application/java-archive"
}

$response = & curl.exe $curlArgs 2>&1

Remove-Item -Recurse -Force $tempDir

if ($LASTEXITCODE -eq 0) {
    try {
        $result = $response | ConvertFrom-Json
        if ($result.id) {
            Write-Host "Published: https://modrinth.com/project/$ProjectId/version/$($result.id)" -ForegroundColor Green
        } else {
            Write-Host "Response: $response" -ForegroundColor Yellow
        }
    } catch {
        Write-Host "Response: $response" -ForegroundColor Yellow
    }
} else {
    Write-Host "FAILED:" -ForegroundColor Red
    $response | Write-Host
}

pause