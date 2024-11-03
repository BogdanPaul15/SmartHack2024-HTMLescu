package org.example;

public class Movement {
    private final String edgeId, fromId, toId;
    private final Integer amount, arrivalDay, startDay;

    public Movement(final String connectionId, final Integer amount, final Integer arrivalDay, final String fromId, final String toId, final Integer startDay) {
        this.edgeId = connectionId;
        this.amount = amount;
        this.arrivalDay = arrivalDay;
        this.fromId = fromId;
        this.toId = toId;
        this.startDay = startDay;
    }

    public String getEdgeId() {
        return edgeId;
    }
    public Integer getAmount() {
        return amount;
    }
    public Integer getArrivalDay() {
        return arrivalDay;
    }
    public String getFromId() {
        return fromId;
    }
    public String getToId() {
        return toId;
    }
    public Integer getStartDay() {
        return startDay;
    }
}
