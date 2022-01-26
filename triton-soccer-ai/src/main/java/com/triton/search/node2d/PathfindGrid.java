package com.triton.search.node2d;

import com.triton.search.base.Graph;
import com.triton.search.base.RouteFinder;
import com.triton.search.base.Scorer;
import com.triton.util.Vector2d;
import org.apache.commons.collections4.iterators.ReverseListIterator;

import java.util.*;

import static com.triton.constant.RuntimeConstants.*;
import static proto.triton.ObjectWithMetadata.Robot;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class PathfindGrid {
    SSL_GeometryFieldSize field;
    HashMap<Integer, Robot> allies, foes;
    Robot excludeAlly;
    Map<Vector2d, Node2d> nodeMap;
    Map<Node2d, Set<Node2d>> connections;
    Set<Node2d> obstacles;

    public PathfindGrid(SSL_GeometryFieldSize field) {
        this.field = field;
        this.obstacles = new HashSet<>();
        generateNodeMap();
        generateConnections();
    }

    /**
     * Generate the node map
     */
    public void generateNodeMap() {
        Map<Vector2d, Node2d> nodeMap = new HashMap<>();

        float gridMaxX = field.getFieldWidth() / 2f
                + 2 * aiConfig.gridExtend;
        float gridMaxY = field.getFieldLength() / 2f
                + 2 * aiConfig.gridExtend;

        for (float x = 0; x < gridMaxX; x += aiConfig.getNodeSpacing()) {
            for (float y = 0; y < gridMaxY; y += aiConfig.getNodeSpacing()) {
                for (int xMul = -1; xMul < 2; xMul += 2) {
                    for (int yMul = -1; yMul < 2; yMul += 2) {
                        Vector2d pos = new Vector2d(xMul * x, yMul * y);
                        Node2d node = new Node2d(pos);
                        nodeMap.put(pos, node);
                    }
                }
            }
        }
        this.nodeMap = nodeMap;
    }

    /**
     * Generate connections between nodes
     */
    public void generateConnections() {
        Map<Node2d, Set<Node2d>> connections = new HashMap<>();
        nodeMap.forEach((pos, node) -> {
            Set<Node2d> neighbors = new HashSet<>();
            connections.put(node, neighbors);

            for (float offsetX = -aiConfig.getNodeSpacing(); offsetX <= aiConfig.getNodeSpacing(); offsetX += aiConfig.getNodeSpacing()) {
                for (float offsetY = -aiConfig.getNodeSpacing(); offsetY <= aiConfig.getNodeSpacing(); offsetY += aiConfig.getNodeSpacing()) {
                    if (offsetX + offsetY == 0 || offsetX == 0 && offsetY == 0)
                        continue;
                    Vector2d offset = new Vector2d(offsetX, offsetY);
                    Vector2d neighborPos = pos.add(offset);
                    if (nodeMap.containsKey(neighborPos))
                        neighbors.add(nodeMap.get(neighborPos));
                }
            }
        });
        this.connections = connections;
    }

    /**
     * Set obstacle value on nodes.
     * @param allies list of allies
     * @param foes list of foes
     * @param excludeAlly an ally that is not counted as an obstacle
     */
    public void updateObstacles(HashMap<Integer, Robot> allies, HashMap<Integer, Robot> foes, Robot excludeAlly) {
        this.allies = allies;
        this.foes = foes;
        this.excludeAlly = excludeAlly;

        obstacles.forEach(node -> node.setPenalty(0));
        obstacles.clear();

        float gridMaxX = field.getFieldWidth() / 2f
                + 2 * aiConfig.gridExtend;
        float gridMaxY = field.getFieldLength() / 2f
                + 2 * aiConfig.gridExtend;

        for (float x = 0; x < gridMaxX; x += aiConfig.getNodeSpacing()) {
            for (float y = 0; y < gridMaxY; y += aiConfig.getNodeSpacing()) {
                for (int xMul = -1; xMul < 2; xMul += 2) {
                    for (int yMul = -1; yMul < 2; yMul += 2) {
                        float posX = xMul * x;
                        float posY = yMul * y;

                        Vector2d pos = new Vector2d(posX, posY);
                        Node2d node = getNearestNode(pos);
                        float dist = getDistanceFromBound(node.getPos());

                        if (node.getPenalty() == 0 && dist > 0) {
                            node.setPenalty(aiConfig.obstacleScale * dist);
                            obstacles.add(node);
                        }
                    }
                }
            }
        }

        allies.forEach((id, ally) -> {
            if (ally == excludeAlly) return;
            Vector2d pos = new Vector2d(ally.getX(), ally.getY());
            Set<Node2d> nearestNodes = getNearestNodes(pos, aiConfig.getRobotCollisionDist());
            nearestNodes.forEach(node -> {
                float dist = node.getPos().dist(pos);
                node.updatePenalty(aiConfig.obstacleScale * (aiConfig.getRobotCollisionDist() / dist));
                obstacles.add(node);
            });
        });

        foes.forEach((id, ally) -> {
            Vector2d pos = new Vector2d(ally.getX(), ally.getY());
            Set<Node2d> nearestNodes = getNearestNodes(pos, aiConfig.getRobotCollisionDist());
            nearestNodes.forEach(node -> {
                float dist = node.getPos().dist(pos);
                node.updatePenalty(aiConfig.obstacleScale * (aiConfig.getRobotCollisionDist() / dist));
                obstacles.add(node);
            });
        });
    }

    /**
     * Returns the minimum distance from a point to the nearest bound, 0 if the point is not out of bounds
     * @param pos the point
     * @return the minimum distance from a point to the nearest bound, 0 if the point is not out of bounds
     */
    private float getDistanceFromBound(Vector2d pos) {
        float boundMinX = -field.getFieldWidth() / 2f + aiConfig.getBoundCollisionDist();
        float boundMaxX = field.getFieldWidth() / 2f - aiConfig.getBoundCollisionDist();
        float boundMinY = -field.getFieldLength() / 2f + aiConfig.getBoundCollisionDist();
        float boundMaxY = field.getFieldLength() / 2f - aiConfig.getBoundCollisionDist();

        float maxDist = 0;

        if (pos.x < boundMinX)
            maxDist = Math.max(maxDist, boundMinX - pos.x);
        if (pos.x > boundMaxX)
            maxDist = Math.max(maxDist, pos.x - boundMaxX);
        if (pos.y < boundMinY)
            maxDist = Math.max(maxDist, boundMinY - pos.y);
        if (pos.y > boundMaxY)
            maxDist = Math.max(maxDist, pos.y - boundMaxY);

        return maxDist;
    }

    /**
     * Returns the obstacle value of the node nearest to a point
     * @param pos the point
     * @return the obstacle value of the node nearest to a point
     */
    public double getObstacle(Vector2d pos) {
        Node2d node = getNearestNode(pos);
        if (node == null) return 0;
        return node.getPenalty();
    }

    /**
     * Returns the largest obstacle value between two points
     * @param from the first point
     * @param to the second point
     * @return the largest obstacle value between two points
     */
    public double getMaxObstacle(Vector2d from, Vector2d to) {
        Vector2d step = to.sub(from).norm().scale(aiConfig.nodeRadius);
        Vector2d currentPos = from;
        double maxObstacle = 0;
        while (currentPos.dist(to) > aiConfig.nodeRadius) {
            Node2d currentNode = getNearestNode(currentPos);
            if (currentNode != null && currentNode.getPenalty() > maxObstacle)
                maxObstacle = currentNode.getPenalty();
            currentPos = currentPos.add(step);
        }
        return maxObstacle;
    }

    /**
     * Returns whether any obstacle value between two points is larger than a threshold
     * @param from the first point
     * @param to the second point
     * @param threshold the threshold
     * @return whether any obstacle value between two points is larger than a threshold
     */
    public boolean checkObstacle(Vector2d from, Vector2d to, double threshold) {
        Vector2d step = to.sub(from).norm().scale(aiConfig.nodeRadius);
        Vector2d currentPos = from;
        while (currentPos.dist(to) > aiConfig.nodeRadius) {
            Node2d currentNode = getNearestNode(currentPos);
            if (currentNode != null && currentNode.getPenalty() > threshold)
                return true;
            currentPos = currentPos.add(step);
        }
        return false;
    }

    /**
     * Returns the nearest nodes to a point
     * @param pos the point
     * @param dist distance to search
     * @return the nearest nodes to a point
     */
    public Set<Node2d> getNearestNodes(Vector2d pos, float dist) {
        Set<Node2d> nearestNodes = new HashSet<>();

        float minX = pos.x - dist;
        float maxX = pos.x + dist;
        float minY = pos.y - dist;
        float maxY = pos.y + dist;

        for (float x = minX; x < maxX; x += aiConfig.getNodeSpacing()) {
            for (float y = minY; y < maxY; y += aiConfig.getNodeSpacing()) {
                Vector2d offsetPos = new Vector2d(x, y);
                if (offsetPos.dist(pos) < dist) {
                    Node2d neighbor = getNearestNode(offsetPos);
                    if (neighbor != null)
                        nearestNodes.add(neighbor);
                }
            }
        }

        return nearestNodes;
    }

    /**
     * Returns the nearest node to a point
     * @param pos the point
     * @return the nearest node to a point
     */
    public Node2d getNearestNode(Vector2d pos) {
        float nearestX = Math.round(pos.x / aiConfig.getNodeSpacing()) * aiConfig.getNodeSpacing();
        float nearestY = Math.round(pos.y / aiConfig.getNodeSpacing()) * aiConfig.getNodeSpacing();
        Vector2d nearestPos = new Vector2d(nearestX, nearestY);
        return nodeMap.get(nearestPos);
    }

    /**
     * Find a route between two points
     * @param from point to start from
     * @param to point to end at
     * @return a route between two points
     */
    public List<Node2d> findRoute(Vector2d from, Vector2d to) {
        Graph<Node2d> graph = new Graph<>(new HashSet<>(nodeMap.values()), connections);
        Scorer<Node2d> nextNodeScorer = new Euclidean2dScorer();
        Scorer<Node2d> targetScorer = new Euclidean2dScorer();
        RouteFinder<Node2d> routeFinder = new RouteFinder<>(graph, nextNodeScorer, targetScorer);

        Node2d fromNode = getNearestNode(from);
        Node2d toNode = getNearestNode(to);

        if (fromNode == null) {
            System.out.println("From node not on field.");
            return null;
        } else if (toNode == null) {
            System.out.println("To node not on field.");
            return null;
        }

        List<Node2d> route;
        try {
            route = routeFinder.findRoute(fromNode, toNode);
        } catch (IllegalStateException e) {
            route = new ArrayList<>();
            route.add(fromNode);
            return route;
        }

        route.add(new Node2d(to));
        return route;
    }

    /**
     * Find the next point in a route between two points
     * @param from point to start from
     * @param to point to end at
     * @return a route between two points
     */
    public Vector2d findNext(Vector2d from, Vector2d to) {
        return findNext(findRoute(from, to));
    }

    /**
     * Find the next point given a route. The next point is the point furthest in the route that can be reached without
     * crossing through points with higher obstacle values than the start point
     * @param route the route
     * @return the next point given a route
     */
    public Vector2d findNext(List<Node2d> route) {
        Node2d fromNode = route.get(0);
        double threshold = fromNode.getPenalty();

        ReverseListIterator<Node2d> reverseListIterator = new ReverseListIterator<>(route);
        while (reverseListIterator.hasNext()) {
            Node2d toNode = reverseListIterator.next();
            if (!checkObstacle(fromNode.getPos(), toNode.getPos(), threshold))
                return toNode.getPos();
        }

        throw new IllegalStateException();
    }

    public Map<Vector2d, Node2d> getNodeMap() {
        return nodeMap;
    }

    public Map<Node2d, Set<Node2d>> getConnections() {
        return connections;
    }

    public Set<Node2d> getObstacles() {
        return obstacles;
    }
}
