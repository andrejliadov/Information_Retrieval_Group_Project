#!/bin/sh

sudo mvn package

# Old commands
# cd target
# java -Xmx4g -jar maven-group-0.0.1-SNAPSHOT.jar

java -Xmx4g -jar target/maven-group-0.0.1-SNAPSHOT.jar
