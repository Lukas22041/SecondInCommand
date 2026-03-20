@echo off
cd /d "%~dp0"
echo -----------------------------------------------
echo  Second-in-Command Skill Browser
echo  http://localhost:8080
echo  Press Ctrl+C to stop
echo -----------------------------------------------
python -m http.server 8080
pause

