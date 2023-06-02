#!/usr/bin/env bash

mvn clean

# Just in case.
cd ..
rm validation-extras.zip
zip -r validation-extras.zip validation-extras
cd validation-extras || exit 1

# Copy jakarta -> javax:
rm -rf javax-validations2/src
cp -R jakarta-validations3/src javax-validations2

# -i = replace in-place (don't make backups)
# -wlpE = warnings, chomp newlines, print each line, Extended regex

find javax-validations2/src -iname '*.java' \
    -exec perl -i -wlpE 's/import jakarta/import javax/g' "{}" \;
