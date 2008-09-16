@echo off

@setlocal

if "%eval[2+2]" == "4" goto 4NTArgs

set CMD_LINE_ARGS=%*
goto endInit

:4NTArgs
set CMD_LINE_ARGS=%$

:endInit

if not "%CMD_LINE_ARGS%" == "" goto passArgs
set CMD_LINE_ARGS=install

:passArgs

set MAVEN_OPTS=-Xms128m -Xmx128m -XX:MaxPermSize=128m

REM pushd PASS1-skirnir-project
REM call mvn %CMD_LINE_ARGS%
REM popd
REM if "%ERRORLEVEL%" == "0" goto pass1succeed
REM exit /B %ERRORLEVEL%
REM :pass1succeed

REM pushd PASS2-cms-project
REM call mvn %CMD_LINE_ARGS%
REM popd
REM if "%ERRORLEVEL%" == "0" goto pass2succeed
REM exit /B %ERRORLEVEL%
REM :pass2succeed

pushd project
call mvn %CMD_LINE_ARGS%
popd
if "%ERRORLEVEL%" == "0" goto pass3succeed
exit /B %ERRORLEVEL%
:pass3succeed

REM pushd PASS4-ymir
REM call mvn %CMD_LINE_ARGS%
REM popd
REM if "%ERRORLEVEL%" == "0" goto pass4succeed
REM exit /B %ERRORLEVEL%
REM :pass4succeed

pushd plugin\plugin-project
call mvn %CMD_LINE_ARGS%
popd
if "%ERRORLEVEL%" == "0" goto pass5succeed
exit /B %ERRORLEVEL%
:pass5succeed

pushd distribution\distribution-project
call mvn %CMD_LINE_ARGS%
popd
if "%ERRORLEVEL%" == "0" goto pass6succeed
exit /B %ERRORLEVEL%
:pass6succeed

@endlocal

:end
