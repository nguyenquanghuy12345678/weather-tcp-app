@echo off
setlocal
echo Running ServerControl UI
where mvn >nul 2>nul
if %ERRORLEVEL%==0 (
  mvn -Dexec.mainClass="com.weatherapp.ui.ServerControlUI" org.codehaus.mojo:exec-maven-plugin:3.1.0:exec
) else (
  echo Maven not found. Please install Maven and add it to PATH, or run from your IDE.
  echo See: https://maven.apache.org/install.html
)
endlocal
