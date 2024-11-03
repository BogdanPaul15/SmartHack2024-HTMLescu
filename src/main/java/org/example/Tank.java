package org.example;

class Tank extends Node {
    int capacity, maxInput, maxOutput, overflowPenalty, underflowPenalty, overInputPenalty, overOutputPenalty, initialStock;

    public Tank(String id, String name, int capacity, int maxInput, int maxOutput, int overflowPenalty,
                int underflowPenalty, int overInputPenalty, int overOutputPenalty, int initialStock) {
        super(id, name, NodeType.TANK);
        this.capacity = capacity;
        this.maxInput = maxInput;
        this.maxOutput = maxOutput;
        this.overflowPenalty = overflowPenalty;
        this.underflowPenalty = underflowPenalty;
        this.overInputPenalty = overInputPenalty;
        this.overOutputPenalty = overOutputPenalty;
        this.initialStock = initialStock;
    }

    public Tank(Tank other) {
        super(other);
        this.capacity = other.capacity;
        this.maxInput = other.maxInput;
        this.maxOutput = other.maxOutput;
        this.overflowPenalty = other.overflowPenalty;
        this.underflowPenalty = other.underflowPenalty;
        this.overInputPenalty = other.overInputPenalty;
        this.overOutputPenalty = other.overOutputPenalty;
        this.initialStock = other.initialStock;
    }
}