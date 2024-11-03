package org.example;

public class Refinery extends Node {
    int capacity, maxOutput, production, overflowPenalty, underflowPenalty, overOutputPenalty;
    int productionCost, productionCO2, stock;

    public Refinery() {
        super("1", "Source", NodeType.RAFINERY);    
    }

    public Refinery(String id, String name, int capacity, int maxOutput, int production, int overflowPenalty,
                    int underflowPenalty, int overOutputPenalty, int productionCost, int productionCO2, int stock) {
        super(id, name, NodeType.RAFINERY);
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

    public Refinery(Refinery other) {
        super(other);
        this.capacity = other.capacity;
        this.maxOutput = other.maxOutput;
        this.production = other.production;
        this.overflowPenalty = other.overflowPenalty;
        this.underflowPenalty = other.underflowPenalty;
        this.overOutputPenalty = other.overOutputPenalty;
        this.productionCost = other.productionCost;
        this.productionCO2 = other.productionCO2;
        this.stock = other.stock;
    }
}
