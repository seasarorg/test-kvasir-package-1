@echo off

@setlocal

set JAVA_OPTS=-Xmx128M -Xms128M -Xss128K -XX:MaxPermSize=128M -Djava.awt.headless=true

if "%@eval[2+2]"=="4" goto setup4NT

set CMD_LINE_ARGS=%*
goto doneStart

:setup4NT
set CMD_LINE_ARGS=%$

:doneStart

set EXCLAM=!
if "!CD!"=="%EXCLAM%CD%EXCLAM%" goto callScript

set JAVA=java.exe
if not "%JAVA_HOME%" == "" set JAVA=%JAVA_HOME%\bin\java.exe

pushd %~dp0

set KVASIR_HOME=%CD%

set LOCALCLASSPATH=base\classes
for %%i in ("base\lib\*.jar") do set LOCALCLASSPATH=!LOCALCLASSPATH!;%%i

echo "%JAVA%" %JAVA_OPTS% %KVASIR_OPTS% -Dsystem.home.dir="%KVASIR_HOME%" -cp "%LOCALCLASSPATH%" %CMD_LINE_ARGS% org.seasar.kvasir.base.server.Bootstrap
"%JAVA%" %JAVA_OPTS% %KVASIR_OPTS% -Dsystem.home.dir="%KVASIR_HOME%" -cp "%LOCALCLASSPATH%" %CMD_LINE_ARGS% org.seasar.kvasir.base.server.Bootstrap

popd

goto end

:callScript

cmd /V:on /C %~f0 %CMD_LINE_ARGS%

:end

@endlocal
