# Software Design Principles in Heart Mining Game

This document briefly outlines how the core architectural principles of Software Engineering are applied within the **Heart Mining Game** codebase.

---

## 🏗️ 1. High Cohesion
**Cohesion** refers to how focused a single class or module is on a specific task.
*   **Player.java**: This class has high cohesion because its ONLY responsibility is managing the miner's state (location, direction, animation). It does not handle scoring or map generation.
*   **ScoreManager.java**: Dedicated solely to tracking points, player lives, and rendering the HUD text. This separation ensures that if the scoring rules change, only this class needs to be modified.
*   **Block.java**: Represents a single cell in the grid. It manages its own textures and "dug" state independently.

## 🔗 2. Low Coupling
**Coupling** refers to how much classes depend on each other. We aim for **Low Coupling** so that changing one class doesn't break everything else.
*   **KeyHandler.java**: Instead of putting keyboard logic inside `GamePanel`, we use a separate listener. The `GamePanel` doesn't need to know *how* keys work; it just receives specific method calls (`movePlayer`, `dig`) when an event occurs.
*   **Independent Entities**: The `Player` class does not have a reference to the `Block` array. Instead, the `GamePanel` acts as a "Mediator," calculating collisions and passing only the necessary data down.

## 👥 3. Virtual Identity
In object-oriented design, **Virtual Identity** (Abstraction) ensures that the "Logical State" of an object is separate from its "Physical Representation."
*   **Logic vs. Visuals**: In `Block.java`, the "identity" of the block is defined by its `Type` (STONE, GOLD, etc.) and `isDug` boolean. These variables are the **Virtual Identity** in memory. The actual pixels (PNG images) drawn on the screen are just a visual projection of that identity.
*   **Facing Directions**: The `Player` tracks a logical `facingRight` boolean. Even if the image flipped on screen is transient, the player's identity as a "miner looking left" remains consistent in the data.

## ⚡ 4. Event-Driven Programming
Our game reactive to inputs and internal triggers rather than running in a forced linear loop.
*   **Input Events**: The game uses Java's **Event Dispatch Thread (EDT)**. Every time a user presses a key, an "Event" is fired to the `KeyHandler`, which triggers logic. The game "waits" for these events.
*   **Timer Events**: In `GamePanel`, we use `javax.swing.Timer`. Instead of a while-loop that eats up 100% CPU, the game sleeps and only "wakes up" to process specific tasks (like finishing a digging animation or updating gravity) when the Timer fires an `ActionEvent`.

## 🔌 5. Interoperability
**Interoperability** is the ability of a system to work with other products or systems, at present or in the future, without any restricted access or implementation.
*   **Web API Integration**: The game features a "Revive Puzzle" system that communicates with an external server via `HeartAPI.java`. Since it uses standard JSON and HTTP/REST protocols, our Java application can "interoperate" with web-based services regardless of the language they are written in.
*   **Asset Standardization**: By using standard `.png` for images and `.wav` for audio, the game can easily integrate assets created in diverse external tools (Photoshop, Audacity, etc.), ensuring the software is compatible with industry-standard media formats.
*   **System Libraries**: The game utilizes the standard `javax.swing` and `java.awt` packages. This allows it to run seamlessly on different operating systems (Mac, Windows, Linux) because it relies on the universal Java Runtime Environment (JRE).

---
*Created for Heart Mining Game Software Architecture Review.*
