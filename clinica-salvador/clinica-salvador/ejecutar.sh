#!/usr/bin/env bash
# Compila y ejecuta la demo SIN Maven (solo requiere JDK 17+). Linux/macOS/Git Bash.
set -e
rm -rf out && mkdir out
javac -encoding UTF-8 -d out $(find src/main -name "*.java")
java -Dfile.encoding=UTF-8 -cp out com.cipa.clinica.Main
