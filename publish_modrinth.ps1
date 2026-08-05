param(
    [Parameter(Mandatory = $true)]
    [string]$ProjectId,

    [string]$Token = $env:MODRINTH_TOKEN,

    [string]$BuildOutput = (Join-Path $PSScriptRoot "build-output"),

    [ValidateSet("release", "beta", "alpha")]
    [string]$VersionType = "release",

    [string]$Changelog = "",

    [switch]$DryRun
)

$ErrorActionPreference = "Stop"

try {
    Add-Type -AssemblyName System.Net.Http -ErrorAction Stop
} catch {
    throw "System.Net.Http could not be loaded: $($_.Exception.Message)"
}

if (!$DryRun -and [string]::IsNullOrWhiteSpace($Token)) {
    throw "MODRINTH_TOKEN is required. Set it as an environment variable or pass -Token."
}

if (!(Test-Path -LiteralPath $BuildOutput)) {
    throw "Build output directory not found: $BuildOutput"
}

$filePattern = '^AdachiRei-nui-(fabric|forge|neoforge)-mc([^\-]+)-v([0-9]+\.[0-9]+\.[0-9]+)\.jar$'
$files = Get-ChildItem -LiteralPath $BuildOutput -Recurse -File -Filter "AdachiRei-nui-*.jar" |
    ForEach-Object {
        $match = [regex]::Match($_.Name, $filePattern)
        if ($match.Success) {
            [pscustomobject]@{
                File = $_
                Loader = $match.Groups[1].Value
                MinecraftVersion = $match.Groups[2].Value
                ModVersion = $match.Groups[3].Value
            }
        }
    }

if (!$files) {
    throw "No standardized build artifacts found under: $BuildOutput"
}

function Invoke-ModrinthVersionUpload($Group) {
    $loader = $Group[0].Loader
    $versionNumber = "{0}+mc{1}-{2}" -f $Group[0].ModVersion, $Group[0].MinecraftVersion, $loader
    $fileParts = @()
    $loaders = @()
    foreach ($entry in $Group) {
        $index = $fileParts.Count
        $partName = "file{0}" -f $index
        $fileParts += $partName
        $loaders += $entry.Loader
    }
    $loaders = @($loaders | Sort-Object -Unique)

    $dependencies = @()
    if ($loader -eq "fabric") {
        $dependencies = @(
            [ordered]@{
                project_id = "P7dR8mSH"
                dependency_type = "required"
            },
            [ordered]@{
                project_id = "9s6osm5g"
                dependency_type = "required"
            },
            [ordered]@{
                project_id = "mOgUt4GM"
                dependency_type = "optional"
            }
        )
    }

    $data = [ordered]@{
        project_id = $ProjectId
        name = "AdachiRei-nui {0} for Minecraft {1} ({2})" -f $Group[0].ModVersion, $Group[0].MinecraftVersion, $loader
        version_number = $versionNumber
        version_type = $VersionType
        status = "listed"
        loaders = $loaders
        game_versions = @($Group[0].MinecraftVersion)
        file_parts = $fileParts
        primary_file = $fileParts[0]
        featured = $false
        changelog = $Changelog
        dependencies = $dependencies
    }
    $json = $data | ConvertTo-Json -Compress -Depth 5

    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "  Modrinth $versionNumber [$($loaders -join ', ')]" -ForegroundColor Yellow
    foreach ($entry in $Group) {
        Write-Host "  $($entry.File.FullName)" -ForegroundColor DarkCyan
    }

    if ($DryRun) {
        Write-Host "  DRY RUN: not uploaded" -ForegroundColor DarkYellow
        return
    }

    $client = [System.Net.Http.HttpClient]::new()
    $content = [System.Net.Http.MultipartFormDataContent]::new()
    $content.Add([System.Net.Http.StringContent]::new($json, [System.Text.Encoding]::UTF8, "application/json"), "data")
    $streams = @()
    try {
        for ($i = 0; $i -lt $Group.Count; $i++) {
            $stream = [System.IO.File]::OpenRead($Group[$i].File.FullName)
            $streams += $stream
            $fileContent = [System.Net.Http.StreamContent]::new($stream)
            $fileContent.Headers.ContentType = [System.Net.Http.Headers.MediaTypeHeaderValue]::new("application/java-archive")
            $content.Add($fileContent, $fileParts[$i], $Group[$i].File.Name)
        }

        $client.DefaultRequestHeaders.Authorization = [System.Net.Http.Headers.AuthenticationHeaderValue]::new("Bearer", $Token)
        $response = $client.PostAsync("https://api.modrinth.com/v2/version", $content).GetAwaiter().GetResult()
        $body = $response.Content.ReadAsStringAsync().GetAwaiter().GetResult()
        if (!$response.IsSuccessStatusCode) {
            throw "Modrinth upload failed ($([int]$response.StatusCode)): $body"
        }
        Write-Host "  Uploaded: $($response.StatusCode)" -ForegroundColor Green
    }
    finally {
        foreach ($stream in $streams) { $stream.Dispose() }
        $content.Dispose()
        $client.Dispose()
    }
}

$groups = $files | Group-Object MinecraftVersion, ModVersion, Loader | Sort-Object Name
foreach ($group in $groups) {
    Invoke-ModrinthVersionUpload @($group.Group)
}

Write-Host "========================================" -ForegroundColor Cyan
if ($DryRun) {
    Write-Host "Dry run completed." -ForegroundColor Yellow
} else {
    Write-Host "All Modrinth uploads completed." -ForegroundColor Green
}
