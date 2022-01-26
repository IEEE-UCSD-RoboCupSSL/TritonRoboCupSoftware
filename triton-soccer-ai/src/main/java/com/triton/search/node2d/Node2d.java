package com.triton.search.node2d;

import com.triton.search.base.GraphNode;
import com.triton.util.Vector2d;

public class Node2d implements GraphNode {
    private final Vector2d pos;
    private double penalty;

    public Node2d(Vector2d pos) {
        this.pos = pos;
        this.penalty = 0;
    }

    public Vector2d getPos() {
        return pos;
    }

    public double getPenalty() {
        return penalty;
    }

    public void setPenalty(double penalty) {
        this.penalty = penalty;
    }

    public void updatePenalty(double obstacle) {
        if (obstacle > this.penalty)
            this.penalty = obstacle;
    }
}
