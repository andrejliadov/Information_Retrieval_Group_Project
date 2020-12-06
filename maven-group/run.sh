#!/bin/sh

sudo mvn package

java -Xmx4g -jar target/maven-group-0.0.1-SNAPSHOT.jar
