#!/bin/sh
if [ -e ./projects/attacker/build/install ]
then
  ./projects/attacker/build/install/attacker/bin/attacker $@
else
  echo "Run ./gradlew build first!"
  exit 1
fi