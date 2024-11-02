package org.example;

public class Movement {
    private final String connectionId;
    private final Integer amount;

    public Movement(String connectionId, Integer amount) {
        this.connectionId = connectionId;
        this.amount = amount;
    }

    public String getConnectionId() {
        return connectionId;
    }
    public Integer getAmount() {
        return amount;
    }
}
