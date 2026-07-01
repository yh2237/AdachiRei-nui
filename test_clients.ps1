$branches = @(
    "fabric/1.21.6",
    "fabric/1.21.7",
    "fabric/1.21.8",
    "fabric/1.21.9",
    "fabric/1.21.10",
    "fabric/1.21.11"
)

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
git checkout main
git stash pop 2>$null
pause