#!/bin/bash
set -e
JUNIT_JAR=junit-platform-console-standalone.jar
if [ ! -f "$JUNIT_JAR" ]; then
  curl -L -o "$JUNIT_JAR" \
    https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.1/junit-platform-console-standalone-1.10.1.jar
fi
mkdir -p bin
find src/main/java src/test/java -name "*.java" | xargs javac -cp "$JUNIT_JAR" -d bin
