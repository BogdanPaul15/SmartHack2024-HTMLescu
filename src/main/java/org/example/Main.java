package org.example;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class Main {
    public static void main(String[] args) throws Exception {
        Graph graph = new Graph();
        Refinery refinery = new Refinery("2", "Refinery 0", 500, 300, 250, 50, 30, 20, 100, 10, 200);
        Tank tank = new Tank("3", "Tank 0", 1000, 150, 100, 40, 20, 10, 15, 500);
        Client client = new Client("4", "Customer 0", 120, 25, 60, 15);

        graph.addNode(refinery);
        graph.addNode(tank);
        graph.addNode(client);

        Edge refineryToTank = new Edge("2", "2", "3", 0, 1, 10, 0);
        Edge tankToClient = new Edge("3", "3", "4", 0, 1, 10, 0);

        graph.addEdge(refineryToTank, true);
        graph.addEdge(tankToClient, true);

        graph.display();
        System.out.println();

        graph.createResidualGraph().display();

        // // Calculate the min-cost max-flow
        double minCost = graph.calculateMinCostMaxFlow(client);
        System.out.println("Minimum cost for the package: " + minCost);
    }
}
