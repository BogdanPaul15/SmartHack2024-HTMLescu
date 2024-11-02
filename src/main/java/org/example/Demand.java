package org.example;

public class Demand {
    private final String customerId;
    private final int amount, postDay, startDay, endDay;

    public Demand(final String id, final int amount, final int postDay, final int startDay, final int endDay) {
        this.customerId = id;
        this.amount = amount;
        this.postDay = postDay;
        this.startDay = startDay;
        this.endDay = endDay;
    }

    public String getCustomerId() {
        return customerId;
    }

    public int getAmount() {
        return amount;
    }

    public int getPostDay() {
        return postDay;
    }

    public int getStartDay() {
        return startDay;
    }

    public int getEndDay() {
        return endDay;
    }
}
