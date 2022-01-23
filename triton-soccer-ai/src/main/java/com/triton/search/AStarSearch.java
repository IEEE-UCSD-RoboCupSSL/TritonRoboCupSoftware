package com.triton.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class AStarSearch extends Search {
    AStarSearch(LinkedList<Node> nodes) {
        super(nodes);
    }

    @Override
    public LinkedList<Node> search(Node start, Node target) {
        ArrayList<Path> closedSet = new ArrayList<>();
        ArrayList<Path> openSet = new ArrayList<>();

        LinkedList<Node> defaultPath = new LinkedList<>();
        defaultPath.push(start);
        Path currentPath = new Path(defaultPath, 0, find_distance(start, target));

        if (start == target)
            return currentPath.nodePath;

        openSet.add(currentPath);

        while (!openSet.isEmpty()) {
            currentPath = openSet.get(0);
            for (int i = 0; i < openSet.size(); i++){
                if (openSet.get(i).score < currentPath.score)
                    currentPath = openSet.get(i);
            }

            for (Path closedPath : closedSet) {
                if (currentPath.nodePath.peek() == closedPath.nodePath.peek() && currentPath.score > closedPath.score)
                    break;

                for (Node neighbor : currentPath.nodePath.peek().neighbors) {
                    Path tempPath = new Path(currentPath); // deep copy of currentPath
                    tempPath.setDistance(tempPath.distance + find_distance(tempPath.nodePath.peek(), neighbor));
                    tempPath.setScore(tempPath.distance + find_distance(neighbor, target));
                    tempPath.nodePath.add(neighbor);

                    // checks if we have already encountered a better way to reach the neighbor
                    for (Path closedPath2 : closedSet) {
                        if (tempPath.nodePath.peek() == closedPath2.nodePath.peek() && tempPath.score > closedPath2.score)
                            break;
                    }

                    if (tempPath.nodePath.peek() == target)
                        return tempPath.nodePath;

                    openSet.add(tempPath);
                }
            }

            closedSet.add(currentPath);
            openSet.remove(currentPath);
        }

        return null;
    }

    public double find_distance(Node node1, Node node2){
        // calculates distance between two nodes
        return Math.sqrt(Math.pow(node2.x - node1.x, 2) + Math.pow(node2.y - node1.y, 2));
    }
}
