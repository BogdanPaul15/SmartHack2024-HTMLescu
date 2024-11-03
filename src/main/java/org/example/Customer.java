package org.example;

class Customer extends Node {
    public int maxInput;
    public double overInputPenalty, lateDeliveryPenalty, earlyDeliveryPenalty;

    public Customer(String id, String name, int maxInput, double overInputPenalty, double lateDeliveryPenalty, double earlyDeliveryPenalty) {
        super(id, name, NodeType.CLIENT);
        this.maxInput = maxInput;
        this.overInputPenalty = overInputPenalty;
        this.lateDeliveryPenalty = lateDeliveryPenalty;
        this.earlyDeliveryPenalty = earlyDeliveryPenalty;
    }

    public Customer(final Customer other) {
        super(other);
        this.maxInput = other.maxInput;
        this.overInputPenalty = other.overInputPenalty;
        this.lateDeliveryPenalty = other.lateDeliveryPenalty;
        this.earlyDeliveryPenalty = other.earlyDeliveryPenalty;
    }
}