@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-23"
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo JAVA_HOME=%JAVA_HOME%
java -version
echo.
echo === Building E-Commerce Microservices ===
echo.
call "C:\Users\HP\.vscode\extensions\oracle.oracle-java-25.0.1\nbcode\java\maven\bin\mvn.cmd" clean install -DskipTests -B
