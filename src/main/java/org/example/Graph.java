package org.example;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

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

    private Node getNode(Node node) {
        if (node.getClass() == Refinery.class) {
            return new Refinery((Refinery) node);
        }
        if (node.getClass() == Tank.class) {
            return new Tank((Tank) node);
        }
        if (node.getClass() == Client.class) {
            return new Client((Client) node);
        }

        return null;
    }

    public Graph(Graph graph) {
        this.nodes = new HashMap<>();
        this.adjacencyList = new HashMap<>();
    
        for (Node node : graph.nodes.values()) {
            Node copiedNode = getNode(node); // Make sure Node has a copy constructor or deep copy method
            this.nodes.put(copiedNode.uuid, copiedNode);
            this.adjacencyList.put(copiedNode, new HashMap<>());
        }
    
        // Deep copy each edge in the adjacency list
        for (Map.Entry<Node, Map<Node, Edge>> entry : graph.adjacencyList.entrySet()) {
            Node fromNode = this.nodes.get(entry.getKey().uuid);
            for (Map.Entry<Node, Edge> edgeEntry : entry.getValue().entrySet()) {
                Node toNode = this.nodes.get(edgeEntry.getKey().uuid);
                Edge edgeCopy = new Edge(edgeEntry.getValue()); // Make sure Edge has a copy constructor
                this.adjacencyList.get(fromNode).put(toNode, edgeCopy);
            }
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
            Edge rafinerySourceToRafinery = new Edge("", "1", node.uuid, 0, ((Refinery) node).stock, 0, 0);
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

    public double getMaxCapacityEdge(Edge edge, Node nodeFrom, Node nodeTo) {
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

    public double getFlow(Node from, Node to) {
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

    public int edmondsKarp(Node source, Node sink) {
        int maxFlow = 0;

        while (true) {
            Map<Node, Edge> parentMap = new HashMap<>();
            double pathFlow = bfsFindAugmentingPath(source, sink, parentMap);

            if (pathFlow == 0) break;

            maxFlow += pathFlow;
            Node current = sink;

            while (current != source) {
                Node parent = parentMap.get(current).uuidFrom.equals(current.uuid) ? current : nodes.get(parentMap.get(current).uuidFrom);
                Edge edge = adjacencyList.get(parent).get(current);
                edge.flow += pathFlow;

                if (adjacencyList.get(current).containsKey(parent)) {
                    adjacencyList.get(current).get(parent).flow -= pathFlow;
                } else {
                    Edge reverseEdge = new Edge(edge.uuid, edge.uuidTo, edge.uuidFrom, -pathFlow, pathFlow, edge.cost, edge.leadTime);
                    adjacencyList.get(current).put(parent, reverseEdge);
                }
                current = parent;
            }
        }

        return maxFlow;
    }

    private double bfsFindAugmentingPath(Node source, Node sink, Map<Node, Edge> parentMap) {
        Map<Node, Boolean> visited = new HashMap<>();
        Queue<Node> queue = new LinkedList<>();
        queue.add(source);
        visited.put(source, true);
    
        while (!queue.isEmpty()) {
            Node node = queue.poll();
    
            for (Map.Entry<Node, Edge> entry : adjacencyList.get(node).entrySet()) {
                Node neighbor = entry.getKey();
                Edge edge = entry.getValue();
    
                if (!visited.containsKey(neighbor) && edge.capacity > edge.flow) {
                    visited.put(neighbor, true);
                    parentMap.put(neighbor, edge);
    
                    if (neighbor.equals(sink)) {
                        double pathFlow = Double.MAX_VALUE;
                        Node current = sink;
    
                        while (current != source) {
                            Edge e = parentMap.get(current);
                            pathFlow = Math.min(pathFlow, e.capacity - e.flow);
                            current = nodes.get(e.uuidFrom);
                        }
                        return pathFlow;
                    }
                    queue.add(neighbor);
                }
            }
        }
        return 0;
    }
    

    // Method to push flow through a negative cycle
    public void pushFlowThroughCycle(List<Node> cycle, double flow) {
        for (int i = 0; i < cycle.size(); i++) {
            Node from = cycle.get(i);
            Node to = cycle.get((i + 1) % cycle.size()); // Wrap around
    
            if (adjacencyList.containsKey(from) && adjacencyList.get(from).containsKey(to)) {
                Edge edge = adjacencyList.get(from).get(to);
                edge.flow += flow;
    
                if (adjacencyList.get(to).containsKey(from)) {
                    Edge reverseEdge = adjacencyList.get(to).get(from);
                    reverseEdge.flow -= flow;
                } else {
                    Edge reverseEdge = new Edge(edge.uuid, edge.uuidTo, edge.uuidFrom, -flow, flow, -edge.cost, edge.leadTime);
                    reverseEdge.flow = -flow; // Resetting the flow for consistency
                    adjacencyList.get(to).put(from, reverseEdge);
                }
            }
        }
    }
    

    public int calculateTotalCost() {
        int totalCost = 0;
    
        for (Node from : adjacencyList.keySet()) {
            for (Edge edge : adjacencyList.get(from).values()) {
                totalCost += edge.cost * Math.max(0, edge.flow); // Ensures flow is non-negative
            }
        }
        return totalCost;
    }
    

    public double calculateMinCostMaxFlow(Node sink) {
        double totalCost = 0;
        double maxFlow = edmondsKarp(refinerySource, sink);
    
        while (true) {
            Graph residualGraph = createResidualGraph();
            List<Node> negativeCycle = residualGraph.findNegativeCycle();
    
            if (negativeCycle == null) {
                break;
            }
    
            double flowToPush = Double.MAX_VALUE;
            for (int i = 0; i < negativeCycle.size(); i++) {
                Node from = negativeCycle.get(i);
                Node to = negativeCycle.get((i + 1) % negativeCycle.size());
                Edge edge = residualGraph.adjacencyList.get(from).get(to);
    
                if (edge != null) {
                    flowToPush = Math.min(flowToPush, edge.capacity - edge.flow);
                }
            }
    
            pushFlowThroughCycle(negativeCycle, flowToPush);
        }
    
        totalCost = calculateTotalCost();
        return totalCost;
    }
    
    public void resetFlows() {
        for (Node from : adjacencyList.keySet()) {
            for (Edge edge : adjacencyList.get(from).values()) {
                edge.flow = 0;
            }
        }
    }
}
