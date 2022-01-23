package com.triton.old.search;

import java.util.Collection;
import java.util.LinkedList;

public abstract class Search {
    protected LinkedList<Node> nodes;

    Search(Collection<Node> nodes) {
        this.nodes = new LinkedList<>(nodes);
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
