package com.triton.search.node1d;

import com.triton.helper.Vector2d;
import com.triton.search.base.GraphNode;

public class Node1d implements GraphNode {
    private final String id;
    private final double value;

    public Node1d(String id, double value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public String getId() {
        return id;
    }

    public double getValue() {
        return value;
    }
}