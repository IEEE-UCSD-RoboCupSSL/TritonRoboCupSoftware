package com.triton.search;

import java.util.ArrayList;

public class Node {
    ArrayList<Node> neighbors;
    int x;
    int y;

    public Node(ArrayList<Node> neighbors, int x, int y) {
        this.neighbors = new ArrayList<>();
        this.neighbors.addAll(neighbors);
        this.x = x;
        this.y = y;
    }
}
