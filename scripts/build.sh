#!/bin/sh
set -e
mkdir -p out
javac -cp "lib/*" -d out $(find src/main/java -name '*.java')
