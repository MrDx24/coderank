#!/bin/bash

# Check and create temporary directory
if [ ! -d "/app/tmp" ]; then
    mkdir /app/tmp
fi
cd /app/tmp

# Save the input C/C++ code into a file
echo "$1" > program.cpp  # You can change this to .c for pure C code

# Compile the C/C++ code
g++ program.cpp -o program 2> compile_errors.txt
if [ $? -ne 0 ]; then
    echo "Error: Compilation failed."
    cat compile_errors.txt
    exit 1
fi

# Run the compiled program
./program
