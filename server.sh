#!/bin/sh
if [ -e ./projects/fileserver/build/install ]
then
  ./projects/fileserver/build/install/fileserver/bin/fileserver $@
else
  echo "Run ./gradlew build first!"
  exit 1
fi