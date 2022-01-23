package com.triton.search;

import java.util.ArrayList;
import java.util.LinkedList;

public abstract class Search {
    protected LinkedList<Node> nodes;

    Search(LinkedList<Node> nodes) {
        this.nodes = nodes;
    }

    /**
     * Returns a list of nodes representing the path from the start node to the target node
     *
     * @param start  the node to start from
     * @param target the node to end at
     * @return a list of nodes representing the path from the start node to the target node, null if there is no path
     */
    public abstract LinkedList<Node> search(Node start, Node target);
}
