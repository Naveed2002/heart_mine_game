# ⛏️ Heart Mining Game

A professional Java Swing mining game with integrated API support, persistent data, and custom macOS features.

## 📂 Project Structure

This project follows a clean, modular structure organized by functionality.

```text
HeartMiningGame/
├── src/                # Source code
│   └── game/           # Main game package
│       ├── res/        # 🎥 Game assets (images, sounds, mp3)
│       └── *.java      # 🏗️ Core logic and engine files
├── docs/               # 📄 Architectural & Academic documentation
├── bin/                # ⚙️ Compiled binaries (generated)
├── run.sh              # 🚀 One-click launch script
└── player_data.json    # 💾 Persistent user save data
```

### 📦 Key Components
- **Game Engine**: `GamePanel.java` handles physics and rendering.
- **Persistence**: `ConfigManager.java` handles JSON save states.
- **Interoperability**: `HeartAPI.java` connects to external REST services.
- **Resources**: `src/game/res/` contains high-quality assets.

## 🚀 How to Run

### Command Line
1. Open your terminal in the project folder.
2. Run `./run.sh`.

### Eclipse IDE
1. Import the project into your workspace.
2. Right-click `src/game/MainGame.java`.
3. Select **Run As > Java Application**.

---
*Developed for CIS045-3 Distributed Service Architectures.*
