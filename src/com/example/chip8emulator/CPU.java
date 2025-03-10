package com.example.chip8emulator;

import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

public class CPU {
    private byte[] V;            // 16 8-bit general-purpose registers (V0-VF)
    private int I;               // 16-bit index register
    private int PC;              // 16-bit program counter
    private Stack<Integer> stack;// Call stack for subroutines
    private IMemory memory;      // Memory interface
    private IDisplay display;    // Display interface
    private IInput input;        // Input interface
    private ITimers timers;      // Timers interface
    private boolean needsRedraw; // Flag to indicate if display needs updating
    private Random random;       // Random number generator for 0xCXNN opcode

    // Constructor
    public CPU(IMemory memory, IDisplay display, IInput input, ITimers timers) {
        this.memory = memory;
        this.display = display;
        this.input = input;
        this.timers = timers;
        V = new byte[16];
        stack = new Stack<>();
        random = new Random();
        reset(); // Initialize to default state
    }

    // Reset CPU state for a new game
    public void reset() {
        Arrays.fill(V, (byte) 0);
        I = 0;
        PC = 0x200; // CHIP-8 programs start at 0x200
        stack.clear();
        timers.setDelayTimer(0);
        timers.setSoundTimer(0);
        display.clear();
        needsRedraw = true;
    }

