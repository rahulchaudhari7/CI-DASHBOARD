$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
Push-Location $root
try {
    $env:STABLE_WEIGHT = '1'
    $env:CANARY_WEIGHT = '1000'
    docker compose -f docker-compose.cd.yml up -d --no-deps --wait canary-gateway
    if ($LASTEXITCODE -ne 0) { throw 'Could not route traffic to the canary.' }
    1..10 | ForEach-Object {
        $response = Invoke-RestMethod http://localhost:8180/api/deployment/instance
        if ($response.slot -ne 'canary') { throw "Expected canary, received $($response.slot). Run rollback-canary.ps1." }
    }
    Write-Host 'Canary promoted: it receives over 99.9% of gateway traffic.' -ForegroundColor Green
} finally { Pop-Location }
