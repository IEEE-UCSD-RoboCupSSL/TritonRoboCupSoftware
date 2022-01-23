package com.triton.old.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class AStarSearch extends Search {

    public AStarSearch(Collection<Node> nodes) {
        super(nodes);
    }

    @Override
    public LinkedList<Node> search(Node start, Node target) {
        ArrayList<Path> closedSet = new ArrayList<>();
        ArrayList<Path> openSet = new ArrayList<>();

        LinkedList<Node> defaultPath = new LinkedList<>();
        defaultPath.push(start);
        Path currentPath = new Path(defaultPath, 0, findDistance(start, target));

        if (start == target)
            return currentPath.nodePath;

        openSet.add(currentPath);

        while (!openSet.isEmpty()) {
            currentPath = null;
            for (Path openPath : openSet) {
                if (currentPath == null || openPath.score < currentPath.score)
                    currentPath = openPath;
            }

            boolean isCurrentPathBetter = true;
            for (Path closedPath : closedSet) {
                if (currentPath.nodePath.peek() == closedPath.nodePath.peek() && currentPath.score > closedPath.score) {
                    isCurrentPathBetter = false;
                    break;
                }
            }

            if (isCurrentPathBetter) {
                for (Node neighbor : currentPath.nodePath.peek().neighbors) {
                    Path tempPath = new Path(currentPath); // deep copy of currentPath
                    tempPath.setDistance(tempPath.distance + findDistance(tempPath.nodePath.peek(), neighbor));
                    tempPath.setScore(tempPath.distance + findDistance(neighbor, target));
                    tempPath.nodePath.add(neighbor);

                    // checks if we have already encountered a better way to reach the neighbor
                    boolean isCurrentPathWithNeighborBetter = false;
                    for (Path closedPath2 : closedSet) {
                        if (tempPath.nodePath.peek() == closedPath2.nodePath.peek() && tempPath.score > closedPath2.score) {
                            isCurrentPathWithNeighborBetter = true;
                            break;
                        }
                    }

                    if (!isCurrentPathWithNeighborBetter) {
                        if (tempPath.nodePath.peek() == target)
                            return tempPath.nodePath;
                        openSet.add(tempPath);
                    }
                }
            }

            closedSet.add(currentPath);
            openSet.remove(currentPath);

            System.out.println("OPEN SET:");
            for (Path openPath : openSet) {
                System.out.println(openPath);
            }

            System.out.println("CLOSED SET:");
            for (Path openPath : openSet) {
                System.out.println(openPath);
            }

            System.out.println();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public double findDistance(Node node1, Node node2){
        // calculates distance between two nodes
        return Math.sqrt(Math.pow(node2.x - node1.x, 2) + Math.pow(node2.y - node1.y, 2));
    }
}
