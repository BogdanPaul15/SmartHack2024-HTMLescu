package org.example;

public class Edge {
    public String uuid;
    public String uuidFrom;
    public String uuidTo;
    public double flow = 0;
    public double capacity;
    public int cost;
    public int leadTime;

    public Edge(String uuid, String uuidFrom, String uuidTo, double flow, double capacity, int cost, int leadTime) {
        this.uuid = uuid;
        this.uuidFrom = uuidFrom;
        this.uuidTo = uuidTo;
        this.flow = flow;
        this.capacity = capacity;
        this.cost = cost;
        this.leadTime = leadTime;
    }

    public Edge(Edge other) {
        this.uuid = other.uuid;
        this.uuidFrom = other.uuidFrom;
        this.uuidTo = other.uuidTo;
        this.flow = other.flow;
        this.capacity = other.capacity;
        this.cost = other.cost;
        this.leadTime = other.leadTime;
    }
}
