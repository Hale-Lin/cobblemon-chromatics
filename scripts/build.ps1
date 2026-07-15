[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot

& (Join-Path $PSScriptRoot 'sync-dependencies.ps1')
& (Join-Path $projectRoot 'gradlew.bat') clean build

