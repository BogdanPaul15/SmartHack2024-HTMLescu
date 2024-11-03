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

    public Graph() {
        nodes = new HashMap<>();
        adjacencyList = new HashMap<>();
        refinerySource = new Refinery();
        adjacencyList.put(refinerySource, new HashMap<>());
        nodes.put(refinerySource.uuid, refinerySource);
        demands = new ArrayList<>();
        pendingMovements = new PriorityQueue<>(new MovementComparator());
    }

    public void addNode(Node node) {
        nodes.putIfAbsent(node.uuid, node);
        adjacencyList.putIfAbsent(node, new HashMap<>());

        // check if the added node is a Refinery node,
        // in this case the Refinery Source must be connected to it
        if (node.type == NodeType.REFINERY) {
            // uuid for the edge and uuid from do not matter,
            // as the refinery source is used to complete the flow algorithm
            Edge rafinerySourceToRafinery = new Edge("", refinerySource.uuid, node.uuid, 0, 0 , ConnectionType.SEF, ((Refinery) node).stock);
            adjacencyList.get(refinerySource).put(node, rafinerySourceToRafinery);
        }
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

    public void addEdge(Edge edge) {
        Node nodeFrom = nodes.get(edge.uuidFrom);
        Node nodeTo = nodes.get(edge.uuidTo);

        // recompute the maximum capacity for the edge by comparing
        // the max output of the incoming node, the max input of the outgoing node,
        // and the intial capacity of the edge
        edge.capacity = this.getMaxCapacityEdge(edge, nodeFrom, nodeTo);

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
}
