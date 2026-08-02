$versions = @(
    "1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.6",
    "1.21.1", "1.21.3", "1.21.4", "1.21.5", "1.21.6",
    "1.21.7", "1.21.8", "1.21.9", "1.21.10", "1.21.11"
)

& "$PSScriptRoot\run_all_branches.ps1" -Loader forge -Task runClient -Versions $versions
exit $LASTEXITCODE
