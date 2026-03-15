# CIS045-3 Distributed Service Architectures – AY 25/26
**Student Name:** [Your Name]  
**Student Number:** [Your Number]  
**Student Signature:** ____________________  
**Date:** March 2026 (Week 8)

---

### Software Architecture (Low Coupling / High Cohesion)
**How is your code structured? (e.g., components, files, libraries, classes, packages …)**

The project is structured using a **Modular Component-Based Architecture** in Java. It is organized into a single `game` package containing specialized classes:
*   **GamePanel.java**: Acts as the central **Mediator**. It coordinates between the player, the map, and the scoring system.
*   **Player.java & Block.java**: Independent state-management classes (High Cohesion). They manage their own internal data and rendering logic without direct dependencies on each other (Low Coupling).
*   **ScoreManager.java**: A dedicated component for HUD rendering and status tracking (Lives/Score).
*   **KeyHandler.java**: A decoupled input listener that translates OS-level keystrokes into game-specific actions.
*   **Libraries**: Utilizes `javax.swing` for GUI components and `java.awt` for rendering.

---

### Event Driven Architectures
**What in your code triggers events (e.g., GUI, buttons, timeout …)?**

The game operates on a reactive, event-driven model:
*   **GUI Input Events**: Java's `KeyEvent` system triggers movement and digging actions whenever the user interacts with the keyboard.
*   **Timer Events**: `javax.swing.Timer` triggers high-frequency "Game Loop" events to handle smooth animations (digging states) and procedural updates (gravity/physics).
*   **Button ActionEvents**: The Main Menu and "Revive" claim screens use `ActionListener` to handle UI transitions and reward claiming.

---

### Interoperability
**You are expected to use the API available via marcconrad.com/uob/heart/api.php. What protocol do you use (e.g., JSON, base64)?**

The system achieves interoperability through standard web protocols:
*   **HTTP/REST**: The game communicates with the UoB Heart API using standard GET requests.
*   **JSON**: The data exchanged with the API is parsed using a JSON format. The game extracts specific fields like `question` (for images) and `solution` to drive the "Revive Puzzle" gameplay mechanic.
*   **Asset Interoperability**: The game uses standard `.png` and `.wav` file formats, allowing assets to be interoperable across different media editing platforms and OS environments.

---

### Virtual Identity
**How did you implement virtual identity in your code? (e.g., Passwords, Cookies, IP Numbers …)**

Identity is managed primarily through **Stateful Logical Abstractions**:
*   **Player Name Identity**: When the game starts, a text input captures the user's name, which becomes the primary persistent identity for the session, tracked by the `ScoreManager`.
*   **Entity State Mapping**: Each `Block` maintains a "Virtual Identity" (its hidden type vs its visible state). The game maintains the logical truth of what a block is (e.g., a hidden Gold ore) even while it is visually disguised as generic Stone.
*   **Session State**: Player lives and scores define the current "Logical Identity" of the session, which determines if the player is "Dead" or "Alive" in the game world.

---

### Any other interesting features (Ideas for continued development):
*   **Procedural Cave Generation**: Implements winding horizontal tunnels and vertical ladder shafts created via random-walk algorithms.
*   **Fog of War / Lighting**: A custom `RadialGradientPaint` system that enforces a limited field of vision around the player for a realistic underground feel.
*   **Cloud Persistence**: Future plans include a remote Leaderboard API to persist and share scores across multiple clients using the same interoperability principles.

---
*Checked and validated for Week 8 working prototype presentation.*
