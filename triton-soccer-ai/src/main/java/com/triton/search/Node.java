package com.triton.search;

import java.util.ArrayList;

public class Node {
    ArrayList<Node> neighbors;
    int x;
    int y;

    public Node(neighbors, x, y){
        this.neighbors(neighbors);
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Node n) {
        return (this.x == n.x && this.y == n.y);
    }

    Node(Node p){
        this.neighbors = new ArrayList<Node>;
        for (Node n : p.neighbors){
            temp = new Node(n);
            this.neighbors.add(temp);
        }

        this.x = p.x;
        this.y = p.y;
    }
}
