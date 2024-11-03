package org.example;

abstract class Node {
    String uuid;
    String name;
    NodeType type;

    public Node(String uuid, String name, NodeType type) {
        this.uuid = uuid;
        this.name = name;
        this.type = type;
    }

    public Node(Node other) {
        this.uuid = other.uuid;
        this.name = other.name;
        this.type = other.type;
    }
}