    // Load ROM into memory
    public void loadRom(String gamePath) {
        try {
            byte[] romData = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(gamePath));
            if (romData.length > (4096 - 0x200)) {
                throw new IllegalArgumentException("ROM too large for CHIP-8 memory");
            }
            memory.loadRom(romData); // Assumes memory loads at 0x200
            System.out.println("ROM loaded successfully: " + gamePath);
        } catch (Exception e) {
            System.err.println("Failed to load ROM: " + e.getMessage());
            throw new RuntimeException("Cannot proceed without a valid ROM", e);
        }
    }

    // Execute one CPU cycle
    public void cycle() {
        if (PC < 0x200 || PC >= 4096) {
            System.err.println("Program Counter out of bounds: PC = " + PC);
            return;
        }

        // Fetch opcode (2 bytes)
        int opcode = (memory.read(PC) & 0xFF) << 8 | (memory.read(PC + 1) & 0xFF);
        PC += 2; // Advance PC by 2 bytes

        // Decode and execute opcode
        decodeAndExecute(opcode);
    }

    // Decode and execute the opcode
    private void decodeAndExecute(int opcode) {
        int x = (opcode & 0x0F00) >> 8; // Register index (Vx)
        int y = (opcode & 0x00F0) >> 4; // Register index (Vy)
        int n = opcode & 0x000F;        // 4-bit value
        int nn = opcode & 0x00FF;       // 8-bit value
        int nnn = opcode & 0x0FFF;      // 12-bit address

        switch (opcode & 0xF000) {
            case 0x0000:
                if (opcode == 0x00E0) { // CLS: Clear display
                    display.clear();
                    needsRedraw = true;
                } else if (opcode == 0x00EE) { // RET: Return from subroutine
                    if (!stack.isEmpty()) {
                        PC = stack.pop();
                    }
                }
                break;

            case 0x1000: // JP addr: Jump to address nnn
                PC = nnn;
                break;

            case 0x2000: // CALL addr: Call subroutine at nnn
                stack.push(PC);
                PC = nnn;
                break;

            case 0x3000: // SE Vx, byte: Skip next instruction if Vx == nn
                if (V[x] == nn) {
                    PC += 2;
                }
                break;

            case 0x4000: // SNE Vx, byte: Skip next instruction if Vx != nn
                if (V[x] != nn) {
                    PC += 2;
                }
                break;

            case 0x5000: // SE Vx, Vy: Skip next instruction if Vx == Vy
                if ((opcode & 0x000F) == 0) {
                    if (V[x] == V[y]) {
                        PC += 2;
                    }
                }
                break;

            case 0x6000: // LD Vx, byte: Set Vx = nn
                V[x] = (byte) nn;
                break;

            case 0x7000: // ADD Vx, byte: Set Vx = Vx + nn
                V[x] = (byte) (V[x] + nn);
                break;

            case 0x8000:
                switch (opcode & 0x000F) {
                    case 0x0: // LD Vx, Vy: Set Vx = Vy
                        V[x] = V[y];
                        break;
                    case 0x1: // OR Vx, Vy: Set Vx = Vx OR Vy
                        V[x] |= V[y];
                        break;
                    case 0x2: // AND Vx, Vy: Set Vx = Vx AND Vy
                        V[x] &= V[y];
                        break;
                    case 0x3: // XOR Vx, Vy: Set Vx = Vx XOR Vy
                        V[x] ^= V[y];
                        break;
                    case 0x4: // ADD Vx, Vy: Set Vx = Vx + Vy, VF = carry
                        int sum = (V[x] & 0xFF) + (V[y] & 0xFF);
                        V[0xF] = (byte) (sum > 0xFF ? 1 : 0);
                        V[x] = (byte) sum;
                        break;
                    case 0x5: // SUB Vx, Vy: Set Vx = Vx - Vy, VF = NOT borrow
                        V[0xF] = (byte) ((V[x] & 0xFF) >= (V[y] & 0xFF) ? 1 : 0);
                        V[x] = (byte) (V[x] - V[y]);
                        break;
                    case 0x6: // SHR Vx: Set Vx = Vx >> 1, VF = LSB
                        V[0xF] = (byte) (V[x] & 0x1);
                        V[x] = (byte) ((V[x] & 0xFF) >>> 1);
                        break;
                    case 0x7: // SUBN Vx, Vy: Set Vx = Vy - Vx, VF = NOT borrow
                        V[0xF] = (byte) ((V[y] & 0xFF) >= (V[x] & 0xFF) ? 1 : 0);
                        V[x] = (byte) (V[y] - V[x]);
                        break;
                    case 0xE: // SHL Vx: Set Vx = Vx << 1, VF = MSB
                        V[0xF] = (byte) ((V[x] & 0x80) >>> 7);
                        V[x] = (byte) (V[x] << 1);
                        break;
                }
                break;

            case 0x9000: // SNE Vx, Vy: Skip next instruction if Vx != Vy
                if ((opcode & 0x000F) == 0) {
                    if (V[x] != V[y]) {
                        PC += 2;
                    }
                }
                break;

            case 0xA000: // LD I, addr: Set I = nnn
                I = nnn;
                break;

            case 0xB000: // JP V0, addr: Jump to nnn + V0
                PC = nnn + (V[0] & 0xFF);
                break;

            case 0xC000: // RND Vx, byte: Set Vx = random byte AND nn
                V[x] = (byte) (random.nextInt(256) & nn);
                break;

            case 0xD000: // DRW Vx, Vy, nibble: Display sprite at (Vx, Vy), height n
                byte[] spriteData = new byte[n];
                for (int i = 0; i < n; i++) {
                    spriteData[i] = memory.read(I + i);
                }
                V[0xF] = (byte) (display.drawSprite(V[x] & 0xFF, V[y] & 0xFF, spriteData, n) ? 1 : 0);
                needsRedraw = true;
                break;

            case 0xE000:
                if (nn == 0x9E) { // SKP Vx: Skip next instruction if key Vx is pressed
                	
                    if (input.isKeyPressed(V[x] & 0xFF)) {
                        PC += 2;
                    }
                } else if (nn == 0xA1) { // SKNP Vx: Skip if key Vx not pressed
                	
                    if (!input.isKeyPressed(V[x] & 0xFF)) {
                        PC += 2;
                    }
                }
                break;

            case 0xF000:
                switch (nn) {
                    case 0x07: // LD Vx, DT: Set Vx = delay timer
                        V[x] = (byte) timers.getDelayTimer();
                        break;
                    case 0x0A: // LD Vx, K: Wait for key press, store in Vx
                        Integer key = input.waitForKeyPress();
                        if (key == null) {
                            PC -= 2; // Stay on this instruction until a key is pressed
                        } else {
                            V[x] = key.byteValue();
                        }
                        break;
                    case 0x15: // LD DT, Vx: Set delay timer = Vx
                        timers.setDelayTimer(V[x] & 0xFF);
                        break;
                    case 0x18: // LD ST, Vx: Set sound timer = Vx
                        timers.setSoundTimer(V[x] & 0xFF);
                        break;
                    case 0x1E: // ADD I, Vx: Set I = I + Vx
                        I += (V[x] & 0xFF);
                        break;
                    case 0x29: // LD F, Vx: Set I = location of sprite for digit Vx
                        I = (V[x] & 0xF) * 5; // Assuming font sprites at 0x0
                        break;
                    case 0x33: // LD B, Vx: Store BCD of Vx in memory[I..I+2]
                        int value = V[x] & 0xFF;
                        memory.write(I, (byte) (value / 100));
                        memory.write(I + 1, (byte) ((value / 10) % 10));
                        memory.write(I + 2, (byte) (value % 10));
                        break;
                    case 0x55: // LD [I], Vx: Store V0-Vx in memory starting at I
                        for (int i = 0; i <= x; i++) {
                            memory.write(I + i, V[i]);
                        }
                        break;
                    case 0x65: // LD Vx, [I]: Load V0-Vx from memory starting at I
                        for (int i = 0; i <= x; i++) {
                            V[i] = memory.read(I + i);
                        }
                        break;
                }
                break;

            default:
                System.err.println("Unknown opcode: 0x" + Integer.toHexString(opcode));
        }
    }

    // Check if display needs to be redrawn
    public boolean needsRedraw() {
        return needsRedraw;
    }

    // Reset redraw flag after rendering
    public void resetRedrawFlag() {
        needsRedraw = false;
    }
}