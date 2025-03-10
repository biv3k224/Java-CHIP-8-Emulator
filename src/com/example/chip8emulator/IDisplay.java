package com.example.chip8emulator;

public interface IDisplay {

	void clear();
	boolean drawSprite(int x, int y, byte[]spriteData, int numBytes);
	boolean isPixelSet(int x, int y);
	void render();
}

interface IMemory {

	byte read(int address);
	void write(int address, byte value);
	void loadRom(byte[] romData);
}

interface IInput {
	boolean isKeyPressed(int key);
	Integer waitForKeyPress();
}

interface ITimers{
	void setDelayTimer(int value);
	int getDelayTimer();
	void setSoundTimer(int value);
	int getSoundTimer();
	void tick();
}
