package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ServerAPI {
    private static ServerAPI instance = null;
    private static final String URI = "http://localhost:8080/api/v1";
    private static final String APIKEY = "7bcd6334-bc2e-4cbf-b9d4-61cb9e868869";
    private static String sessionID = null;
    private static Integer day = 0;
    private static final HttpClient client = HttpClient.newHttpClient();

    private ServerAPI() {
        startSession();
    }

    public static ServerAPI getInstance() {
        if (instance == null) instance = new ServerAPI();
        return instance;
    }

    private void startSession() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(new URI(URI + "/session/start"))
                    .header("API-KEY", APIKEY)
                    .header("User-Agent", "PostmanRuntime/7.37.3")
                    .header("Accept", "*/*")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .POST(HttpRequest.BodyPublishers.noBody()) // Sending empty body
                    .build();

            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            String responseBody = res.body();
            System.out.println("Start Session: " + responseBody);
            if (responseBody.startsWith("{") || responseBody.startsWith("[")) {
                // It's a JSON object or array
                System.out.println("ERROR: " + responseBody);
            } else {
                // Otherwise, it's a plain string (ID)
                System.out.println("Session ID: " + responseBody);
                sessionID = responseBody;
                day = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JsonNode playRound(final List<Movement> movements) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jsonBody = objectMapper.createObjectNode();
            jsonBody.put("day", day++);
            jsonBody.set("movements", movementsToNode(movements));

            String requestBody = objectMapper.writeValueAsString(jsonBody);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(new URI(URI + "/play/round"))
                    .header("API-KEY", APIKEY)
                    .header("User-Agent", "PostmanRuntime/7.37.3")
                    .header("Accept", "*/*")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Content-Type", "application/json")
                    .header("SESSION-ID", sessionID)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody)) // Sending empty body
                    .build();

            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            String responseBody = res.body();
            System.out.println("Play Round: " + responseBody);

            return objectMapper.readTree(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void endSession() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(new URI(URI + "/session/end"))
                    .header("API-KEY", APIKEY)
                    .header("User-Agent", "PostmanRuntime/7.37.3")
                    .header("Accept", "*/*")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .POST(HttpRequest.BodyPublishers.noBody()) // Sending empty body
                    .build();

            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            String responseBody = res.body();
            System.out.println("End Session: " + responseBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayNode movementsToNode(@org.jetbrains.annotations.NotNull final List<Movement> movements) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();

        for (Movement movement : movements) {
            ObjectNode movementNode = objectMapper.createObjectNode();
            movementNode.put("connectionId", movement.getEdgeId());
            movementNode.put("amount", movement.getAmount());
            arrayNode.add(movementNode);
        }

        return arrayNode;
    }

    public int getDay() {
        return day;
    }
}
