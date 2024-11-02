package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ServerAPI {
    private static ServerAPI instance = null;
    private static final String URI = "http://localhost:8080/api/v1";
    private static final String APIKEY = "7bcd6334-bc2e-4cbf-b9d4-61cb9e868869";
    public static String sessionID = null;
    private static final HttpClient client = HttpClient.newHttpClient();

    private ServerAPI() {
        startSession();
    }

    public static synchronized ServerAPI getInstance() {
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JsonNode playRound() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jsonBody = objectMapper.createObjectNode();
            jsonBody.put("day", 0);
            jsonBody.set("movements", objectMapper.createArrayNode());

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
}
