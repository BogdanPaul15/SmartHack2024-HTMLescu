package org.example;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Ref;
import java.util.*;

public class Graph {
    Refinery refinerySource;
    Map<String, Node> nodes;
    Map<Node, Map<Node, Edge>> adjacencyList;
    private final List<Demand> demands;
    private final PriorityQueue<Movement> pendingMovements;
    private List<Movement> todayMovements;

    private List<Refinery> refineries;
    private List<Edge> refineriesEdges;

    public Graph() {
        this.nodes = new HashMap<>();
        this.adjacencyList = new HashMap<>();
        this.refinerySource = new Refinery();
        this.adjacencyList.put(refinerySource, new HashMap<>());
        this.nodes.put(refinerySource.uuid, refinerySource);
        this.demands = new ArrayList<>();
        this.pendingMovements = new PriorityQueue<>(new MovementComparator());
        this.todayMovements = new ArrayList<>();

        this.refineries = new ArrayList<>();
        this.refineriesEdges = new ArrayList<>();
    }

    public Graph(Graph graph) {
        this.nodes = new HashMap<>();
        this.adjacencyList = new HashMap<>();
        this.demands = new ArrayList<>();
        this.pendingMovements = new PriorityQueue<>(new MovementComparator());
        this.todayMovements = new ArrayList<>();

        for (final Node node : graph.nodes.values()) {
            Node copiedNode = getNode(node);
            this.nodes.put(copiedNode.uuid, copiedNode);
            this.adjacencyList.put(copiedNode, new HashMap<>());
        }

        for (final Map.Entry<Node, Map<Node, Edge>> entry : graph.adjacencyList.entrySet()) {
            final Node fromNode = this.nodes.get(entry.getKey().uuid);
            for (Map.Entry<Node, Edge> edgeEntry : entry.getValue().entrySet()) {
                final Node toNode = this.nodes.get(edgeEntry.getKey().uuid);
                final Edge edgeCopy = new Edge(edgeEntry.getValue());
                this.adjacencyList.get(fromNode).put(toNode, edgeCopy);
            }
        }
    }

    public void addNode(Node node) {
        nodes.putIfAbsent(node.uuid, node);
        adjacencyList.putIfAbsent(node, new HashMap<>());

        if (node.getClass() == Refinery.class) {
            refineries.add((Refinery) node);
        }
    }

    private Node getNode(Node node) {
        if (node.getClass() == Refinery.class) {
            return new Refinery((Refinery) node);
        }
        if (node.getClass() == Tank.class) {
            return new Tank((Tank) node);
        }
        if (node.getClass() == Customer.class) {
            return new Customer((Customer) node);
        }

        return null;
    }

    public Refinery getRefinery(final String id) {
        return (Refinery) nodes.get(id);
    }

    public List<Refinery> getAllRefineries() {
        List<Refinery> refineries = new ArrayList<>();
        for (final Node node : nodes.values()) {
            if (node.getClass() == Refinery.class) {
                refineries.add((Refinery) node);
            }
        }
        return refineries;
    }

    public Tank getTank(final String id) {
        return (Tank) nodes.get(id);
    }

    public Customer getCustomer(final String id) {
        return (Customer) nodes.get(id);
    }

    public void addEdge(final Edge edge, final Boolean maxCapacity) {
        Node nodeFrom = nodes.get(edge.uuidFrom);
        Node nodeTo = nodes.get(edge.uuidTo);

        // compute the cost considering the nodes stock, capacity
        // and other metrics related to them
        edge.computeCost(nodeFrom, nodeTo);

        if (nodeFrom != null && nodeTo != null && nodeFrom.getClass() == Refinery.class && nodeTo.getClass() == Refinery.class) {
            refineriesEdges.add(edge);
        }

        // recompute the maximum capacity for the edge by comparing
        // the max output of the incoming node, the max input of the outgoing node,
        // and the intial capacity of the edge
        if (maxCapacity) edge.capacity = this.getMaxCapacityEdge(edge, nodeFrom, nodeTo);

        adjacencyList.get(nodeFrom).put(nodeTo, edge);
    }

