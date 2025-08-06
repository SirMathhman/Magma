@echo off
echo Compiling Java files...
if not exist build\classes mkdir build\classes
javac -d build\classes -cp "lib\junit-platform-console-standalone-1.9.2.jar" src\main\java\*.java src\test\java\*.java

echo Running array tests...
java -cp "lib\junit-platform-console-standalone-1.9.2.jar;build\classes" org.junit.platform.console.ConsoleLauncher --select-class=ArrayTest
echo Test execution completed.