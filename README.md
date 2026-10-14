# Connect 4 Client-Server Game

A Java-based multiplayer Connect 4 game built with:

- **JavaFX** for the desktop client UI
- **Java sockets** for real-time client/server communication
- **Maven** for project structure and dependency management

The repository is split into two modules:

- `connectfour-server` → accepts two players, handles registration, and relays moves
- `connectfour-client` → JavaFX app with login/register screens, menu, and game board

---

## Project Structure

```text
connect4-client-server-game/
├── connectfour-client/
│   ├── pom.xml
│   └── src/main/java/com/connectfour/client/
│       ├── Main.java
│       ├── LoginScreen.java
│       ├── RegisterScreen.java
│       ├── MenuScreen.java
│       ├── GameScreen.java
│       └── GameOverScreen.java
└── connectfour-server/
    ├── pom.xml
    ├── users.txt
    └── src/main/java/com/connectfour/server/
        └── Server.java
```

---

## Prerequisites

Before running the project, install:

- **Java 17+** (project compiler level is set to 17)
- **Maven 3.8+**

---

## How to Run

### 1) Start the Server

From repository root:

```bash
cd /home/runner/work/connect4-client-server-game/connect4-client-server-game/connectfour-server
mvn compile
mvn exec:java -Dexec.mainClass="com.connectfour.server.Server"
```

If `mvn exec:java` is unavailable, run directly after compilation:

```bash
mvn package
java -cp target/classes com.connectfour.server.Server
```

### 2) Launch Two Clients (two terminal windows)

In each terminal:

```bash
cd /home/runner/work/connect4-client-server-game/connect4-client-server-game/connectfour-client
mvn compile
mvn javafx:run -Djavafx.mainClass="com.connectfour.client.Main"
```

If JavaFX plugin goals are unavailable in your environment, use your IDE run configuration for `com.connectfour.client.Main`.

---

## Gameplay Flow

1. Start the server.
2. Open two client instances.
3. Enter usernames on each client (registration is handled by the server).
4. Both users reach the menu and start a new game.
5. Players take turns dropping pieces in the 7-column board.
6. When a win condition is met, the game displays a game-over result.

---

## Networking Details

- Server host: `localhost`
- Port: `12345`
- Communication protocol: UTF messages over `DataInputStream` / `DataOutputStream`
- Server waits for exactly **two players** before starting the match relay loop.

---

## Current Functional Notes

- The server currently stores usernames in a text file.
- Client includes:
  - Login screen
  - Register screen
  - Menu screen
  - Main game board
  - Game-over screen
- Basic move forwarding and turn notification are implemented between two connected players.

---

## Important Limitation to Fix Before Production Use

In `connectfour-server/src/main/java/com/connectfour/server/Server.java`, the user file path is currently hardcoded to a local Windows path:

```java
private static final String USER_FILE = "D:\\eclipse-workspace\\connectfour-server\\src\\main\\java\\com\\connectfour\\users.txt";
```

You should replace this with a relative/configurable path for portability across environments.

---

## Suggested Next Improvements

- Add proper authentication (username + password validation)
- Remove hardcoded paths and server constants
- Add reconnection/timeout handling
- Add draw detection and rematch flow
- Add unit/integration tests
- Add packaging scripts for easier launch

---

## License

Add a license file (`LICENSE`) to define usage and distribution terms.

