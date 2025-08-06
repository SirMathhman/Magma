@echo off
echo Running tests...
java -cp "lib\junit-platform-console-standalone-1.9.2.jar;src\main\java;src\test\java" org.junit.platform.console.ConsoleLauncher --select-class=MainTest
echo Test execution completed.