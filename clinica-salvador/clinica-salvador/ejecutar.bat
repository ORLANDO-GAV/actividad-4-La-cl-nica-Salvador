@echo off
chcp 65001 >nul
rem Compila y ejecuta la demo SIN Maven (solo requiere JDK 17+). Windows.
if exist out rmdir /s /q out
mkdir out
dir /s /b src\main\*.java > fuentes.txt
javac -encoding UTF-8 -d out @fuentes.txt
del fuentes.txt
java -Dfile.encoding=UTF-8 -cp out com.cipa.clinica.Main
