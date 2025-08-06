@echo off
echo Running CharTypeTest...
java -cp "lib\junit-platform-console-standalone-1.9.2.jar;src\main\java;src\test\java" org.junit.platform.console.ConsoleLauncher --select-class=CharTypeTest
echo Test execution completed.