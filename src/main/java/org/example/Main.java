package org.example;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.opencsv.*;
import com.opencsv.exceptions.CsvException;

public class Main {
    private static Graph graph;

    public static void main(String[] args) throws Exception {
        graph = new Graph();

    //    // Create multiple nodes
    //    Refinery refinery1 = new Refinery("1", "Refinery 1", 300, 200, 150, 50, 10, 5, 80, 15, 100);
    //    Refinery refinery2 = new Refinery("2", "Refinery 2", 400, 250, 200, 60, 15, 10, 90, 20, 150);
    //    Tank tank1 = new Tank("3", "Tank 1", 600, 120, 80, 20, 15, 5, 10, 200);
    //    Tank tank2 = new Tank("4", "Tank 2", 500, 100, 60, 15, 10, 5, 10, 150);
    //    Customer client1 = new Customer("5", "Client 1", 100, 20, 40, 10);
    //    Customer client2 = new Customer("6", "Client 2", 200, 30, 50, 15);

    //    // Add nodes to the graph
    //    graph.addNode(refinery1);
    //    graph.addNode(refinery2);
    //    graph.addNode(tank1);
    //    graph.addNode(tank2);
    //    graph.addNode(client1);
    //    graph.addNode(client2);

    //    // Create edges between nodes
    //    Edge refinery1ToTank1 = new Edge("1", "1", "3", 0, 0, ConnectionType.PIPELINE, 100, 5, 0);  // Edge from Refinery 1 to Tank 1
    //    Edge refinery2ToTank1 = new Edge("2", "2", "3", 0, 0, ConnectionType.PIPELINE, 150, 4, 0);  // Edge from Refinery 2 to Tank 1
    //    Edge refinery1ToTank2 = new Edge("3", "1", "4", 0, 0, ConnectionType.PIPELINE, 80, 6, 0);   // Edge from Refinery 1 to Tank 2
    //    Edge refinery2ToTank2 = new Edge("4", "2", "4", 0, 0, ConnectionType.PIPELINE, 100, 5, 0);  // Edge from Refinery 2 to Tank 2
    //    Edge tank1ToClient1 = new Edge("5", "3", "5", 0, 0, ConnectionType.TRUCK, 50, 8, 0);     // Edge from Tank 1 to Client 1
    //    Edge tank1ToClient2 = new Edge("6", "3", "6", 0, 0, ConnectionType.TRUCK, 30, 7, 0);     // Edge from Tank 1 to Client 2
    //    Edge tank2ToClient1 = new Edge("7", "4", "5", 0, 0, ConnectionType.TRUCK, 40, 9, 0);     // Edge from Tank 2 to Client 1
    //    Edge tank2ToClient2 = new Edge("8", "4", "6", 0, 0, ConnectionType.TRUCK, 20, 10, 0);

    //    // Add edges to the graph
    //    graph.addEdge(refinery1ToTank1, true);
    //    graph.addEdge(refinery2ToTank1, true);
    //    graph.addEdge(refinery1ToTank2, true);
    //    graph.addEdge(refinery2ToTank2, true);
    //    graph.addEdge(tank1ToClient1, true);
    //    graph.addEdge(tank1ToClient2, true);
    //    graph.addEdge(tank2ToClient1, true);
    //    graph.addEdge(tank2ToClient2, true);

       // Calculate the min-cost max-flow to Client 1

    //    List<Edge> lista1 = graph.calculateMinCostMaxFlow(client1);
    //    System.out.println(lista1);

    //    graph.resetFlows(); // Reset the flows to 0

    //     List<Edge> lista2 = graph.calculateMinCostMaxFlow(client2);
    //     System.out.println(lista2);

        // for (Demand demand : graph.getDemands()) {
        //     List<Edge> edges = graph.calculateMinCostMaxFlow(graph.getCustomer(demand.getCustomerId()));
        //     int currentDay = ServerAPI.getInstance().getDay();
        //     int totalPackets = demand.getAmount();

        //     int startDay = currentDay;
        //     for (Edge edge : edges) {
        //         for (int day = 0; day < edge.leadTime; day++) {
        //             int packets = packetsPerDay(day + 1, edge.leadTime, totalPackets);
        //             Movement movement = new Movement(edge.uuid, packets, startDay + edge.leadTime, edge.uuidFrom, edge.uuidTo, startDay);
        //             graph.addMovementStart(movement);
        //         }
        //     }
        //     graph.resetFlows(); // this could break the code
        // }

        // Example usage: Create Movement object or add to your logic as needed
        // Movement movement = new Movement(...);  // Customize based on your Movement logic

        // N = ziua in care am primit pachetul (ziua curenta) + leadTime



        populate();
        ServerAPI serverAPI = ServerAPI.getInstance();
        for (int i = 0; i < 42; i++) {
            playRound();
        }
        serverAPI.endSession();
    }

    private static void playRound() {
        pumpRefineries();
        List<Movement> movements = graph.getTodayMovements();
        ServerAPI serverAPI = ServerAPI.getInstance();
        JsonNode node = serverAPI.playRound(movements);
        for (JsonNode demandNode : node.get("demand")) {
            String customerId = demandNode.get("customerId").asText();
            int amount = demandNode.get("amount").asInt();
            int postDay = demandNode.get("postDay").asInt();
            int startDay = demandNode.get("startDay").asInt();
            int endDay = demandNode.get("endDay").asInt();

            Demand demand = new Demand(customerId, amount, postDay, startDay, endDay);
            graph.addDemand(demand);
        }

        decideMovements();
        graph.solveMovements(ServerAPI.getInstance().getDay());
    }

