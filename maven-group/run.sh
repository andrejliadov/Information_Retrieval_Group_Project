#!/bin/sh

sudo mvn package
cd target
java -Xmx4g -jar maven-group-0.0.1-SNAPSHOT.jar
