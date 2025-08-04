# Build Environment, eg. dev, prod, qa, defaults to dev
param (
  [Parameter(Mandatory = $False)]
  [ValidateSet('dev', 'qa', 'prod')]
  [string]$config = 'dev'
)


Write-Host "-------------------------------"
Write-Host "Building for " -NoNewline
Write-Host $config.ToUpper() -ForegroundColor Green -NoNewline
Write-Host " environment"
Write-Host "-------------------------------"

if ($config -eq 'dev') {
  (npx ng build --configuration develop)
}
else {
  (npx ng build --configuration $config)
}

if ($?) {
  Write-Host "Build Successful" -ForegroundColor Green
}
else {
  Write-Host "Build Failed" -ForegroundColor Red
}

if (Test-Path "dist/etrack-analyst-portal-ui") {
  Write-Host "`nCopying .htaccess into build output directory..."
  Copy-Item .\.htaccess .\dist\etrack-analyst-portal-ui\
  Write-Host "Copy successful`n" -ForegroundColor Green
}
else {
  Write-Host "`nUnable to find the build directory" -ForegroundColor Red
  Break
}

Write-Host "-------------------------------------"
Write-Host "Creating archive for " -NoNewline
Write-Host $config.ToUpper() -ForegroundColor Green -NoNewline
Write-Host " environment"
Write-Host "-------------------------------------"

$FolderName = "./dist/etrack-analyst-portal-ui";
$zipFileName = "./dist/etrack-analyst-portal-ui-$config-$(Get-Date -Format "yyyyMMddHHmm").zip";
Write-Host "Creating $zipFileName"
[string]$Zip = "C:\Program Files\7-zip\7z.exe"; #path to 7Zip executable
if (Test-Path $Zip) {
  [array]$arguments = @("a", "-tzip", "-y", $zipFileName) + $FolderName
  & $Zip $arguments ;
}
else {
  Write-Error "7zip not found at default location. $Zip"
  exit 1;
}
Write-Host "Archive successfully created.`n" -ForegroundColor Green

Write-Output (Get-Item $zipFileName).FullName
