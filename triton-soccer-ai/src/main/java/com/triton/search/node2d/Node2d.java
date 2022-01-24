package com.triton.search.node2d;

import com.triton.helper.Vector2d;
import com.triton.search.base.GraphNode;

public class Node2d implements GraphNode {
    private final Vector2d pos;
    private boolean obstacle;

    public Node2d(Vector2d pos) {
        this.pos = pos;
        this.obstacle = false;
    }

    public Vector2d getPos() {
        return pos;
    }

    public boolean isObstacle() {
        return obstacle;
    }

    public void setObstacle(boolean obstacle) {
        this.obstacle = obstacle;
    }
}
