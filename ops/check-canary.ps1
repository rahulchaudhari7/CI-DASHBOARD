$ErrorActionPreference = 'Stop'
$seen = @{ stable = 0; canary = 0 }
1..100 | ForEach-Object {
    $response = Invoke-RestMethod http://localhost:8180/api/deployment/instance
    if ($seen.ContainsKey($response.slot)) { $seen[$response.slot]++ }
}
$seen.GetEnumerator() | ForEach-Object { '{0}: {1} requests' -f $_.Key, $_.Value }
if ($seen.stable -eq 0 -or $seen.canary -eq 0) { throw 'Both stable and canary instances were not observed.' }
