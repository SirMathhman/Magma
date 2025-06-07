#!/bin/bash
set -e
DIR=$(dirname "$0")
"$DIR/build.sh"
java -jar junit-platform-console-standalone.jar -cp bin --scan-classpath
