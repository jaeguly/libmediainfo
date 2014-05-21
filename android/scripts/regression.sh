#!/bin/bash

MEDIAINFO_CLI=./mediainfo_0.7.67
VALIDATE_SET=../validates
TMP_PATH_PREFIX=/tmp/mediainfo-regression

TEST_SET=$1


if [ ! -e "$TEST_SET" ]; then
    echo "need to specify a test set dir."
    exit 0;
fi


filter_line() {
    SRC_FILE=$1
    DST_FILE=$2

    cat $SRC_FILE | grep -v -e "^#" -e "Complete name" -e "Folder name" -e "File last modification date" |grep [a-zA-Z0-9] > $DST_FILE
}

if [ -e "$TMP_PATH_PREFIX" ]; then
    rm -f $TMP_PATH_PREFIX
fi


if [ -d $TEST_SET ]; then
    find $TEST_SET -type f | while read f ; do
        $MEDIAINFO_CLI -f "$f" > $TMP_PATH_PREFIX.cur
        FILENAME=$(basename "$f")

        find $VALIDATE_SET -name "$FILENAME*.info" | while read v ; do
            echo "$f" >> $TMP_PATH_PREFIX
            cat "$v" > $TMP_PATH_PREFIX.val

            filter_line $TMP_PATH_PREFIX.val $TMP_PATH_PREFIX.val2
            filter_line $TMP_PATH_PREFIX.cur $TMP_PATH_PREFIX.cur2

            diff -Nurbd $TMP_PATH_PREFIX.val2 $TMP_PATH_PREFIX.cur2 >> $TMP_PATH_PREFIX
            exit 0;
        done
    done
fi

if [ -f $TEST_SET ]; then
    $MEDIAINFO_CLI -f "$TEST_SET" > $TMP_PATH_PREFIX.info
    FILENAME=$(basename "$TEST_SET")

    find $VALIDATE_SET -name "$FILENAME*.info" | while read f ; do
        echo "$f" >> $TMP_PATH_PREFIX
        cat "$f" > $TMP_PATH_PREFIX.val

        filter_line $TMP_PATH_PREFIX.val $TMP_PATH_PREFIX.val2
        filter_line $TMP_PATH_PREFIX.cur $TMP_PATH_PREFIX.cur2

        diff -Nurbd $TMP_PATH_PREFIX.val2 $TMP_PATH_PREFIX.cur2 >> $TMP_PATH_PREFIX
    done
fi


