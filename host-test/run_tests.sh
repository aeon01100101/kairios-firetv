#!/bin/bash
# Host-test the pure boot routing logic with a plain JVM.
set -e
cd "$(dirname "$0")"
JDK="$HOME/workspace/java/jdk-17.0.20.1+1"
SRC="../src/app/src/main/java/com/kairios/firetv/BootLogic.java"
rm -rf out && mkdir -p out
"$JDK/bin/javac" -encoding UTF-8 -d out "$SRC" TestBootLogic.java
"$JDK/bin/java" -cp out TestBootLogic
