package org.example;

import com.fasterxml.jackson.databind.JsonNode;

public class Main {
<<<<<<< HEAD
    public static void main(String[] args) {
        System.out.println("Hello world!");

        ServerAPI.getInstance();
        JsonNode node = ServerAPI.getInstance().playRound();
        ServerAPI.getInstance().endSession();
=======
    public static void main(String[] args) throws Exception {
        Graph graph = new Graph();
        
        Refinery refinery = new Refinery("2", "Refinery 0", 500, 300, 250, 50, 30, 20, 100, 10, 200);
        Tank tank = new Tank("3", "Tank 0", 1000, 150, 100, 40, 20, 10, 15, 500);
        Client client = new Client("4", "Customer 0", 120, 25, 60, 15);

        graph.addNode(refinery);
        graph.addNode(tank);
        graph.addNode(client);

        Edge refineryToTank = new Edge("2", "2", "3", 0, 0, 0, 0);
        Edge tankToClient = new Edge("3", "3", "4", 0, 0, 0, 0);

        graph.addEdge(refineryToTank);
        graph.addEdge(tankToClient);

        graph.display();
>>>>>>> 8b30a22 (added graph classes)
    }
}
