$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
Push-Location $root
try {
    $env:STABLE_WEIGHT = '1000'
    $env:CANARY_WEIGHT = '1'
    docker compose -f docker-compose.cd.yml up -d --no-deps --wait canary-gateway
    if ($LASTEXITCODE -ne 0) { throw 'Could not route traffic back to stable.' }
    Write-Host 'Rollback complete: stable receives over 99.9% of gateway traffic.' -ForegroundColor Yellow
} finally { Pop-Location }
