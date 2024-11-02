package org.example;

public class Refinery extends Node {
    public int capacity, maxOutput, production, stock;
    public double overflowPenalty, underflowPenalty, overOutputPenalty, productionCost, productionCO2;

    public Refinery() {
        super("1", "Source", NodeType.REFINERY);
    }

    public Refinery(String id, String name, int capacity, int maxOutput, int production, double overflowPenalty,
                    double underflowPenalty, double overOutputPenalty, double productionCost, double productionCO2, int stock) {
        super(id, name, NodeType.REFINERY);
        this.capacity = capacity;
        this.maxOutput = maxOutput;
        this.production = production;
        this.overflowPenalty = overflowPenalty;
        this.underflowPenalty = underflowPenalty;
        this.overOutputPenalty = overOutputPenalty;
        this.productionCost = productionCost;
        this.productionCO2 = productionCO2;
        this.stock = stock;
    }
}
