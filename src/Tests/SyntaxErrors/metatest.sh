#!/bin/bash
#export SCRATCHDIR=`mktemp -d`
{
if [ -f ${TESTDIR}/$1.tape ]; then
    ./runtm "${TESTDIR}/$1.tm" "${TESTDIR}/$1.tape" 
    export RESULT=$?
else
    ./runtm "${TESTDIR}/$1.tm"
    export RESULT=$?
fi
if [[ x$RESULT != x$2 ]]; then exit 1; fi
} |
grep -ve '^[[:space:]]' |
diff -w - "${TESTDIR}/$1.tmout"
export RESULT=$?
rm -rf $SCRATCHDIR
exit $RESULT
