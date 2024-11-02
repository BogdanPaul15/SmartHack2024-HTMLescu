package org.example;
import java.util.HashMap;
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
}
