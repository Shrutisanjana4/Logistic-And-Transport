# PrimeRoute Logistics - Backend Startup Script
$STS_JDK = "C:\Users\shrut\Downloads\spring-tools\sts-5.0.0.RELEASE\plugins\org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_25.0.1.v20251108-1451\jre"
$BACKEND_DIR = if (Test-Path "$PSScriptRoot\backend\pom.xml") { "$PSScriptRoot\backend" } else { "C:\Users\shrut\Documents\workspace-spring-tools-for-eclipse-5.0.0.RELEASE\Logistics-management" }

if (Test-Path "$STS_JDK\bin\java.exe") {
    $env:JAVA_HOME = $STS_JDK
    $env:Path = "$STS_JDK\bin;" + $env:Path
    Write-Host "[PrimeRoute] Using Java from STS: $STS_JDK" -ForegroundColor Green
}

# Load local environment variables from backend/.env, frontend/.env.local, or .env.local if present
$ENV_CANDIDATES = @(
    (Join-Path $PSScriptRoot "backend\.env"),
    (Join-Path $PSScriptRoot "frontend\.env.local"),
    (Join-Path $PSScriptRoot ".env.local")
)
foreach ($cand in $ENV_CANDIDATES) {
    if (Test-Path $cand) {
        Get-Content $cand | ForEach-Object {
            $line = $_.Trim()
            if ($line -and !$line.StartsWith("#") -and $line.Contains("=")) {
                $parts = $line.Split("=", 2)
                $name = $parts[0].Trim()
                $val = $parts[1].Trim()
                [System.Environment]::SetEnvironmentVariable($name, $val, "Process")
            }
        }
        Write-Host "[PrimeRoute] Loaded environment variables from $cand" -ForegroundColor Green
        break
    }
}

Write-Host "[PrimeRoute] Starting Spring Boot server at $BACKEND_DIR..." -ForegroundColor Cyan
Write-Host "[PrimeRoute] Landing Page URL: http://localhost:5173/" -ForegroundColor Green
& "$BACKEND_DIR\mvnw.cmd" -f "$BACKEND_DIR\pom.xml" spring-boot:run
