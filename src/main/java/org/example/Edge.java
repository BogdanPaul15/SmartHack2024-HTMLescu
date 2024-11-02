package org.example;

public class Edge {
    public String uuid;
    public String uuidFrom;
    public String uuidTo;
    public int flow = 0;
    public int capacity;
    public int cost;
    public int leadTime;

    public Edge(String uuid, String uuidFrom, String uuidTo, int flow, int capacity, int cost, int leadTime) {
        this.uuid = uuid;
        this.uuidFrom = uuidFrom;
        this.uuidTo = uuidTo;
        this.flow = flow;
        this.capacity = capacity;
        this.cost = cost;
        this.leadTime = leadTime;
    }
}
