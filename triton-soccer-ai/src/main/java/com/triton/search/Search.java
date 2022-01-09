package com.triton.search;

import java.util.ArrayList;

public abstract class Search {
    protected ArrayList<Node> nodes;

    Search(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    /**
     * Returns a list of nodes representing the path from the start node to the target node
     * @param start the node to start from
     * @param target the node to end at
     * @return a list of nodes representing the path from the start node to the target node, null if there is no path
     */
    public abstract ArrayList<Node> search(Node start, Node target);
}
