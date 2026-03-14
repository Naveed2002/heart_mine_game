#!/bin/bash
# Compile and run the HeartMiningGame

# Create bin directory if it doesn't exist
mkdir -p bin

# Compile Java files
echo "Compiling..."
javac -d bin -sourcepath src src/game/*.java

# Copy resources
echo "Copying resources..."
cp res/*.png bin/ 2>/dev/null || :
cp res/*.wav bin/ 2>/dev/null || :

# Check if compilation succeeded
if [ $? -eq 0 ]; then
    echo "Compilation successful! Starting game..."
    # Run the game
    java -cp bin game.MainGame
else
    echo "Compilation failed."
fi
