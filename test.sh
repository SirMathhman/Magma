#!/bin/bash
set -e

JUNIT_JAR=junit-platform-console-standalone.jar
if [ ! -f "$JUNIT_JAR" ]; then
  curl -L -o "$JUNIT_JAR" https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar
fi

mkdir -p out
# Compile all Java files from src/ and test/ directories
javac -d out -cp "$JUNIT_JAR" $(find src test -name '*.java')

# Generate diagram and TypeScript stubs before running tests
java -cp out magma.GenerateDiagram

java -jar "$JUNIT_JAR" --class-path out:"$JUNIT_JAR" --scan-class-path
