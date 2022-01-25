package com.triton.search.node2d;

import com.triton.util.Vector2d;
import com.triton.search.base.GraphNode;

public class Node2d implements GraphNode {
    private final Vector2d pos;
    private double obstacle;

    public Node2d(Vector2d pos) {
        this.pos = pos;
        this.obstacle = 0;
    }

    public Vector2d getPos() {
        return pos;
    }

    public double getObstacle() {
        return obstacle;
    }

    public void setObstacle(double obstacle) {
        this.obstacle = obstacle;
    }
}
