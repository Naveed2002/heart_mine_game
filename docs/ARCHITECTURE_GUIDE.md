# Heart Mining Game: Architecture & Design Guide

This guide explains how I designed the Heart Mining Game and why the code is structured this way.

## 1. High Cohesion
This means each file in the project has one clear job and doesn't try to do everything at once.

[ScoreManager.java](file:///Users/nawaznaveed/Documents/HeartMiningGame/src/game/ScoreManager.java) (Lines 9-76): This file only cares about the player's name, their score, and how many hearts they have left.

[Player.java](file:///Users/nawaznaveed/Documents/HeartMiningGame/src/game/Player.java) (Lines 8-109): Everything here is about the character—where they are standing, which way they are looking, and if they are currently digging.

[Block.java](file:///Users/nawaznaveed/Documents/HeartMiningGame/src/game/Block.java) (Lines 8-181): This handles the single squares on the map. It knows if a block is stone, gold, or a bomb, and it knows how to draw itself.

## 2. Low Coupling
This means the files are independent. If I change how the keyboard works, I shouldn't have to rewrite the whole game.

[KeyHandler.java](file:///Users/nawaznaveed/Documents/HeartMiningGame/src/game/KeyHandler.java) (Lines 6-36): This file just listens for key presses. It doesn't know the rules of the game; it just tells the GamePanel when the user hits a key.

[GamePanel.java](file:///Users/nawaznaveed/Documents/HeartMiningGame/src/game/GamePanel.java) (Lines 157-193): The GamePanel acts as a middleman. It checks if the player is allowed to move to a certain spot before letting the Player class update its position.

## 3. Virtual Identity (Abstraction)
This is about separating the "data" from the "visuals."

[Block.java](file:///Users/nawaznaveed/Documents/HeartMiningGame/src/game/Block.java) (Lines 9-11): The "identity" of a block is just a piece of data (like STONE or GOLD). This exists in the computer's memory even if you can't see it yet.

[Block.java](file:///Users/nawaznaveed/Documents/HeartMiningGame/src/game/Block.java) (Lines 92-180): The actual picture you see on the screen is just a visual representation of that data. The code "looks" at the data and decides which image to draw.

## 4. Event-Driven Programming
The game doesn't just run in a straight line; it reacts when things happen.

[MainGame.java](file:///Users/nawaznaveed/Documents/HeartMiningGame/src/game/MainGame.java) (Lines 103-114): The game stays on the menu screen and waits for the user to click the "Start" button. This is a "Click Event."

[GamePanel.java](file:///Users/nawaznaveed/Documents/HeartMiningGame/src/game/GamePanel.java) (Lines 40, 556-561): I used timers for gravity. Every half a second, the game "wakes up" due to a timer event and checks if the player needs to fall down.

## 5. Interoperability
This is about making the game work with other systems and computers.

[HeartAPI.java](file:///Users/nawaznaveed/Documents/HeartMiningGame/src/game/HeartAPI.java) (Lines 17-75): The game can talk to a website to get puzzles. It uses standard formats like HTTP and JSON so it can communicate with servers anywhere.

[GamePanel.java](file:///Users/nawaznaveed/Documents/HeartMiningGame/src/game/GamePanel.java) (Lines 596-622): To play sounds on a Mac, the game uses a system command called "afplay." This allows the Java program to use the computer's built-in tools.

[MainGame.java](file:///Users/nawaznaveed/Documents/HeartMiningGame/src/game/MainGame.java) (Lines 14-30): The game uses a special Java tool to put a custom Miner icon in the Mac Dock, making it feel like a real professional app.

---
Created for Heart Mining Game Software Architecture Review.
