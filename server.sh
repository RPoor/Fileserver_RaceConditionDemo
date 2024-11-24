#!/bin/sh
if [ -e ./build/fileserver/bin/fileserver ]
then
  ./build/fileserver/bin/fileserver $@
else
  echo "Run ./gradlew build first!"
  exit 1
fi