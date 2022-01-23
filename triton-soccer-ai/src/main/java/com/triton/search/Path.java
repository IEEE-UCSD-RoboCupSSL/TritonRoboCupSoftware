package com.triton.search;

import java.util.ArrayList;

public class Path {
    ArrayList<Node> path;
    Node latestNode;
    double distanceTraveled;
    double score; // score = actual distance to Node from start + heuristic

    public Path(ArrayList<Node> p, Node n, double distance, double s){
        this.path = p;
        this.latestNode = n;
        this.distanceTraveled = distanceTraveled;
        this.score = s;
    }

    // copy constructor
    Path(Path p){
        this.path = new ArrayList<Path>;
        for (Node n : p.path){
            temp = new Node(n);
            this.path.add(temp);
        }
        this.latestNode = p.latestNode;
        this.distanceTraveled = p.distanceTraveled
        this.score = p.score;
    }
}

public void setPath(ArrayList<Node> p){
    this.path = p;
}

public void setNode(Node n){
    this.latestNode = n;
}
 
public void setDistance(double distance){
    distanceTraveled = distance;
}

public void setScore(double s){
    score = s;
}