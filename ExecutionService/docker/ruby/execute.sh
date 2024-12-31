#!/bin/bash

# Check and create temporary directory
if [ ! -d "/app/tmp" ]; then
    mkdir /app/tmp
fi
cd /app/tmp

# Save the input Ruby code into a file
echo "$1" > script.rb

# Run the Ruby code
ruby script.rb
