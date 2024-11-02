public class Refinery extends Node {
    int capacity, maxOutput, production, overflowPenalty, underflowPenalty, overOutputPenalty;
    int productionCost, productionCO2, initialStock;

    public Refinery(String id, String name, int capacity, int maxOutput, int production, int overflowPenalty,
                    int underflowPenalty, int overOutputPenalty, int productionCost, int productionCO2, int initialStock) {
        super(id, name, NodeType.RAFINERY);
        this.capacity = capacity;
        this.maxOutput = maxOutput;
        this.production = production;
        this.overflowPenalty = overflowPenalty;
        this.underflowPenalty = underflowPenalty;
        this.overOutputPenalty = overOutputPenalty;
        this.productionCost = productionCost;
        this.productionCO2 = productionCO2;
        this.initialStock = initialStock;
    }
}
