package org.example;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Graph {
    Refinery refinerySource;
    Map<String, Node> nodes;
    Map<Node, Map<Node, Edge>> adjacencyList;
    private final Set<Demand> demands;
    private final Map<String, Set<Demand>> customerDemands;

    public Graph() {
        nodes = new HashMap<>();
        adjacencyList = new HashMap<>();
        refinerySource = new Refinery();
        demands = new HashSet<>();
        customerDemands = new HashMap<>();

        adjacencyList.put(refinerySource, new HashMap<>());
        nodes.put(refinerySource.uuid, refinerySource);
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
        customerDemands.putIfAbsent(demand.getCustomerId(), new HashSet<>());
        customerDemands.get(demand.getCustomerId()).add(demand);
    }

    public Set<Demand> getDemands() {
        return demands;
    }

    public Set<Demand> getCustomerDemands(final String customerId) {
        return customerDemands.get(customerId);
    }
}
