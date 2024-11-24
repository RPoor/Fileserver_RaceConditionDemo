#!/bin/sh
if [ -e ./build/attacker/bin/attacker ]
then
  ./build/attacker/bin/attacker $@
else
  echo "Run ./gradlew build first!"
  exit 1
fi