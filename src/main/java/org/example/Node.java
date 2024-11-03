package org.example;

import java.util.PriorityQueue;

abstract class Node {
    String uuid;
    String name;
    NodeType type;

    public Node(String uuid, String name, NodeType type) {
        this.uuid = uuid;
        this.name = name;
        this.type = type;
    }
}
