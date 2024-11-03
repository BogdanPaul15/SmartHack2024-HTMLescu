package org.example;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.opencsv.*;
import com.opencsv.exceptions.CsvException;

public class Main {
    private static Graph graph;

    public static void main(String[] args) throws Exception {
        graph = new Graph();
        Refinery refinery = new Refinery("2", "Refinery 0", 500, 300, 250, 50, 30, 20, 100, 10, 200);
        Tank tank = new Tank("3", "Tank 0", 1000, 150, 100, 40, 20, 10, 15, 500);
        Customer customer = new Customer("4", "Customer 0", 120, 25, 60, 15);

        graph.addNode(refinery);
        graph.addNode(tank);
        graph.addNode(customer);

        Edge refineryToTank = new Edge("2", "2", "3", 0, 0, ConnectionType.TRUCK, 0);
        Edge tankToClient = new Edge("3", "3", "4", 0, 0, ConnectionType.PIPELINE, 0);

        graph.addEdge(refineryToTank);
        graph.addEdge(tankToClient);

        graph.display();

        populate();
        ServerAPI serverAPI = ServerAPI.getInstance();
        playRound();
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
        graph.solveMovements(ServerAPI.getInstance().getDay());
    }

    private static void pumpRefineries() {
        Refinery sef = graph.refinerySource;
        List<Refinery> refineries = graph.getAllRefineries();
        for (final Refinery refinery : refineries) {
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
                graph.addEdge(edge);
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
}
