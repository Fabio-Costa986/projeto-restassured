param(
    [string]$cmd = "test",
    [string]$groups = ""
)

$wrapper = ".mvn\wrapper\maven-wrapper.jar"
$base = "-Dmaven.multiModuleProjectDirectory=$(Get-Location)"

switch ($cmd) {
    "test"       { $goal = "test" }
    "smoke"      { $goal = "test"; $groups = "smoke" }
    "regression" { $goal = "test"; $groups = "regression" }
    "report"     { $goal = "allure:report" }
    "serve"      {
        java -classpath $wrapper $base org.apache.maven.wrapper.MavenWrapperMain allure:report 2>&1 | Out-Null
        Start-Process powershell -ArgumentList "-NoExit","-Command","cd 'target\site\allure-maven-plugin'; python -m http.server 8080"
        Start-Sleep -Seconds 2
        Start-Process "http://localhost:8080"
        return
    }
    default { Write-Host "Comandos: test | smoke | regression | report | serve"; return }
}

$groupsArg = if ($groups) { "-Dgroups=$groups" } else { "" }

if ($groupsArg) {
    java -classpath $wrapper $base org.apache.maven.wrapper.MavenWrapperMain $goal $groupsArg
} else {
    java -classpath $wrapper $base org.apache.maven.wrapper.MavenWrapperMain $goal
}
