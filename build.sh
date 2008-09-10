#!/bin/sh

export MAVEN_OPTS='-Xms128m -Xmx128m -XX:MaxPermSize=128m'

CMD_LINE_ARGS=$*
if [ -z "$CMD_LINE_ARGS" ]; then CMD_LINE_ARGS='install'; fi

# pushd PASS1-skirnir-project
# mvn $CMD_LINE_ARGS
# EXITCODE=$?
# popd
# if [ $EXITCODE != 0 ]; then exit $EXITCODE; fi

# pushd PASS2-cms-project
# mvn $CMD_LINE_ARGS
# EXITCODE=$?
# popd
# if [ $EXITCODE != 0 ]; then exit $EXITCODE; fi

pushd project
mvn $CMD_LINE_ARGS
EXITCODE=$?
popd
if [ $EXITCODE != 0 ]; then exit $EXITCODE; fi

# pushd PASS4-ymir
# mvn $CMD_LINE_ARGS
# EXITCODE=$?
# popd
# if [ $EXITCODE != 0 ]; then exit $EXITCODE; fi

pushd plugin
mvn $CMD_LINE_ARGS
EXITCODE=$?
popd
if [ $EXITCODE != 0 ]; then exit $EXITCODE; fi

pushd distribution
mvn $CMD_LINE_ARGS
EXITCODE=$?
popd
if [ $EXITCODE != 0 ]; then exit $EXITCODE; fi
