package com.triton.search.base;

public interface Scorer<T extends GraphNode> {
    float computeCost(T from, T to);
}
