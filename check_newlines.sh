#!/bin/bash

# Find all Kotlin, Java, XML, and Gradle files
find . -type f \( -name "*.kt" -o -name "*.java" -o -name "*.xml" -o -name "*.gradle" -o -name "*.gradle.kts" \) -not -path "./build/*" -not -path "./.gradle/*" -not -path "./app/build/*" -not -path "*/build/*" | while read -r file; do
    # Check if file exists and is readable
    if [ -r "$file" ]; then
        # Check if the last character is a newline
        if [ -s "$file" ] && [ "$(tail -c 1 "$file" | wc -l)" -eq 0 ]; then
            echo "$file"
        fi
    fi
done