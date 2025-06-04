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

# Use the same JDK release for compiling the tests as we do for the main
# sources. This avoids "release version ... not supported" errors on older
# environments.
JAVA_VERSION=$(javac -version 2>&1 | sed -E 's/.* ([0-9]+).*/\1/')

javac --release "$JAVA_VERSION" --enable-preview \
      -cp "$JUNIT_JAR:$SCRIPT_DIR/out" -d "$SCRIPT_DIR/out" \
      $(find test/java -name '*.java')
java --enable-preview -jar "$JUNIT_JAR" --class-path "$SCRIPT_DIR/out" --scan-class-path

