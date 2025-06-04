#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Ensure main classes are built
"$SCRIPT_DIR/build.sh"

JUNIT_VERSION="1.10.1"
JUNIT_JAR="junit-platform-console-standalone.jar"

if [ ! -f "$JUNIT_JAR" ]; then
  curl -L -o "$JUNIT_JAR" "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/${JUNIT_VERSION}/junit-platform-console-standalone-${JUNIT_VERSION}.jar"
fi

javac --release 21 --enable-preview -cp "$JUNIT_JAR:$SCRIPT_DIR/out" -d "$SCRIPT_DIR/out" $(find packages/compiler-java/src/test/java -name '*.java')
java --enable-preview -jar "$JUNIT_JAR" --class-path "$SCRIPT_DIR/out" --scan-class-path

