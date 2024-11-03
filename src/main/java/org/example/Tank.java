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
}