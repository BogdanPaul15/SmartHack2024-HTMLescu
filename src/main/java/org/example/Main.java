package org.example;

public class Main {
    public static void main(String[] args) throws Exception {
        // Initialize a new graph
        Graph graph = new Graph();

        // Create multiple nodes
        Refinery refinery1 = new Refinery("1", "Refinery 1", 300, 200, 150, 50, 10, 5, 80, 15, 100);
        Refinery refinery2 = new Refinery("2", "Refinery 2", 400, 250, 200, 60, 15, 10, 90, 20, 150);
        Tank tank1 = new Tank("3", "Tank 1", 600, 120, 80, 20, 15, 5, 10, 200);
        Tank tank2 = new Tank("4", "Tank 2", 500, 100, 60, 15, 10, 5, 10, 150);
        Client client1 = new Client("5", "Client 1", 100, 20, 40, 10);
        Client client2 = new Client("6", "Client 2", 200, 30, 50, 15);

        // Add nodes to the graph
        graph.addNode(refinery1);
        graph.addNode(refinery2);
        graph.addNode(tank1);
        graph.addNode(tank2);
        graph.addNode(client1);
        graph.addNode(client2);

        // Create edges between nodes
        Edge refinery1ToTank1 = new Edge("1", "1", "3", 0, 100, 5, 0);  // Edge from Refinery 1 to Tank 1
        Edge refinery2ToTank1 = new Edge("2", "2", "3", 0, 150, 4, 0);  // Edge from Refinery 2 to Tank 1
        Edge refinery1ToTank2 = new Edge("3", "1", "4", 0, 80, 6, 0);   // Edge from Refinery 1 to Tank 2
        Edge refinery2ToTank2 = new Edge("4", "2", "4", 0, 100, 5, 0);  // Edge from Refinery 2 to Tank 2
        Edge tank1ToClient1 = new Edge("5", "3", "5", 0, 50, 8, 0);     // Edge from Tank 1 to Client 1
        Edge tank1ToClient2 = new Edge("6", "3", "6", 0, 30, 7, 0);     // Edge from Tank 1 to Client 2
        Edge tank2ToClient1 = new Edge("7", "4", "5", 0, 40, 9, 0);     // Edge from Tank 2 to Client 1
        Edge tank2ToClient2 = new Edge("8", "4", "6", 0, 20, 10, 0);    // Edge from Tank 2 to Client 2

        // Add edges to the graph
        graph.addEdge(refinery1ToTank1, true);
        graph.addEdge(refinery2ToTank1, true);
        graph.addEdge(refinery1ToTank2, true);
        graph.addEdge(refinery2ToTank2, true);
        graph.addEdge(tank1ToClient1, true);
        graph.addEdge(tank1ToClient2, true);
        graph.addEdge(tank2ToClient1, true);
        graph.addEdge(tank2ToClient2, true);

        // Calculate the min-cost max-flow to Client 1
        double minCostToClient1 = graph.calculateMinCostMaxFlow(client1);
        System.out.println("Minimum cost for the package to Client 1: " + minCostToClient1);

        graph.resetFlows(); // Reset the flows to 0

        double minCostToClient2WithFlow = graph.calculateMinCostMaxFlow(client2);
        System.out.println("Minimum cost for the package to Client 2 with flow: " + minCostToClient2WithFlow);
    }
}
