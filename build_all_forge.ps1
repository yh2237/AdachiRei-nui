$originalBranch = git rev-parse --abbrev-ref HEAD
$branches = @(
    "forge/1.20.1"
)

$outputDir = "build-output"
if (!(Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir | Out-Null
}

foreach ($b in $branches) {
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "  Building [$b] ..." -ForegroundColor Yellow
    Write-Host "========================================" -ForegroundColor Cyan

    git stash push -m "auto-stash" 2>$null
    git checkout $b
    if ($LASTEXITCODE -ne 0) {
        Write-Host "FAILED: checkout $b" -ForegroundColor Red
        continue
    }

    & .\gradlew build 2>&1 | Out-String | Out-Null
    if ($LASTEXITCODE -eq 0) {
        Copy-Item "build/libs/AdachiRei-nui-*.jar" -Destination $outputDir -ErrorAction SilentlyContinue
        Write-Host "  $b -> OK" -ForegroundColor Green
    } else {
        Write-Host "  $b -> BUILD FAILED" -ForegroundColor Red
    }
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  All done. Jars in ./$outputDir/" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
git checkout $originalBranch
git stash pop 2>$null
pause
