package org.example;

public class Refinery extends Node {
    public int capacity, maxOutput, production, stock;
    public double overflowPenalty, underflowPenalty, overOutputPenalty, productionCost, productionCO2;

    public Refinery() {
        super("sef", "Source", NodeType.REFINERY);
    }

    public Refinery(String id, String name, int capacity, int maxOutput, int production, double overflowPenalty,
                    double underflowPenalty, double overOutputPenalty, double productionCost, double productionCO2, int initialStock) {
        super(id, name, NodeType.REFINERY);
        this.capacity = capacity;
        this.maxOutput = maxOutput;
        this.production = production;
        this.overflowPenalty = overflowPenalty;
        this.underflowPenalty = underflowPenalty;
        this.overOutputPenalty = overOutputPenalty;
        this.productionCost = productionCost;
        this.productionCO2 = productionCO2;
        this.stock = initialStock;
    }

    public Refinery(final Refinery other) {
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

    @Override
    double getMaxOutputPenalty() {
        if (stock > capacity)
            return (stock - capacity) * overflowPenalty;
        else    
            return 0;
    }   

    @Override 
    double getMaxInputPenalty() {
        return 0;
    }
}
