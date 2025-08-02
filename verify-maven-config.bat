@echo off
echo Verifying Maven configuration...

echo.
echo Project structure:
echo -----------------
dir /s /b src\java\magma\*.java
dir /s /b test\java\magma\*.java
echo.

echo Maven POM file:
echo --------------
type pom.xml | findstr /C:"<groupId>" /C:"<artifactId>" /C:"<version>" /C:"<sourceDirectory>" /C:"<testSourceDirectory>" /C:"<mainClass>"
echo.

echo Maven wrapper:
echo -------------
if exist mvnw.cmd (
    echo Maven wrapper script exists.
) else (
    echo Maven wrapper script does not exist.
)
echo.

echo .gitignore entries:
echo -----------------
type .gitignore | findstr /C:"/target/" /C:"pom.xml.tag" /C:"dependency-reduced-pom.xml"
echo.

echo README.md Maven instructions:
echo ---------------------------
type README.md | findstr /C:"Maven" /C:"mvn " /C:"mvnw.cmd"
echo.

echo Maven configuration verification complete.
echo.
echo Note: To fully test the Maven build, you would need to run:
echo   mvnw.cmd clean package
echo.
echo This script only verifies the configuration files are in place.