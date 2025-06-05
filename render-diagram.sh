#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PUML_FILE="$SCRIPT_DIR/diagram.puml"

if [ ! -f "$PUML_FILE" ]; then
  echo "diagram.puml not found. Run ./run.sh first." >&2
  exit 1
fi

PLANTUML_VERSION="1.2024.5"
PLANTUML_JAR="$SCRIPT_DIR/plantuml.jar"

if [ ! -f "$PLANTUML_JAR" ]; then
  curl -L -o "$PLANTUML_JAR" "https://repo1.maven.org/maven2/net/sourceforge/plantuml/plantuml/${PLANTUML_VERSION}/plantuml-${PLANTUML_VERSION}.jar"
fi

java -jar "$PLANTUML_JAR" -tpng "$PUML_FILE"
