package com.example.chip8emulator;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import java.util.HashMap;
import java.util.Map;

public class Input implements IInput {
    private Map<KeyCode, Integer> keyMap;
    private boolean[] keyStates;
    private Integer waitingKey;
    private Runnable onExit;

    public Input(Scene scene, Runnable onExit) {
        this.onExit = onExit;
        keyMap = new HashMap<>();
        keyStates = new boolean[16];
        waitingKey = null;

        // Key mappings
        keyMap.put(KeyCode.DIGIT1, 0x1);
        keyMap.put(KeyCode.W, 0x2);    // Up
        keyMap.put(KeyCode.DIGIT3, 0x3);
        keyMap.put(KeyCode.A, 0x4);    // Left
        keyMap.put(KeyCode.DIGIT4, 0xC);
        keyMap.put(KeyCode.E, 0x5);
        keyMap.put(KeyCode.D, 0x6);    // Right
        keyMap.put(KeyCode.R, 0xD);
        keyMap.put(KeyCode.S, 0x8);    // Down
        keyMap.put(KeyCode.F, 0xE);
        keyMap.put(KeyCode.Z, 0xA);
        keyMap.put(KeyCode.X, 0x0);
        keyMap.put(KeyCode.C, 0xB);
        keyMap.put(KeyCode.V, 0xF);

        setScene(scene);
        
    }

    public void setScene(Scene scene) {
        scene.setOnKeyPressed(event -> {
            Integer chip8Key = keyMap.get(event.getCode());
            if (chip8Key != null) {
                keyStates[chip8Key] = true;
                
                if (waitingKey != null) {
                    waitingKey = chip8Key;
                }
            } 
            if (event.getCode() == KeyCode.ESCAPE) {
                onExit.run();
            }
        });

        scene.setOnKeyReleased(event -> {
            Integer chip8Key = keyMap.get(event.getCode());
            if (chip8Key != null) {
                keyStates[chip8Key] = false;
               
            }
        });
    }

    @Override
    public boolean isKeyPressed(int key) {
        boolean state = key >= 0 && key < 16 && keyStates[key];
        
        return state;
    }

    @Override
    public Integer waitForKeyPress() {
        if (waitingKey == null) {
            waitingKey = -1;
            
            return null;
        }
        if (waitingKey == -1) {
            return null;
        }
        int key = waitingKey;
        waitingKey = null;
       
        return key;
    }
}