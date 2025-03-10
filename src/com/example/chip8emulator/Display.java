package com.example.chip8emulator;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
public class Display implements IDisplay {
    private static final int WIDTH = 64;
    private static final int HEIGHT = 32;
    private boolean[][] pixels = new boolean[WIDTH][HEIGHT];
    private Canvas canvas;
    private GraphicsContext gc;

    public Display(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        clear();
    }
	
	@Override
    public void clear() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                pixels[x][y] = false;
            }
        }
        render();
    }
	
	@Override
	
    public boolean drawSprite(int x, int y, byte[] sprite, int height) {
        boolean collision = false;
        for (int row = 0; row < height; row++) {
            byte spriteRow = sprite[row];
            for (int bit = 0; bit < 8; bit++) {
                int pixelX = (x + bit) % WIDTH;
                int pixelY = (y + row) % HEIGHT;
                boolean spritePixel = (spriteRow & (0x80 >> bit)) != 0;
                boolean oldPixel = pixels[pixelX][pixelY];
                pixels[pixelX][pixelY] ^= spritePixel; // XOR with existing pixel
                if (oldPixel && !pixels[pixelX][pixelY]) { // Collision: pixel flipped from 1 to 0
                    collision = true;
                }
            }
        }
        return collision;
    }
	
	@Override
    public boolean isPixelSet(int x, int y) {
        return pixels[x % WIDTH][y % HEIGHT];
    }
	
	@Override
    public void render() {
        double scaleX = canvas.getWidth() / WIDTH;
        double scaleY = canvas.getHeight() / HEIGHT;
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.WHITE);
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (pixels[x][y]) {
                    gc.fillRect(x * scaleX, y * scaleY, scaleX, scaleY);
                }
            }
        }
    }
}
