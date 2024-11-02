package org.example;

public class Edge {
    public String uuid, uuidFrom, uuidTo;
    public int distance, leadTime, capacity;
    public ConnectionType type;
    public long flow, cost;

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
    }

    public void ComputeCost(/* TODO(Alex Mirzea): add data on which to make the computing */) {

    }
}
