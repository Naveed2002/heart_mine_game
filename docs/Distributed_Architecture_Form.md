# CIS045-3 Distributed Service Architectures – AY 25/26

**Student Name:** N M Naveeed  
**Student Number:** 2541296  
**Date:** March 2026 (Week 8)

---

### Software Architecture (Low Coupling / High Cohesion)
**How is your code structured? (e.g., components, files, libraries, classes, packages …)**

I organized the code into a package called `game`. I kept it clean by giving each file a specific job. For example, `Player.java` only handles where the character is, and `ScoreManager.java` only tracks points and lives. This is called "High Cohesion." I also made sure the files don't depend too much on each other ("Low Coupling") by using `KeyHandler.java` as a middleman for keyboard controls.

### Event Driven Architectures
**What in your code triggers events (e.g., GUI, buttons, timeout …)?**

The game waits for things to happen before it does anything. Clicking the "Start Digging" button on the menu starts the game. Pressing the arrow keys or Space bar on your keyboard triggers movement and digging. I also used Timers to create gravity—every 500ms, a "timer event" tells the game to pull the player down if they aren't standing on something.

### Interoperability 
**You are expected to use the API available via marcconrad.com/uob/heart/api.php**  
**What protocol do you use (e.g., JSON, base64)?**

I use the **JSON** protocol to talk to the Heart API. My code connects to the internet, gets a JSON response from the server, and then reads that data to find the URL for the puzzle image and the correct answer.

### Virtual Identity
**How did you implement virtual identity in your code? (e.g., Passwords, Cookies, IP Numbers …)**

I used a **Player Name** system. When you start the game, you type in your name. That name follows you throughout the game and shows up at the top of the screen next to your score. It basically gives the player a "virtual name" inside the game world.

### Any other interesting features: 

1. **Mac Support**: I added special code so the game shows a nice Miner icon in the Mac Dock area and can play MP3 sounds using the Mac's built-in sound system.
2. **Revive Puzzle**: If you lose all your lives, the game pulls a puzzle from a website. If you solve it correctly, you get a "second chance" and your lives are reset.
3. **Folders**: I moved all the images and sounds into the code folders so that the game works perfectly in Eclipse without any missing file errors.
