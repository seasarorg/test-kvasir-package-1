#!/bin/sh

JETTY_HOME=`dirname $0`/..
JETTY_LOGS=$JETTY_HOME/logs
JAVA_OPTS='-Xmx128M -Xms128M -Xss128K -Djava.awt.headless=true'
JAVA_OPTS=''

java $JAVA_OPTS $JETTY_OPTS -Djetty.home=$JETTY_HOME -Djetty.logs=$JETTY_LOGS -jar $JETTY_HOME/start.jar >> $JETTY_LOGS/jetty-stdout.txt 2>&1 &
