#!/usr/bin/env bash
set -euo pipefail

JUNIT_VERSION=1.10.2
JUNIT_JAR="libs/junit-platform-console-standalone-${JUNIT_VERSION}.jar"

mkdir -p libs build test-classes

if [ ! -f "$JUNIT_JAR" ]; then
    echo "Downloading JUnit Platform ${JUNIT_VERSION}..."
    curl -L -o "$JUNIT_JAR" "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/${JUNIT_VERSION}/junit-platform-console-standalone-${JUNIT_VERSION}.jar"
fi

# Compile main sources
find src -name "*.java" > sources.txt
javac --release 21 --enable-preview -d build @sources.txt

# Compile test sources
find test -name "*.java" > test-sources.txt
javac --release 21 --enable-preview -cp "$JUNIT_JAR:build" -d test-classes @test-sources.txt

# Run tests
java --enable-preview -jar "$JUNIT_JAR" -cp build:test-classes --scan-class-path
