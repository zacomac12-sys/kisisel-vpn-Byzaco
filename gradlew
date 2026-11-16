#!/usr/bin/env sh
##############################################################################
# Gradle start up script for UN*X
##############################################################################
set -e
APP_HOME="$(CDPATH='' cd -- "$(dirname -- "$0")" && pwd -P)"
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

if [ -n "$JAVA_HOME" ] ; then
    JAVACMD="$JAVA_HOME/bin/java"
    if [ ! -x "$JAVACMD" ] ; then
        echo "JAVA_HOME=$JAVA_HOME geçersiz." >&2; exit 1
    fi
else
    JAVACMD="java"
    if ! command -v java > /dev/null 2>&1 ; then
        echo "java bulunamadı." >&2; exit 1
    fi
fi

exec "$JAVACMD" \
  -classpath "$CLASSPATH" \
  org.gradle.wrapper.GradleWrapperMain "$@"
