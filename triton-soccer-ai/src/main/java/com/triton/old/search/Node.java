package com.triton.old.search;

import java.util.ArrayList;

public class Node {
    public ArrayList<Node> neighbors;
    public int x;
    public int y;

    public Node(int x, int y) {
        this.neighbors = new ArrayList<>();
        this.x = x;
        this.y = y;
    }

    public void setNeighbors(ArrayList<Node> neighbors) {
        this.neighbors = new ArrayList<>();
        this.neighbors.addAll(neighbors);
    }

    public void addNeighbor(Node neighbor) {
        this.neighbors.add(neighbor);
    }

    @Override
    public String toString() {
        String str = "";
        for (Node neighbor : neighbors) {
            str += neighbor.x + ", " + neighbor.y + "\n";
        }
        return str;
    }
}
