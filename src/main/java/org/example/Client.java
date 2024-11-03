package org.example;

class Client extends Node {
    int maxInput, overInputPenalty, lateDeliveryPenalty, earlyDeliveryPenalty;

    public Client(String id, String name, int maxInput, int overInputPenalty, int lateDeliveryPenalty, int earlyDeliveryPenalty) {
        super(id, name, NodeType.CLIENT);
        this.maxInput = maxInput;
        this.overInputPenalty = overInputPenalty;
        this.lateDeliveryPenalty = lateDeliveryPenalty;
        this.earlyDeliveryPenalty = earlyDeliveryPenalty;
    }

    public Client(Client other) {
        super(other);
        this.maxInput = other.maxInput;
        this.overInputPenalty = other.overInputPenalty;
        this.lateDeliveryPenalty = other.lateDeliveryPenalty;
        this.earlyDeliveryPenalty = other.earlyDeliveryPenalty;
    }
}