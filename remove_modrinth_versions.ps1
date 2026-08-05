param(
    [Parameter(Mandatory = $true)]
    [string]$ProjectId,

    [Alias("Version")]
    [string]$TargetVersion = "1.1.5",

    [string]$Token = $env:MODRINTH_TOKEN,

    [switch]$ConfirmDelete
)

$ErrorActionPreference = "Stop"
Add-Type -AssemblyName System.Net.Http

$client = [System.Net.Http.HttpClient]::new()
try {
    if ($ConfirmDelete) {
        if ([string]::IsNullOrWhiteSpace($Token)) {
            throw "MODRINTH_TOKEN is required when -ConfirmDelete is specified."
        }
        $client.DefaultRequestHeaders.Authorization =
            [System.Net.Http.Headers.AuthenticationHeaderValue]::new("Bearer", $Token)
    }

    $uri = "https://api.modrinth.com/v2/project/{0}/version" -f $ProjectId
    $response = $client.GetAsync($uri).GetAwaiter().GetResult()
    $body = $response.Content.ReadAsStringAsync().GetAwaiter().GetResult()
    if (!$response.IsSuccessStatusCode) {
        throw "Could not list Modrinth versions ($([int]$response.StatusCode)): $body"
    }

    $parsedVersions = $body | ConvertFrom-Json
    $versions = @(
        foreach ($candidate in $parsedVersions) {
            if (([string]$candidate.version_number).Contains([string]$TargetVersion)) {
                $candidate
            }
        }
    )

    if (!$versions) {
        Write-Host "No versions matching '$TargetVersion' were found." -ForegroundColor Yellow
        return
    }

    Write-Host "Matching Modrinth versions: $($versions.Count)" -ForegroundColor Cyan
    foreach ($item in $versions) {
        Write-Host "  $($item.id)  $($item.version_number)  $($item.name)" -ForegroundColor Yellow
    }

    if (!$ConfirmDelete) {
        Write-Host "Dry run: nothing was deleted. Add -ConfirmDelete to remove exactly these versions." -ForegroundColor DarkYellow
        return
    }

    foreach ($item in $versions) {
        $deleteUri = "https://api.modrinth.com/v2/version/{0}" -f $item.id
        $deleteResponse = $client.DeleteAsync($deleteUri).GetAwaiter().GetResult()
        $deleteBody = $deleteResponse.Content.ReadAsStringAsync().GetAwaiter().GetResult()
        if (!$deleteResponse.IsSuccessStatusCode) {
            throw "Failed to delete $($item.id) ($([int]$deleteResponse.StatusCode)): $deleteBody"
        }
        Write-Host "Deleted $($item.version_number) ($($item.id))" -ForegroundColor Green
    }
}
finally {
    $client.Dispose()
}