    private static void decideMovements() {
        for (Demand demand : graph.getDemands()) {
            List<Edge> edges = graph.calculateMinCostMaxFlow(graph.getCustomer(demand.getCustomerId()));
            int currentDay = ServerAPI.getInstance().getDay();
            int totalPackets = demand.getAmount();

            int startDay = currentDay;
            for (Edge edge : edges) {
                for (int day = 0; day < edge.leadTime; day++) {
                    int packets = packetsPerDay(day + 1, edge.leadTime, totalPackets);
                    Movement movement = new Movement(edge.uuid, packets, startDay + edge.leadTime, edge.uuidFrom, edge.uuidTo, startDay);
                    graph.addMovementStart(movement);
                }
            }
            graph.resetFlows(); // this could break the code
        }
    }

    private static void pumpRefineries() {
        Refinery sef = graph.refinerySource;
        List<Refinery> refineries = graph.getAllRefineries();
        for (final Refinery refinery : refineries) {
            if (refinery.uuid.equals(graph.refinerySource.uuid)) continue;
            refinery.stock += refinery.production;
            Edge edge = graph.getEdge(sef, refinery);
            edge.capacity += refinery.production;
        }
    }

    private static void populate() {
        populateRafineries();
        populateTanks();
        populateCustomers();
        populateConnections();
    }

    private static void populateRafineries() {
        String csvFile = "src/main/data/refineries.csv";
        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(csvFile)).withCSVParser(parser).build()) {
            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                String id = nextLine[0];
                String name = nextLine[1];
                int capacity = Integer.parseInt(nextLine[2]);
                int maxOutput = Integer.parseInt(nextLine[3]);
                int production = Integer.parseInt(nextLine[4]);
                double overflowPenalty = Double.parseDouble(nextLine[5]);
                double underflowPenalty = Double.parseDouble(nextLine[6]);
                double overOutputPenalty = Double.parseDouble(nextLine[7]);
                double productionCost = Double.parseDouble(nextLine[8]);
                double productionCO2 = Double.parseDouble(nextLine[9]);
                int initialStock = Integer.parseInt(nextLine[10]);

                Refinery refinery = new Refinery(id, name, capacity, maxOutput, production,
                        overflowPenalty, underflowPenalty,
                        overOutputPenalty, productionCost, productionCO2, initialStock);
                graph.addNode(refinery);
                Edge edge = new Edge("sef", "sef", refinery.uuid, 0, 0, ConnectionType.SEF, refinery.stock, 0, 0);
                graph.addEdge(edge, false); // False because you want to have the given capacity, not the processed one
            }
        } catch (IOException | CsvException e ) {
            e.printStackTrace();
        }
    }

    private static void populateTanks() {
        String csvFile = "src/main/data/tanks.csv";
        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(csvFile)).withCSVParser(parser).build()) {
            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                String id = nextLine[0];
                String name = nextLine[1];
                int capacity = Integer.parseInt(nextLine[2]);
                int maxInput = Integer.parseInt(nextLine[3]);
                int maxOutput = Integer.parseInt(nextLine[4]);
                double overflowPenalty = Double.parseDouble(nextLine[5]);
                double underflowPenalty = Double.parseDouble(nextLine[6]);
                double overInputPenalty = Double.parseDouble(nextLine[7]);
                double overOutputPenalty = Double.parseDouble(nextLine[8]);
                int initialStock = Integer.parseInt(nextLine[9]);

                Tank tank = new Tank(id, name, capacity, maxInput, maxOutput,
                        overflowPenalty, underflowPenalty, overInputPenalty,
                        overOutputPenalty, initialStock);
                graph.addNode(tank);
            }
        } catch (IOException | CsvException e ) {
            e.printStackTrace();
        }
    }

    private static void populateConnections() {
        String csvFile = "src/main/data/connections.csv";
        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(csvFile)).withCSVParser(parser).build()) {
            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                String id = nextLine[0];
                String fromId = nextLine[1];
                String toId = nextLine[2];
                int distance = Integer.parseInt(nextLine[3]);
                int leadTime = Integer.parseInt(nextLine[4]);
                ConnectionType type = nextLine[5].equals("TRUCK") ? ConnectionType.TRUCK : nextLine[5].equals("PIPELINE") ? ConnectionType.PIPELINE : ConnectionType.UNKNOWN;
                int capacity = Integer.parseInt(nextLine[6]);

                Edge edge = new Edge(id, fromId, toId, distance, leadTime, type, capacity);
                graph.addEdge(edge, true);
            }
        } catch (IOException | CsvException e ) {
            e.printStackTrace();
        }
    }

    private static void populateCustomers() {
        String csvFile = "src/main/data/customers.csv";
        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(csvFile)).withCSVParser(parser).build()) {
            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                String id = nextLine[0];
                String name = nextLine[1];
                int maxInput = Integer.parseInt(nextLine[2]);
                double overInputPenalty = Double.parseDouble(nextLine[3]);
                double lateDeliveryPenalty = Double.parseDouble(nextLine[4]);
                double earlyDeliveryPenalty = Double.parseDouble(nextLine[5]);

                Customer customer = new Customer(id, name, maxInput, overInputPenalty, lateDeliveryPenalty, earlyDeliveryPenalty);
                graph.addNode(customer);
            }
        } catch (IOException | CsvException e ) {
            e.printStackTrace();
        }
    }

    private static int packetsPerDay(int x, int N, int totalPackets) {
        double harmonicSum = harmonicSum(N); // Calculate the harmonic sum
        double a = totalPackets / harmonicSum; // Calculate a so that the sum of packets over N days equals totalPackets
        return (int) (a / x); // Calculate packets for day x
    }

    private static double harmonicSum(int N) {
        double sum = 0.0;
        for (int x = 1; x <= N; x++) {
            sum += 1.0 / x;
        }
        return sum;
    }
}
