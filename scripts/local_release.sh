#!/usr/bin/env bash

set -e

export MAVEN_OPTS="$MAVEN_OPTS -Xmx2048m"
export JAVA_OPTS="$JAVA_OPTS -Xmx2048m"
# -agentpath:/Applications/jprofiler8/bin/macos/libjprofilerti.jnilib=port=25000
export JAVA_HOME=`/usr/libexec/java_home -v 1.8`
echo
java -version
echo
./mvnw -version
echo

export GPG_TTY=$(tty)

./mvnw release:clean -P release -Drelease.arguments="-Dmaven.test.skip=true -DskipTests=true" && \
./mvnw release:prepare -P release -Drelease.arguments="-Dmaven.test.skip=true -DskipTests=true" && \
./mvnw release:perform -P release -Drelease.arguments="-Dmaven.test.skip=true -DskipTests=true"