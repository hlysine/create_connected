package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

public abstract class InstructionResult {

    private final boolean immediateExecution;

    public InstructionResult(boolean immediateExecution) {
        this.immediateExecution = immediateExecution;
    }

    public boolean isImmediate() {
        return immediateExecution;
    }

    public abstract int getNextInstruction(int currentInstruction);

    public static InstructionResult incomplete() {
        return new ContinueCurrent(false);
    }

    public static InstructionResult next(boolean immediate) {
        return new NextInstruction(immediate);
    }

    public static InstructionResult terminate() {
        return new EndSequence();
    }

    public static class ContinueCurrent extends InstructionResult {
        public ContinueCurrent(boolean immediateExecution) {
            super(immediateExecution);
        }

        @Override
        public int getNextInstruction(int currentInstruction) {
            return currentInstruction;
        }
    }

    public static class NextInstruction extends InstructionResult {
        public NextInstruction(boolean immediateExecution) {
            super(immediateExecution);
        }

        @Override
        public int getNextInstruction(int currentInstruction) {
            return currentInstruction + 1;
        }
    }

    public static class EndSequence extends InstructionResult {
        public EndSequence() {
            super(false);
        }

        @Override
        public int getNextInstruction(int currentInstruction) {
            return -1;
        }
    }
}
