public class Edge {
    public int flow;
    public int capacity;
    public int cost;
    public int leadTime;

    public Edge(int capacity, int cost, int leadTime) {
        this.flow = 0;
        this.capacity = capacity;
        this.cost = cost;
        this.leadTime = leadTime;
    }
}
