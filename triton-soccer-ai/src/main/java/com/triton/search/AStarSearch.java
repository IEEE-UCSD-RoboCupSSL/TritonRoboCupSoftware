package com.triton.search;

import java.util.ArrayList;

public class AStarSearch extends Search {
    AStarSearch(ArrayList<Node> nodes) {
        super(nodes);
    }

    @Override
    public ArrayList<Node> search(Node start, Node target) {
        ArrayList<Path> closedSet = new ArrayList<Path>;
        ArrayList<Path> openSet = new ArrayList<Path>;
        Path currentPath = new Path([start], start, 0, find_distance(start, target));
        int currentIndex = 0;
        // currentPath.setPath([start]);
        // currentPath.setNode(start);
        // currentPath.setDistance(0);
        // currentPath.setScore(find_distance(currentPath.latestNode, target))
        openSet.add(currentPath);
        if (start == target){return currentPath.path}
        
        while (true) {
            currentPath = openSet[0];
            for (int i = 0; i < openSet.length; i++){
                if (openSet[i].path.score < currentPath.score) {
                    currentPath = openSet[i].path;
                    currenIndex = i;
                }
            }
            for (Path p : closedSet){
                if !(currentPath.latestNode == p.latestNode && currentPath.score > p.score)
                    break;
                for (Node n : currentPath.latestNode.neighbors){
                    Path tempPath = new Path(currentPath); // deep copy of currentPath
                    tempPath.setDistance(distanceTraveled + find_distance(currentPath.latestNode, n));
                    tempPath.setScore(distanceTraveled + find_distance(n, target)); 
                    tempPath.setNode(n);
                    node = tempPath.path.add(n);
                    tempPath.setPath(node);
                    for (Path p : closedSet){
                        if (tempPath.latestNode == p.latestNode && tempPath.score > p.score)
                            break;
                    }
                    if (tempPath.latestNode == target)
                        return tempPath.path;
                    openSet.add(tempPath);
                }
            }
            closedSet.add(currentPath);
            openSet.remove[i];
        }
        return null;
    }

    public double find_distance(Node node1, Node node2){
        // calculates distance between two nodes
        return Math.sqrt(Math.pow(node2.x - node1.x, 2) + Math.pow(node2.y - node1.y, 2))
    }
}
