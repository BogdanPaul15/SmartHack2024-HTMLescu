package org.example;

class Tank extends Node {
    public int capacity, maxInput, maxOutput, stock;
    public double overflowPenalty, underflowPenalty, overInputPenalty, overOutputPenalty;

    public Tank(String id, String name, int capacity, int maxInput, int maxOutput, double overflowPenalty,
                double underflowPenalty, double overInputPenalty, double overOutputPenalty, int initialStock) {
        super(id, name, NodeType.TANK);
        this.capacity = capacity;
        this.maxInput = maxInput;
        this.maxOutput = maxOutput;
        this.overflowPenalty = overflowPenalty;
        this.underflowPenalty = underflowPenalty;
        this.overInputPenalty = overInputPenalty;
        this.overOutputPenalty = overOutputPenalty;
        this.stock = initialStock;
    }

    public Tank(final Tank other) {
        super(other);
        this.capacity = other.capacity;
        this.maxInput = other.maxInput;
        this.maxOutput = other.maxOutput;
        this.overflowPenalty = other.overflowPenalty;
        this.underflowPenalty = other.underflowPenalty;
        this.overInputPenalty = other.overInputPenalty;
        this.overOutputPenalty = other.overOutputPenalty;
        this.stock = other.stock;
    }
}