[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
$libraryDirectory = Join-Path $projectRoot 'libs'
$fileName = 'Cobblemon-fabric-1.7.3+1.21.1.jar'
$target = Join-Path $libraryDirectory $fileName
$downloadUrl = 'https://cdn.modrinth.com/data/MdwFAVRL/versions/kF7CvxTo/Cobblemon-fabric-1.7.3%2B1.21.1.jar'
$expectedSha1 = 'f192cda3fdfec0f20d1dee1887ec1b9e77b1618c'

New-Item -ItemType Directory -Force -Path $libraryDirectory | Out-Null

if (Test-Path -LiteralPath $target) {
    $actualSha1 = (Get-FileHash -LiteralPath $target -Algorithm SHA1).Hash.ToLowerInvariant()
    if ($actualSha1 -eq $expectedSha1) {
        Write-Host "Dependency is current: $fileName"
        exit 0
    }

    throw "Existing dependency has an unexpected SHA-1: $actualSha1"
}

$temporaryTarget = "$target.download"
try {
    Invoke-WebRequest -UseBasicParsing -Uri $downloadUrl -OutFile $temporaryTarget
    $actualSha1 = (Get-FileHash -LiteralPath $temporaryTarget -Algorithm SHA1).Hash.ToLowerInvariant()
    if ($actualSha1 -ne $expectedSha1) {
        throw "Downloaded dependency has SHA-1 $actualSha1; expected $expectedSha1"
    }

    Move-Item -LiteralPath $temporaryTarget -Destination $target
    Write-Host "Downloaded and verified: $fileName"
} finally {
    if (Test-Path -LiteralPath $temporaryTarget) {
        Remove-Item -LiteralPath $temporaryTarget -Force
    }
}

