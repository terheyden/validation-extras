#!/usr/bin/env bash

mvn clean

# Just in case.
cd ..
rm validation-extras.zip
zip -r validation-extras.zip validation-extras
cd validation-extras

# Copy Validations.java to Validators.java:
validir=validation-extras/src/main/java/com/terheyden/valid
cp $validir/Validations.java $validir/Validators.java
perl -i -wlpE 's/ Validations/ Validators/g' $validir/Validators.java

validir=validation-extras/src/test/java/com/terheyden/valid
cp $validir/ValidationsTest.java $validir/ValidatorsTest.java
perl -i -wlpE 's/ValidationsTest/ValidatorsTest/g' $validir/ValidatorsTest.java
perl -i -wlpE 's/ Validations/ Validators/g' $validir/ValidatorsTest.java

# Copy jakarta -> javax:
rm -rf javax-validation-extras/src
cp -R validation-extras/src javax-validation-extras

# -i = replace in-place (don't make backups)
# -wlpE = warnings, chomp newlines, print each line, Extended regex

find javax-validation-extras/src -iname '*.java' \
    -exec perl -i -wlpE 's/import jakarta/import javax/g' "{}" \;

# Update directories.
# mv javax-validation-extras/src/main/resources/META-INF/services/jakarta.validation.ConstraintValidator javax-validation-extras/src/main/resources/META-INF/services/javax.validation.ConstraintValidator
