package com.triton.search.node2d;

import com.triton.helper.Vector2d;
import com.triton.search.base.GraphNode;

public class Node2d implements GraphNode {
    private final String id;
    private final Vector2d pos;

    public Node2d(String id, Vector2d pos) {
        this.id = id;
        this.pos = pos;
    }

    @Override
    public String getId() {
        return id;
    }

    public Vector2d getPos() {
        return pos;
    }
}
