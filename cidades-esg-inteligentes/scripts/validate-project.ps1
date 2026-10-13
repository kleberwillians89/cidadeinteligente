$ErrorActionPreference = 'Stop'

$requiredFiles = @(
    'pom.xml',
    'Dockerfile',
    'docker-compose.yml',
    'docker-compose.staging.yml',
    'docker-compose.production.yml',
    '.env.example',
    'README.md',
    '.github/workflows/ci-cd.yml'
)

foreach ($file in $requiredFiles) {
    if (-not (Test-Path -LiteralPath $file)) {
        throw "Arquivo obrigatório ausente: $file"
    }
    Write-Output "[OK] $file"
}

$testCount = (Get-ChildItem -Path 'src/test' -Recurse -Filter '*.java').Count
if ($testCount -lt 1) {
    throw 'Nenhum teste automatizado foi encontrado.'
}

Write-Output "[OK] $testCount arquivos de teste automatizado encontrados"
Write-Output '[OK] Estrutura mínima do desafio validada'
