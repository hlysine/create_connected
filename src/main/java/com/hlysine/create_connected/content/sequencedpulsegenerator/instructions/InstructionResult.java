package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

public abstract class InstructionResult {

    public InstructionResult() {
    }

    public abstract int getNextInstruction(int currentInstruction);

    public static InstructionResult incomplete() {
        return new ContinueCurrent();
    }

    public static InstructionResult next() {
        return new NextInstruction();
    }

    public static InstructionResult terminate() {
        return new EndSequence();
    }

    public static class ContinueCurrent extends InstructionResult {
        @Override
        public int getNextInstruction(int currentInstruction) {
            return currentInstruction;
        }
    }

    public static class NextInstruction extends InstructionResult {
        @Override
        public int getNextInstruction(int currentInstruction) {
            return currentInstruction + 1;
        }
    }

    public static class EndSequence extends InstructionResult {
        @Override
        public int getNextInstruction(int currentInstruction) {
            return -1;
        }
    }
}
