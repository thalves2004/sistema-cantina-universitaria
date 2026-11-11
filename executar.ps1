# Execute este script no PowerShell a partir da pasta do projeto.
$env:CANTINA_DB_USER = Read-Host "Usuario MySQL [root]"
if ([string]::IsNullOrWhiteSpace($env:CANTINA_DB_USER)) { $env:CANTINA_DB_USER = "root" }
$senha = Read-Host "Senha MySQL (deixe vazio se nao houver)" -AsSecureString
$ptr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($senha)
try { $env:CANTINA_DB_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ptr) }
finally { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr) }
mvn clean compile
if ($LASTEXITCODE -eq 0) { mvn exec:java }
