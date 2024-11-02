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
}