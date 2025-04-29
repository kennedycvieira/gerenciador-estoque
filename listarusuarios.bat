@echo off
echo SQLite User Reader - Compile and Run Script
echo --------------------------------------------

rem Check if the SQLite JDBC driver exists
if not exist sqlite-jdbc*.jar (
    echo ERROR: SQLite JDBC driver not found!
    echo Please download the SQLite JDBC driver from:
    echo https://github.com/xerial/sqlite-jdbc/releases
    echo and place it in the same directory as this script.
    pause
    exit /b 1
)

rem Get the name of the SQLite JDBC JAR file
for %%f in (sqlite-jdbc*.jar) do set SQLITE_JAR=%%f
echo Found SQLite JDBC driver: %SQLITE_JAR%

rem Compile the Java source file
echo.
echo Compiling SQLiteUserReader.java...
javac -cp ".;%SQLITE_JAR%" SQLiteUserReader.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!

rem Run the compiled program
echo.
echo Running SQLiteUserReader...
echo.
java -cp ".;%SQLITE_JAR%" SQLiteUserReader

echo.
echo Program execution completed.
pause