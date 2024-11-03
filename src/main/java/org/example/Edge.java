package org.example;

import java.util.PriorityQueue;

public class Edge {
    double pipelineCostPerDistanceAndVolume = 0.05d;
    double pipelineCo2PerDistanceAndVolume = 0.02d;
    double truckCostPerDistanceAndVolume = 0.42d;
    double truckCo2PerDistanceAndVolume = 0.31d;

    public String uuid, uuidFrom, uuidTo;
    public int distance, leadTime, capacity;
    public ConnectionType type;
    public int flow, cost;
    private final PriorityQueue<Movement> pendingMovementsArrive;
    private final PriorityQueue<Movement> pendingMovementsStart;

    public Edge(String uuid, String uuidFrom, String uuidTo, int distance, int leadTime, ConnectionType type, int capacity) {
        this.uuid = uuid;
        this.uuidFrom = uuidFrom;
        this.uuidTo = uuidTo;
        this.distance = distance;
        this.leadTime = leadTime;
        this.type = type;
        this.capacity = capacity;
        this.flow = 0;
        this.cost = Integer.MAX_VALUE;
        this.pendingMovementsArrive = new PriorityQueue<>(new MovementComparator());
        this.pendingMovementsStart = new PriorityQueue<>(new MovementComparator());
    }

    public Edge(String uuid, String uuidFrom, String uuidTo, int distance, int leadTime, ConnectionType type, int capacity, int cost, int flow) {
        this.uuid = uuid;
        this.uuidFrom = uuidFrom;
        this.uuidTo = uuidTo;
        this.distance = distance;
        this.leadTime = leadTime;
        this.type = type;
        this.capacity = capacity;
        this.flow = flow;
        this.cost = cost;
        this.pendingMovementsArrive = new PriorityQueue<>(new MovementComparator());
        this.pendingMovementsStart = new PriorityQueue<>(new MovementComparator());
    }

    public Edge(final Edge other) {
        this.uuid = other.uuid;
        this.uuidFrom = other.uuidFrom;
        this.uuidTo = other.uuidTo;
        this.distance = other.distance;
        this.leadTime = other.leadTime;
        this.type = other.type;
        this.capacity = other.capacity;
        this.flow = other.flow;
        this.cost = other.cost;
        this.pendingMovementsArrive = new PriorityQueue<>(new MovementComparator());
        this.pendingMovementsStart = new PriorityQueue<>(new MovementComparator());
    }

    public void addMovementArrive(final Movement movement) {
        pendingMovementsArrive.add(movement);
    }

    public void removeMovementArrive(final Movement movement) {
        pendingMovementsArrive.remove(movement);
    }

    public void addMovementStart(final Movement movement) {
        pendingMovementsStart.add(movement);
    }

    public void removeMovementStart(final Movement movement) {
        pendingMovementsStart.remove(movement);
    }

    public void computeCost(final Node from, final Node to, int amount, int day, int startDay, int endDay) {
        // default costs
        if (type == ConnectionType.PIPELINE) {
            cost = (int) Math.ceil(distance * (pipelineCostPerDistanceAndVolume + pipelineCo2PerDistanceAndVolume));
        } else if (type == ConnectionType.TRUCK) {
            cost = (int) Math.ceil(distance * (truckCostPerDistanceAndVolume + truckCo2PerDistanceAndVolume));
        } else if (type == ConnectionType.SEF) {
            cost = 0;
        }

        // penalty cases
        if (from.getClass() == Refinery.class) {
            Refinery refinery = (Refinery) from;
            if (amount > refinery.maxOutput) { // REFINERY_OVER_OUTPUT
                double penalty = (amount - refinery.maxOutput) * refinery.overOutputPenalty;
                cost += penalty;
            }
        } else if (from.getClass() == Tank.class) {
            Tank tank = (Tank) from;
            if (amount > tank.maxOutput) { // STORAGE_OVER_OUTPUT
                double penalty = (amount - tank.maxOutput) * tank.overOutputPenalty;
                cost += penalty;
            }
        } else if (from.getClass() == Customer.class) {
            Customer customer = (Customer) from;
        }

        if (to.getClass() == Refinery.class) {
            Refinery refinery = (Refinery) to;
        } else if (to.getClass() == Tank.class) {
            Tank tank = (Tank) to;
            if (amount > tank.maxInput) { // STORAGE_TYPE_OVER_INPUT
                double penalty = (amount - tank.maxInput) * tank.overInputPenalty;
                cost += penalty;
            }
        } else if (to.getClass() == Customer.class) {
            Customer customer = (Customer) to;
            if (amount > customer.maxInput) { // CUSTOMER_OVER_INPUT
                double penalty = (amount - customer.maxInput) * customer.overInputPenalty;
                cost += penalty;
            }
            if (day < startDay) { // CUSTOMER_EARLY_DELIVERY
                // add penalty
            } else if (endDay < day) { // CUSTOMER_LATE_DELIVERY
                // add penalty
            }
        }

        if (amount > capacity) {
            // add penalty
//            int penalty = (amount - capacity); // TODO(Alex Mirzea): complete this
        }
    }
}
