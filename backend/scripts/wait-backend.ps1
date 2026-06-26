$maxAttempts = 60
$attempt = 0
$url = "http://localhost:8080/actuator/health"
while ($attempt -lt $maxAttempts) {
    $attempt++
    try {
        $r = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 2
        if ($r.StatusCode -eq 200) {
            Write-Host "Backend ready after $attempt attempt(s)"
            exit 0
        }
    } catch {
        Write-Host "Attempt $attempt : $($_.Exception.Message)"
    }
    Start-Sleep -Seconds 2
}
Write-Host "Backend NOT ready after $maxAttempts attempts (120s)"
exit 1
