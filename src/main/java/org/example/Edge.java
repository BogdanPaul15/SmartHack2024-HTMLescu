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
    private final PriorityQueue<Movement> pendingMovements;

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
        this.pendingMovements = new PriorityQueue<>(new MovementComparator());
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
        this.pendingMovements = new PriorityQueue<>(new MovementComparator());
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
        this.pendingMovements = new PriorityQueue<>(new MovementComparator());
    }

    public void addMovement(final Movement movement) {
        pendingMovements.add(movement);
    }

    public void removeMovement(final Movement movement) {
        pendingMovements.remove(movement);
    }

    public void computeCost(Node nodeFrom, Node nodeTo) {
        // default costs
        if (type == ConnectionType.PIPELINE) {
            cost = (int) Math.ceil(distance * (pipelineCostPerDistanceAndVolume + pipelineCo2PerDistanceAndVolume));
        } else if (type == ConnectionType.TRUCK) {
            cost = (int) Math.ceil(distance * (truckCostPerDistanceAndVolume + truckCo2PerDistanceAndVolume));
        } else if (type == ConnectionType.SEF) {
            cost = 0;
        }

        // penalty cases
        cost += (int) Math.ceil(nodeFrom.getMaxOutputPenalty());
        cost += (int) Math.ceil(nodeTo.getMaxInputPenalty());
    }
}
