package com.triton.search;

import java.util.ArrayList;
import java.util.LinkedList;

public class Path {
    public LinkedList<Node> nodePath;
    public double distance;
    public double score; // score = actual distance to Node from start + heuristic

    public Path(LinkedList<Node> nodePath, double distance, double score){
        this.nodePath = nodePath;
        this.distance = distance;
        this.score = score;
    }

    // copy constructor
    public Path(Path path) {
        this.nodePath = new LinkedList<>();
        this.nodePath.addAll(path.nodePath);
        this.distance = path.distance;
        this.score = path.score;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setScore(double score) {
        this.score = score;
    }
}