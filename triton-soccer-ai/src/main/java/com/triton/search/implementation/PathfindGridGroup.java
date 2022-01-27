package com.triton.search.implementation;

import com.triton.search.node2d.Node2d;
import com.triton.util.Vector2d;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static proto.triton.ObjectWithMetadata.Robot;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class PathfindGridGroup {
    HashMap<Integer, PathfindGrid> pathfindGrids;

    public PathfindGridGroup(int numGrids, SSL_GeometryFieldSize field) {
        pathfindGrids = new HashMap<>();
        for (int i = 0; i < numGrids; i++)
            pathfindGrids.put(i, new PathfindGrid(field));
    }

    public void updateObstacles(Map<Integer, Robot> allies, Map<Integer, Robot> foes) {
        pathfindGrids.forEach((id, pathfindGrid) -> {
            Robot excludeAlly = allies.get(id);
            pathfindGrid.updateObstacles(allies, foes, excludeAlly);
        });
    }

    public Vector2d findNext(int id, Vector2d from, Vector2d to) {
        return findNext(id, findRoute(id, from, to));
    }

    public Vector2d findNext(int id, LinkedList<Node2d> route) {
        PathfindGrid pathfindGrid = pathfindGrids.get(id);
        return pathfindGrid.findNext(route);
    }

    public LinkedList<Node2d> findRoute(int id, Vector2d fromPos, Vector2d toPos) {
        PathfindGrid pathfindGrid = pathfindGrids.get(id);
        return pathfindGrid.findRoute(fromPos, toPos);
    }
}
