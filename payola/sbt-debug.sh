java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005  -Xmx12G -XX:MaxPermSize=512M -Xss2M -jar `dirname $0`/sbt-launch.jar "$@"
