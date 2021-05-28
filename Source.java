/*
* To change this license header, choose License Headers in Project
Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Source {

    public static void main(String[] args) {
        String firstArgument;
        if (args.length == 0) firstArgument = null;
        else firstArgument = args[0];
        if (firstArgument == null || firstArgument.isEmpty())
            firstArgument = "input.txt";
        Compiler compiler = new Compiler(firstArgument);
        compiler.start();
    }
}

class Instruction {
    private int position;
    private String instruction;
    private int value;

    public Instruction(int position, String instruction, int value) {
        this.position = position;
        this.instruction = instruction;
        this.value = value;
    }

    public Instruction(int position, String instruction) {
        this.position = position;
        this.instruction = instruction;
        value = 0;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(byte position) {
        this.position = position;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public int getValue() {
        return value;
    }

    public void setValue(byte value) {
        this.value = value;
    }
}

class Compiler extends Thread {
    private int[] memory = new int[256];
    private Stack<Integer> stack = new Stack<Integer>();
    private int AC = 0;
    private int PC = 0;
    private int AX = 0;
    private String str = null;

    private boolean execution = true;
    private int flag;
    private boolean isJumped = false;

    public Compiler(String path) {
        FileHandler.setFilePath(path);
    }

    @Override
    public void run() {
        super.run();
        executeInstruction();
        while (execution) {
            executeInstruction();
        }
    }

    private void executeInstruction() {
        Instruction instruction =
                FileHandler.readInstructionFromFile(PC);
        if (instruction != null) {
            switch (instruction.getInstruction()) {
                case "START":
                    START();
                    break;

                case "LOAD":
                    LOAD(instruction.getValue());
                    break;
                case "LOADM":
                    LOADM(instruction.getValue());
                    break;
                case "STORE":
                    STORE(instruction.getValue());
                    break;
                case "CMPM":
                    CMPM(instruction.getValue());
                    break;
                case "CJMP":
                    CJMP(instruction.getValue());
                    break;
                case "JMP":
                    JMP(instruction.getValue());
                    break;
                case "ADD":
                    ADD(instruction.getValue());
                    break;
                case "ADDM":
                    ADDM(instruction.getValue());
                    break;
                case "SUBM":
                    SUBM(instruction.getValue());
                    break;
                case "SUB":
                    SUB(instruction.getValue());
                    break;
                case "MUL":
                    MUL(instruction.getValue());
                    break;
                case "MULM":
                    MULM(instruction.getValue());
                    break;
                case "PUSH":
                    PUSH(instruction.getValue());
                    break;
                case "DISP":
                    DISP();
                    break;
                case "DASC":
                    DASC();
                    break;
                case "HALT":
                    HALT();
                    break;
                case "LOADI":
                    LOADI();
                    break;
                case "STOREI":
                    STOREI();
                    break;
                case "SWAP":
                    SWAP();
                    break;
                case "POP":
                    POP();
                    break;
                case "RETURN":
                    RETURN();
                    break;
            }
            if (!isJumped) PC++;
            isJumped = false;
        }
    }

    private void START() {
        execution = true;
    }

    private void LOAD(int value) {
        AC = value;
    }

    private void LOADM(int memoryValue) {
        AC = memory[memoryValue];
    }

    private void STORE(int memoryValue) {
        memory[memoryValue] = AC;
    }

    private void CMPM(int memoryValue) {
        flag = Integer.compare(AC, memory[memoryValue]);
    }

    private void CJMP(int value) {
        if (flag > 0) {
            PC = value;
            isJumped = true;
        }
    }

    private void JMP(int value) {
        PC = value;
        isJumped = true;
    }

    private void ADD(int value) {
        AC += value;
    }

    private void ADDM(int memoryValue) {
        AC += memory[memoryValue];
    }

    private void SUBM(int memoryValue) {
        AC -= memory[memoryValue];
    }

    private void SUB(int value) {
        AC -= value;
    }

    private void MUL(int value) {
        AC *= value;
    }

    private void MULM(int memoryValue) {
        AC *= memory[memoryValue];
    }

    private void DISP() {
        System.out.print(AC);
    }

    private void DASC() {
        str = Character.toString(AC);
        System.out.print(str);
    }

    private void HALT() {
        execution = false;
    }

    private void LOADI() {
        AC = memory[AX];
    }

    private void STOREI() {
        memory[AX] = AC;
    }

    private void SWAP() {
        memory[255] = AC;
        AC = AX;
        AX = memory[255];
    }

    private void PUSH(int value) {
        stack.push(value);
    }

    private void POP() {

        AC = stack.pop();
    }

    private void RETURN() {
        PC = stack.pop();
        isJumped = true;
    }
}

class FileHandler {
    /**
     * We hold the insctruction in memory
     * For each instruction, speed performance is O(1)
     * memory performance is O(n)
     */
    private static String filePathAsString = "program.txt";

    public static Instruction[] readInstructionsFromFile() {
        try {
            List<String> lines =
                    Files.readAllLines(Paths.get(filePathAsString));
            List<Instruction> instructions = new ArrayList<>();
            for (String line : lines) {
                instructions.add(convertStringToInstruction(line));
            }
            return instructions.toArray(new Instruction[0]);
        } catch (IOException e) {
            return new Instruction[0];
        }
    }

    public static void setFilePath(String path) {
        filePathAsString = path;
    }

    /**
     * When each instruction is called it reads the file from begining
     * to end but does not keep it in memory
     * For each instruction, speed performance is O(n)
     * all instructions O(n^2)
     * memory performance O(1)
     */
    public static Instruction readInstructionFromFile(int position) {
        try {
            return
                    convertStringToInstruction(Files.readAllLines(Paths.get(filePathAsString)).get(position));
        } catch (IOException e) {
            return null;
        }
    }

    private static Instruction convertStringToInstruction(String line) {
        String[] instruction = line.split(" ");
        if (instruction.length == 2) return new
                Instruction(Integer.parseInt(instruction[0]), instruction[1]);
        else if (instruction.length == 3) {
            return new
                    Instruction(Integer.parseInt(instruction[0]), instruction[1], Integer.parseInt(instruction[2]));
        } else return null;
    }
}