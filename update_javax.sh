#!/usr/bin/env bash

mvn clean

# Just in case.
cd ..
rm validation-extras.zip
zip -r validation-extras.zip validation-extras
cd validation-extras

rm -rf javax-validation-extras/src
cp -R validation-extras/src javax-validation-extras

# -i = replace in-place (don't make backups)
# -wlpE = warnings, chomp newlines, print each line, Extended regex

find javax-validation-extras/src -iname '*.java' \
    -exec perl -i -wlpE 's/import jakarta/import javax/g' "{}" \;

