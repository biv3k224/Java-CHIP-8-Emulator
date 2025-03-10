package com.example.chip8emulator;

public class Timers implements ITimers{

	private int soundTimer;
	private int delayTimer;
	
	@Override
	public void setDelayTimer(int value) {
		delayTimer = value;
	}
	
	@Override
	public int getDelayTimer() {
		return delayTimer;
	}
	
	@Override
	public void setSoundTimer(int value) {
		soundTimer = value;
	}
	
	@Override
	public int getSoundTimer() {
		return soundTimer;
	}
	
	@Override
	public void tick() {
		if(delayTimer > 0) delayTimer --;
		if(soundTimer > 0) soundTimer --;
	}
}
