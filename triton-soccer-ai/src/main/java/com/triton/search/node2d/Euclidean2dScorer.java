package com.triton.search.node2d;

import com.triton.search.base.Scorer;

public class Euclidean2dScorer implements Scorer<Node2d> {
    @Override
    public double computeCost(Node2d from, Node2d to) {
        return from.getPos().dist(to.getPos());
    }
}
