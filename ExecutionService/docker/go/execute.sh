#!/bin/bash

# Check and create temporary directory
if [ ! -d "/app/tmp" ]; then
    mkdir /app/tmp
fi
cd /app/tmp

# Save the input Go code into a file
echo "$1" > main.go

# Run the Go code
go run main.go
