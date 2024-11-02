package org.example;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    Refinery refinerySource;
    Map<String, Node> nodes;
    Map<Node, Map<Node, Edge>> adjacencyList;

    public Graph() {
        nodes = new HashMap<>();
        adjacencyList = new HashMap<>();
        refinerySource = new Refinery();

        adjacencyList.put(refinerySource, new HashMap<>());
        nodes.put(refinerySource.uuid, refinerySource);
    }

    public Graph(Graph graph) {
        this.nodes = new HashMap<>();
        this.adjacencyList = new HashMap<>();

        for (Node node: graph.nodes.values()) {
            this.nodes.put(node.uuid, node);
            this.adjacencyList.put(node, new HashMap<>());
        }
    }

    public void addNode(Node node) {
        nodes.putIfAbsent(node.uuid, node);
        adjacencyList.putIfAbsent(node, new HashMap<>());

        // check if the added node is a Refinery node,
        // in this case the Refinery Source must be connected to it
        if (node.type == NodeType.RAFINERY) {
            // uuif for the edge and uuid from do not matter,
            // as the refinery source is used to complete the flow algorithm
            Edge rafinerySourceToRafinery = new Edge("", "1", node.uuid, 0, ((Refinery) node).stock, 1, 0);
            adjacencyList.get(refinerySource).put(node, rafinerySourceToRafinery);
        }
    }

    public void addEdge(Edge edge, Boolean maxCapacity) {
        Node nodeFrom = nodes.get(edge.uuidFrom);
        Node nodeTo = nodes.get(edge.uuidTo);

        // recompute the maximum capacity for the edge by comparing
        // the max output of the incoming node, the max input of the outgoing node,
        // and the intial capacity of the edge
        if (maxCapacity == true)
            edge.capacity = this.getMaxCapacityEdge(edge, nodeFrom, nodeTo);

        adjacencyList.get(nodeFrom).put(nodeTo, edge);
    }

    public int getMaxCapacityEdge(Edge edge, Node nodeFrom, Node nodeTo) {
        int nodeFromCapacity, nodeToCapacity;

        if (nodeFrom instanceof Refinery) {
            nodeFromCapacity = ((Refinery) nodeFrom).maxOutput;
        } else {
            nodeFromCapacity = ((Tank) nodeFrom).maxOutput;
        } 

        if (nodeTo instanceof Tank) {
            nodeToCapacity = ((Tank) nodeTo).maxInput;
        } else {
            nodeToCapacity = ((Client) nodeTo).maxInput;
        }
            
        return java.lang.Math.min(java.lang.Math.min(nodeFromCapacity, nodeToCapacity), edge.capacity);
    }

    public void display() {
        for (Map.Entry<Node, Map<Node, Edge>> entry : adjacencyList.entrySet()) {
            Node fromNode = entry.getKey();
            System.out.print(fromNode.name + " -> ");
            
            Map<Node, Edge> edges = entry.getValue();
            if (edges.isEmpty()) {
                System.out.print("No edges");
            } else {
                for (Edge edge : edges.values()) {
                    System.out.print(edge.uuidTo + "(flow: " + edge.flow + ", capacity: " + edge.capacity + 
                                     ", cost: " + edge.cost + 
                                     ", leadTime: " + edge.leadTime + ") ");
                }
            }
            System.out.println();
        }
    }

    public int getFlow(Node from, Node to) {
        if (adjacencyList.containsKey(from) && adjacencyList.get(from).containsKey(to)) {
            return adjacencyList.get(from).get(to).flow;
        }
        return 0;
    }

    public void updateFlow(Node from, Node to, double flowChange) {
        if (adjacencyList.containsKey(from) && adjacencyList.get(from).containsKey(to)) {
            Edge edge = adjacencyList.get(from).get(to);
            edge.flow += flowChange;

            // Update backward flow
            if (adjacencyList.containsKey(to) && adjacencyList.get(to).containsKey(from)) {
                Edge backwardEdge = adjacencyList.get(to).get(from);
                backwardEdge.flow -= flowChange;
            }
        }
    }

    // New method to find negative cycles using Bellman-Ford
    public List<Node> findNegativeCycle() { 
        Map<Node, Double> distances = new HashMap<>();
        Map<Node, Node> parents = new HashMap<>();
        List<Node> negativeCycle = new ArrayList<>();

        for (Node node: nodes.values()) {
            distances.put(node, Double.MAX_VALUE);
        }

        distances.put(refinerySource, 0.0);

        for (int i = 1; i < nodes.size(); i++) {
            Boolean updated = false;

            for (Node u : nodes.values()) {
                for (Map.Entry<Node, Edge> entry : adjacencyList.get(u).entrySet()) {
                    Node v = entry.getKey();
                    Edge edge = entry.getValue();
                    if (distances.get(u) != Double.MAX_VALUE && 
                        distances.get(u) + edge.cost < distances.get(v)) {
                        distances.put(v, distances.get(u) + edge.cost);
                        parents.put(v, u);
                        updated = true;
                    }
                }
            }

            if (updated == false)
                break;
        }

        for (Node u : nodes.values()) {
            for (Map.Entry<Node, Edge> entry : adjacencyList.get(u).entrySet()) {
                Node v = entry.getKey();
                Edge edge = entry.getValue();

                // check if a negative cycle exists
                if (distances.get(u) != Double.MAX_VALUE && 
                    distances.get(u) + edge.cost < distances.get(v)) {
                    negativeCycle.add(v);
                    Node curr = u;
                    while (curr != v) {
                        negativeCycle.add(curr);
                        curr = parents.get(curr);
                    }
                    Collections.reverse(negativeCycle);
                    // return the negative cycle
                    return negativeCycle; 
                }
            }
        }

        return null;
    }

    // Method to create a residual graph
    public Graph createResidualGraph() {
        Graph residualGraph = new Graph(this);
        
        for (Node from : adjacencyList.keySet()) {
            for (Map.Entry<Node, Edge> entry : adjacencyList.get(from).entrySet()) {
                Node to = entry.getKey();
                Edge edge = entry.getValue();

                // Add forward edge with residual capacity
                if (edge.capacity > edge.flow) {
                    Edge residualEdge = new Edge(edge.uuid, edge.uuidFrom, edge.uuidTo, 
                                                  edge.flow, edge.capacity - edge.flow, 
                                                  edge.cost, edge.leadTime);
                    residualGraph.addEdge(residualEdge, false);
                }

                // Add backward edge with flow as capacity
                if (edge.flow > 0) {
                    Edge backwardEdge = new Edge(edge.uuid, edge.uuidTo, edge.uuidFrom, 
                                                  -edge.flow, edge.flow, 
                                                  -edge.cost, edge.leadTime);
                    residualGraph.addEdge(backwardEdge, false);
                }
            }
        }

        return residualGraph;
    }

    // Method to push flow through a negative cycle
    public void pushFlowThroughCycle(List<Node> cycle, double flow) {
        for (int i = 0; i < cycle.size(); i++) {
            Node from = cycle.get(i);
            Node to = cycle.get((i + 1) % cycle.size()); // Wrap around

            updateFlow(from, to, flow);
        }
    }

    public int calculateTotalCost() {
        int totalCost = 0;
    
        for (Node from : adjacencyList.keySet()) {
            for (Edge edge : adjacencyList.get(from).values()) {
                totalCost += edge.cost * edge.flow; // Cost multiplied by flow gives total cost for that edge
            }
        }
    
        return totalCost;
    }

    public double calculateMinCostMaxFlow() {
        double totalCost = 0;

        while (true) {
            // Create residual graph
            Graph residualGraph = createResidualGraph();

            // Find a negative cycle
            List<Node> negativeCycle = residualGraph.findNegativeCycle();

            if (negativeCycle == null) {
                break; // No more negative cycles, we are done
            }

            // Calculate the flow that can be pushed through the cycle
            double flowToPush = Double.MAX_VALUE;

            for (int i = 0; i < negativeCycle.size(); i++) {
                Node from = negativeCycle.get(i);
                Node to = negativeCycle.get((i + 1) % negativeCycle.size());
                Edge edge = residualGraph.adjacencyList.get(from).get(to);

                if (edge != null) {
                    flowToPush = Math.min(flowToPush, edge.capacity - edge.flow);
                }
            }

            // Push flow through the cycle
            pushFlowThroughCycle(negativeCycle, flowToPush);
        }

        // Calculate the total cost based on the flows
        totalCost = calculateTotalCost();

        return totalCost;
    }
}
