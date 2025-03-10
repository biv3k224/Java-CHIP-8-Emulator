package com.example.chip8emulator;

public class Memory implements IMemory{

	private byte[] memory = new byte[4096];
	
	public Memory() {
		// Load CHIP-8 font set (0x0 to 0xF) into memory 0x000-0x1FF
		byte[] fontSet = {
				(byte)0xF0, (byte)0x90, (byte)0x90, (byte)0x90, (byte)0xF0, // 0
	            (byte)0x20, (byte)0x60, (byte)0x20, (byte)0x20, (byte)0x70, // 1
	            (byte)0xF0, (byte)0x10, (byte)0xF0, (byte)0x80, (byte)0xF0, // 2
	            (byte)0xF0, (byte)0x10, (byte)0xF0, (byte)0x10, (byte)0xF0, // 3
	            (byte)0x90, (byte)0x90, (byte)0xF0, (byte)0x10, (byte)0x10, // 4
	            (byte)0xF0, (byte)0x80, (byte)0xF0, (byte)0x10, (byte)0xF0, // 5
	            (byte)0xF0, (byte)0x80, (byte)0xF0, (byte)0x90, (byte)0xF0, // 6
	            (byte)0xF0, (byte)0x10, (byte)0x20, (byte)0x40, (byte)0x40, // 7
	            (byte)0xF0, (byte)0x90, (byte)0xF0, (byte)0x90, (byte)0xF0, // 8
	            (byte)0xF0, (byte)0x90, (byte)0xF0, (byte)0x10, (byte)0xF0, // 9
	            (byte)0xF0, (byte)0x90, (byte)0xF0, (byte)0x90, (byte)0x90, // A
	            (byte)0xE0, (byte)0x90, (byte)0xE0, (byte)0x90, (byte)0xE0, // B
	            (byte)0xF0, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0xF0, // C
	            (byte)0xE0, (byte)0x90, (byte)0x90, (byte)0x90, (byte)0xE0, // D
	            (byte)0xF0, (byte)0x80, (byte)0xF0, (byte)0x80, (byte)0xF0, // E
	            (byte)0xF0, (byte)0x80, (byte)0xF0, (byte)0x80, (byte)0x80  // F
		};
		
		System.arraycopy(fontSet, 0, memory, 0, fontSet.length);
	}
	
	@Override
	public byte read(int address) {
		return memory[address];
	}
	
	@Override
	public void write(int address, byte value) {
		memory[address] = value;
	}
	
	@Override
	public void loadRom(byte[] romData) {
		System.arraycopy(romData, 0, memory, 0x200, romData.length);
	}
}
