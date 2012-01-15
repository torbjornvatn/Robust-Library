#!/bin/bash 

JAVAEXE=java 
CLASSPATH=./lib/xstream-1.3.1.jar:./lib/log4j-1.2.15.jar:./lib/bluecove-2.1.0.jar:./lib/pccomm.jar:./build/robustpc.jar:./src/log4j.properties:./src/robustcfg.xml

$JAVAEXE -classpath $CLASSPATH robust.pc.examples.SystemEx

# $JAVAEXE -classpath $CLASSPATH robust.pc.examples.MoveEx

