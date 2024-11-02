package org.example;

import com.fasterxml.jackson.databind.JsonNode;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        ServerAPI.getInstance();
        JsonNode node = ServerAPI.getInstance().playRound();
        ServerAPI.getInstance().endSession();
    }
}