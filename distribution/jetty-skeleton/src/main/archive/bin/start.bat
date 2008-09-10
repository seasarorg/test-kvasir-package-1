@echo off

@setlocal

set JETTY_HOME=%~dp0..
set JETTY_LOGS=%JETTY_HOME%\logs
set JAVA_OPTS=-Xmx128M -Xms128M -Xss128K -Djava.awt.headless=true

if "%@eval[2+2]"=="4" goto setup4NT

set CMD_LINE_ARGS=%*
goto doneStart

:setup4NT
set CMD_LINE_ARGS=%$

:doneStart

set JAVA=java.exe
if not "%JAVA_HOME%" == "" set JAVA=%JAVA_HOME%\bin\java.exe

"%JAVA%" %JAVA_OPTS% %JETTY_OPTS% -Djetty.home="%JETTY_HOME%" -Djetty.logs="%JETTY_LOGS%" -jar "%JETTY_HOME%\start.jar" %CMD_LINE_ARGS%

@endlocal