    public Edge getEdge(final Node from, final Node to) {
        return adjacencyList.get(from).get(to);
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
            nodeToCapacity = ((Customer) nodeTo).maxInput;
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

    public void addDemand(final Demand demand) {
        demands.add(demand);
    }

    public List<Demand> getDemands() {
        return demands;
    }

    public void addMovement(final Movement movement) {
        pendingMovements.add(movement);
        Node from = nodes.get(movement.getFromId());
        Node to = nodes.get(movement.getToId());
        Edge edge = adjacencyList.get(from).get(to);
        edge.addMovement(movement);

        if (refinerySource.uuid.equals(from.uuid)) {
            edge.capacity -= movement.getAmount();
        }
    }

    public void solveMovements(final int day) {
        todayMovements = new ArrayList<>();
        for (final Movement movement : pendingMovements) {
            if (movement.getArrivalDay() == day) {
                Node to = nodes.get(movement.getToId());
                if (to.getClass() == Tank.class) {
                    Tank tank = (Tank) to;
                    tank.stock += movement.getAmount();
                } else if (to.getClass() == Refinery.class) {
                    Refinery refinery = (Refinery) to;
                    refinery.stock += movement.getAmount();
                }

                Node from = nodes.get(movement.getFromId());
                Edge edge = adjacencyList.get(from).get(to);
                pendingMovements.remove(movement);
                edge.removeMovement(movement);

                todayMovements.add(movement);
            }
        }
    }

    public List<Movement> getTodayMovements() {
        return todayMovements;
    }

    public int getFlow(final Node from, final Node to) {
        if (adjacencyList.containsKey(from) && adjacencyList.get(from).containsKey(to)) {
            return adjacencyList.get(from).get(to).flow;
        }
        return 0;
    }

    public void updateFlow(final Node from, final Node to, final int flowChange) {
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
        Map<Node, Integer> distances = new HashMap<>();
        Map<Node, Node> parents = new HashMap<>();
        List<Node> negativeCycle = new ArrayList<>();

        for (Node node : nodes.values()) {
            distances.put(node, Integer.MAX_VALUE);
        }

        distances.put(refinerySource, 0);

        for (int i = 1; i < nodes.size(); i++) {
            Boolean updated = false;

            for (Node u : nodes.values()) {
                for (Map.Entry<Node, Edge> entry : adjacencyList.get(u).entrySet()) {
                    Node v = entry.getKey();
                    Edge edge = entry.getValue();
                    if (distances.get(u) != Integer.MAX_VALUE &&
                            distances.get(u) + edge.cost < distances.get(v)) {
                        distances.put(v, distances.get(u) + edge.cost);
                        parents.put(v, u);
                        updated = true;
                    }
                }
            }

            if (updated == false) break;
        }

        for (Node u : nodes.values()) {
            for (Map.Entry<Node, Edge> entry : adjacencyList.get(u).entrySet()) {
                Node v = entry.getKey();
                Edge edge = entry.getValue();

                // check if a negative cycle exists
                if (distances.get(u) != Integer.MAX_VALUE &&
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
                    Edge residualEdge = new Edge(edge.uuid, edge.uuidFrom, edge.uuidTo, edge.distance, edge.leadTime,
                            ConnectionType.PIPELINE, edge.capacity - edge.flow,
                            edge.cost, edge.flow);
                    residualGraph.addEdge(residualEdge, false);
                }

                // Add backward edge with flow as capacity
                if (edge.flow > 0) {
                    Edge backwardEdge = new Edge(edge.uuid, edge.uuidTo, edge.uuidFrom, edge.distance, edge.leadTime,
                            ConnectionType.PIPELINE, edge.flow, -edge.cost, -edge.flow);
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
            int pathFlow = bfsFindAugmentingPath(source, sink, parentMap);

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
                    Edge reverseEdge = new Edge(edge.uuid, edge.uuidTo, edge.uuidFrom, edge.distance, edge.leadTime, ConnectionType.PIPELINE, pathFlow, edge.cost, -pathFlow);
                    adjacencyList.get(current).put(parent, reverseEdge);
                }
                current = parent;
            }
        }

        return maxFlow;
    }

    private int bfsFindAugmentingPath(Node source, Node sink, Map<Node, Edge> parentMap) {
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
                        int pathFlow = Integer.MAX_VALUE;
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
    public void pushFlowThroughCycle(List<Node> cycle, int flow) {
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
                    Edge reverseEdge = new Edge(edge.uuid, edge.uuidTo, edge.uuidFrom, edge.distance, edge.leadTime, ConnectionType.PIPELINE, flow, -edge.cost, -flow);
                    reverseEdge.flow = -flow; // Resetting the flow for consistency
                    adjacencyList.get(to).put(from, reverseEdge);
                }
            }
        }
    }

    public List<Edge> calculateTotalCost() {
        // int totalCost = 0;
        List<Edge> pathToMinEdges = new ArrayList<>();

        for (Node from : adjacencyList.keySet()) {
            for (Edge edge : adjacencyList.get(from).values()) {
                if (edge.flow > 0) {
                    pathToMinEdges.add(edge);
                }
                // totalCost += edge.cost * Math.max(0, edge.flow); // Ensures flow is non-negative
            }
        }
        return pathToMinEdges;
    }

    public List<Edge> calculateMinCostMaxFlow(Node sink) {
        int totalCost = 0;
        int maxFlow = edmondsKarp(refinerySource, sink);

        while (true) {
            Graph residualGraph = createResidualGraph();
            List<Node> negativeCycle = residualGraph.findNegativeCycle();

            if (negativeCycle == null) break;

            int flowToPush = Integer.MAX_VALUE;
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

        List<Edge> pathToMinEdges = calculateTotalCost();
        return pathToMinEdges;
    }

    public void resetFlows() {
        for (Node from : adjacencyList.keySet()) {
            for (Edge edge : adjacencyList.get(from).values()) {
                edge.flow = 0;
            }
        }
    }

}
