# 🎮 CHIP-8 Emulator  

A **JavaFX-based** emulator for the **CHIP-8 virtual machine**, designed to run classic CHIP-8 games in **full-screen mode with audio support**. This project includes a graphical interface for selecting and loading ROMs, as well as keyboard controls mapped to the original CHIP-8 keypad.

---

## ✨ Features  
✔ **Full-screen emulation** with a **64x32** pixel display, scaled up for modern screens.  
✔ **Supports .ch8 ROM files** from a `games` folder within the project.   
✔ **Home screen with game selection** for easy navigation.  
✔ **Configurable CPU cycle speed** for balancing performance and input responsiveness.  

---

## 🛠 Prerequisites  
- **Java**: Version **17** or higher (**JavaFX included**).  
- **IDE**: IntelliJ IDEA, Eclipse, or any IDE with JavaFX support (**optional but recommended**).  
- **Games**: CHIP-8 ROMs (`.ch8` files) placed in the `games` folder.  

---

## 🚀 Setup Instructions  

### 🔧 Running from Source  
1⃣ **Clone or Download the Project**  
   ```sh
   git clone https://github.com/your-repo/chip8-emulator.git
   cd chip8-emulator
   ```

2⃣ **Project Structure**  
   - Ensure the `games` folder is in the project root (`project_root/games/`)  
   - Example ROMs: `Brick (Brix hack, 1990).ch8`, `Airplane.ch8`  

3⃣ **Configure JavaFX**  
   - If using an IDE, add JavaFX SDK (e.g., VM options:  
     ```
     --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
     ```
   - Alternatively, use Maven/Gradle with JavaFX dependencies.  

4⃣ **Compile and Run**  
   - Open the project in your IDE.  
   - Run `Emulator.java` as the **main class**.   

---

### 📦 Running as a JAR (Advanced)  
1⃣ **Package the project using a build tool** (e.g., Maven).  
2⃣ **Ensure the games folder is in the JAR’s root** or bundled as a resource.  
3⃣ **Run with:**  
   ```sh
   java -jar chip8-emulator.jar --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
   ```

---

## 🎮 Controls  

| CHIP-8 Key | Keyboard Key | Function |
|------------|-------------|----------|
| 0x0        | X           |          |
| 0x1        | 1           |          |
| 0x2        | W           | Up       |
| 0x3        | 3           |          |
| 0x4        | A           | Left     |
| 0x5        | E           |          |
| 0x6        | D           | Right    |
| 0x7        | 4           |          |
| 0x8        | S           | Down     |
| 0x9        | R           |          |
| 0xA        | Z           |          |
| 0xB        | C           |          |
| 0xC        | Q           |          |
| 0xD        | F           |          |
| 0xE        | T           |          |
| 0xF        | V           |          |

### 🎯 Additional Controls  
🔹 **Exit Game:** Press **ESC** to return to the home screen from full-screen mode.  

---

## 🎮 Usage  

1⃣ **Launch the Emulator** – Start the app to see the home screen.  
2⃣ **Load Games** – The `games` folder is preset to `games/` (relative to the project root).  
3⃣ **Play a Game** – Click a game name (e.g., `Brick (Brix hack, 1990).ch8`) to launch it in full-screen mode.  
4⃣ **Exit** – Press **ESC** to return to the home screen.  

---

## ⚙️ Configuration  

🔧 **Cycle Speed:** Adjust `CYCLES_PER_FRAME` in `Emulator.java` (**default: 5**). Increase for faster emulation (**e.g., 10**), but test input responsiveness.  

---

## ❗ Troubleshooting  

⚠ **Games Don’t Load**  
   - Check the `games` folder path in `Emulator.java`.  
   - Ensure `.ch8` files are present.  
   - Console output shows the attempted path if loading fails.  

⚠ **Input Lag**  
   - Increase `CYCLES_PER_FRAME` if too slow.  
   - Decrease if keys don’t register properly.  

---

## 🔮 Future Improvements  

✅ **Add dynamic cycle speed adjustment via UI.**  
✅ **Support loading ROMs from external directories.**  
✅ **Enhance audio with a generated square wave instead of a static file.**  

---

## 🎓 Credits  

👨‍💻 Built with **JavaFX** by **Bibek Kumar Tamang**.  
---

🚀 **Enjoy playing classic CHIP-8 games on your modern machine!**

