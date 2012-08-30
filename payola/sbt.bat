set SCRIPT_DIR=%~dp0
java -Xmx1024M -XX:MaxPermSize=512M -Xss2M -jar "%SCRIPT_DIR%sbt-launch.jar" %*