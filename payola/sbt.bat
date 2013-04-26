set SCRIPT_DIR=%~dp0
java -Xmx1024M -XX:MaxPermSize=256M -Xss2M -jar "%SCRIPT_DIR%sbt-launch.jar" %*