package com.example.chip8emulator;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import java.io.File;

public class Emulator extends Application {
    private static final int WIDTH = 64;
    private static final int HEIGHT = 32;
    private static final int SCALE = 10;

    private CPU cpu;
    private IDisplay display;
    private IInput input;
    private ITimers timers;
    private IMemory memory;
    private Stage stage;
    private Scene homeScene;
    private Scene emulatorScene;
    private AnimationTimer timer;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;

        // Home page setup
        VBox homeRoot = new VBox(10);
        TextField folderPathField = new TextField("games");
        Button loadButton = new Button("Load Games");
        ListView<String> gameList = new ListView<>();
        homeRoot.getChildren().addAll(new Label("Folder Path:"), folderPathField, loadButton, gameList);
        homeScene = new Scene(homeRoot, 400, 300);

        // Emulator setup
        Canvas canvas = new Canvas(WIDTH * SCALE, HEIGHT * SCALE);
        StackPane emulatorRoot = new StackPane(canvas);
        emulatorScene = new Scene(emulatorRoot);
        canvas.widthProperty().bind(emulatorScene.widthProperty());
        canvas.heightProperty().bind(emulatorScene.heightProperty());

        // Initialize components in correct order
        memory = new Memory();
        display = new Display(canvas);
        timers = new Timers();
        // Initialize input BEFORE cpu
        input = new Input(emulatorScene, () -> {
            timer.stop();
            stage.setScene(homeScene);
            stage.setFullScreen(false);
        });
        cpu = new CPU(memory, display, input, timers);

        // Load games from folder
        loadButton.setOnAction(e -> {
            String folderPath = folderPathField.getText();
            File folder = new File(folderPath);
            if (folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".ch8"));
                if (files != null) {
                    gameList.getItems().clear();
                    for (File file : files) {
                        gameList.getItems().add(file.getName());
                    }
                }
            } else {
                System.out.println("Invalid folder path: " + folderPath);
            }
        });

        // Launch game on selection
        gameList.setOnMouseClicked(e -> {
            String selectedGame = gameList.getSelectionModel().getSelectedItem();
            if (selectedGame != null) {
                cpu.reset();
                cpu.loadRom(folderPathField.getText() + "\\" + selectedGame);
                stage.setScene(emulatorScene);
                stage.setFullScreen(true);
                emulatorScene.getRoot().requestFocus();
                
                timer.start();
            }
        });

        // Emulation loop
        timer = new AnimationTimer() {
            private static final int CYCLES_PER_FRAME = 5; // Adjust this (e.g., 3, 5, 7)
            @Override
            public void handle(long now) {
                for (int i = 0; i < CYCLES_PER_FRAME; i++) {
                    cpu.cycle();
                }
                timers.tick();
                if (cpu.needsRedraw()) {
                    display.render();
                    cpu.resetRedrawFlag();
                }
            }
        };

        // Start with home page
        stage.setScene(homeScene);
        stage.setTitle("CHIP-8 Emulator");
        stage.show();
        homeScene.getRoot().requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}