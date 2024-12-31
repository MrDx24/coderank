#!/bin/bash

# Check and create temporary directory
if [ ! -d "/app/tmp" ]; then
    mkdir /app/tmp
fi
cd /app/tmp

# Extract the public class name from the Java code
CLASS_NAME=$(echo "$1" | grep -o "public class [a-zA-Z0-9_]*" | cut -d' ' -f3)

if [ -z "$CLASS_NAME" ]; then
    echo "Error: No public class found in the code."
    exit 1
fi

# Save the input Java code into a file with the correct name
echo "$1" > "${CLASS_NAME}.java"

# Compile the Java code
javac "${CLASS_NAME}.java" 2> compile_errors.txt
if [ $? -ne 0 ]; then
    echo "Error: Compilation failed."
    cat compile_errors.txt
    exit 1
fi

# Run the Java code
java "$CLASS_NAME"
