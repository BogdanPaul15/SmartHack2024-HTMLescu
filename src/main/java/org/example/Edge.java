package org.example;

import java.util.PriorityQueue;

public class Edge {
    public String uuid, uuidFrom, uuidTo;
    public int distance, leadTime, capacity;
    public ConnectionType type;
    public long flow, cost;
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
        this.cost = Long.MAX_VALUE;
        this.pendingMovements = new PriorityQueue<>(new MovementComparator());
    }

    public void addMovement(final Movement movement) {
        pendingMovements.add(movement);
    }

    public void removeMovement(final Movement movement) {
        pendingMovements.remove(movement);
    }

    public void ComputeCost(/* TODO(Alex Mirzea): add data on which to make the computing */) {
        // default costs

        // penalty cases
    }
}
