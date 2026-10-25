$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
Push-Location $root
try {
    docker build -t ci-dashboard-backend:local ./backend
    if ($LASTEXITCODE -ne 0) { throw 'Docker image build failed.' }

    $env:STABLE_IMAGE = 'ci-dashboard-backend:local'
    $env:CANARY_IMAGE = 'ci-dashboard-backend:local'
    $env:STABLE_VERSION = 'local-stable'
    $env:CANARY_VERSION = 'local-candidate'
    $env:STABLE_WEIGHT = '90'
    $env:CANARY_WEIGHT = '10'
    docker compose -f docker-compose.cd.yml up -d --wait
    if ($LASTEXITCODE -ne 0) { throw 'Canary stack did not become healthy.' }

    Invoke-RestMethod http://localhost:8180/actuator/health | ConvertTo-Json
    Write-Host 'Canary is live at 10% traffic; stable receives 90%.' -ForegroundColor Green
    Write-Host 'Check routing: .\ops\check-canary.ps1'
    Write-Host 'Promote:       .\ops\promote-canary.ps1'
    Write-Host 'Rollback:      .\ops\rollback-canary.ps1'
} finally { Pop-Location }
