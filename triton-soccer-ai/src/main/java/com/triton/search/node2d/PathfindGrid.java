package com.triton.search.node2d;

import com.triton.search.base.Graph;
import com.triton.search.base.RouteFinder;
import com.triton.search.base.Scorer;
import com.triton.util.Vector2d;
import org.apache.commons.collections4.iterators.ReverseListIterator;

import java.util.*;

import static com.triton.constant.RuntimeConstants.aiConfig;
import static proto.triton.ObjectWithMetadata.Robot;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class PathfindGrid {
    SSL_GeometryFieldSize field;
    Map<Integer, Robot> allies, foes;
    Map<Vector2d, Node2d> nodeMap;
    Map<Node2d, Set<Node2d>> connections;
    List<Node2d> obstacles;

    public PathfindGrid(SSL_GeometryFieldSize field) {
        this.field = field;
        this.obstacles = new ArrayList<>();
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
     *
     * @param allies      list of allies
     * @param foes        list of foes
     * @param excludeAlly an ally that is not counted as an obstacle
     */
    public synchronized void updateObstacles(Map<Integer, Robot> allies, Map<Integer, Robot> foes, Robot excludeAlly) {
        this.allies = allies;
        this.foes = foes;

        obstacles.forEach(node -> node.setPenalty(0));
        obstacles.clear();

        nodeMap.forEach((pos, node) -> {
            float dist = getDistanceFromBound(node.getPos());
            if (node.getPenalty() == 0 && dist > 0) {
                node.setPenalty(aiConfig.obstacleScale * dist);
//                obstacles.add(node);
            }
        });

        allies.forEach((id, ally) -> {
            if (ally == excludeAlly) return;
            Vector2d allyPos = new Vector2d(ally.getX(), ally.getY());
            Vector2d allyVel = new Vector2d(ally.getVx(), ally.getVy());
            Vector2d pos = allyPos.add(allyVel.scale(aiConfig.collisionExtrapolation));
            float collisionDist = aiConfig.getRobotCollisionDist()
                    + aiConfig.collisionSpeedScale * allyVel.mag();
            List<Node2d> nearestNodes = getNearestNodes(pos, collisionDist);
            nearestNodes.forEach(node -> {
                float dist = node.getPos().dist(pos);
                node.updatePenalty(aiConfig.obstacleScale * (aiConfig.getRobotCollisionDist() / dist));
                obstacles.add(node);
            });
        });

        foes.forEach((id, foe) -> {
            Vector2d foePos = new Vector2d(foe.getX(), foe.getY());
            Vector2d foeVel = new Vector2d(foe.getVx(), foe.getVy());
            Vector2d pos = foePos.add(foeVel.scale(aiConfig.collisionExtrapolation));
            float collisionDist = aiConfig.getRobotCollisionDist()
                    + aiConfig.collisionSpeedScale * foeVel.mag();
            List<Node2d> nearestNodes = getNearestNodes(pos, collisionDist);
            nearestNodes.forEach(node -> {
                float dist = node.getPos().dist(pos);
                node.updatePenalty(aiConfig.obstacleScale * (aiConfig.getRobotCollisionDist() / dist));
                obstacles.add(node);
            });
        });
    }

    /**
     * Returns the minimum distance from a point to the nearest bound, 0 if the point is not out of bounds
     *
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
     * Returns the nearest nodes to a point
     *
     * @param pos  the point
     * @param dist distance to search
     * @return the nearest nodes to a point
     */
    public List<Node2d> getNearestNodes(Vector2d pos, float dist) {
        List<Node2d> nearestNodes = new ArrayList<>();

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
     *
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
     * Returns the obstacle value of the node nearest to a point
     *
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
     *
     * @param from the first point
     * @param to   the second point
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
     * Find the next point in a route between two points
     *
     * @param from point to start from
     * @param to   point to end at
     * @return a route between two points
     */
    public Vector2d findNext(Vector2d from, Vector2d to) {
        return findNext(findRoute(from, to));
    }

    /**
     * Find the next point given a route. The next point is the point furthest in the route that can be reached without
     * crossing through points with higher obstacle values than the start point
     *
     * @param route the route
     * @return the next point given a route
     */
    public Vector2d findNext(LinkedList<Node2d> route) {
        Node2d from = route.getFirst();
        double threshold = from.getPenalty();

        ReverseListIterator<Node2d> reverseListIterator = new ReverseListIterator<>(route);
        while (reverseListIterator.hasNext()) {
            Node2d to = reverseListIterator.next();
            if (!checkObstacle(from.getPos(), to.getPos(), threshold))
                return to.getPos();
        }

        throw new IllegalStateException();
    }

    /**
     * Find a route between two points
     *
     * @param fromPos point to start from
     * @param toPos   point to end at
     * @return a route between two points
     */
    public LinkedList<Node2d> findRoute(Vector2d fromPos, Vector2d toPos) {
        Graph<Node2d> graph = new Graph<>(new HashSet<>(nodeMap.values()), connections);
        Scorer<Node2d> nextNodeScorer = new Euclidean2dScorer();
        Scorer<Node2d> targetScorer = new Euclidean2dScorer();
        RouteFinder<Node2d> routeFinder = new RouteFinder<>(graph, nextNodeScorer, targetScorer);

        Node2d from = getNearestNode(fromPos);
        Node2d to = getNearestNode(toPos);

        if (from == null) {
            System.out.println("From node not on field.");
            return null;
        } else if (to == null) {
            System.out.println("To node not on field.");
            return null;
        }

        LinkedList<Node2d> route;
        try {
            route = routeFinder.findRoute(from, to);
        } catch (IllegalStateException e) {
            route = new LinkedList<>();
            route.addLast(from);
        }

        route.addLast(new Node2d(toPos));
        return route;
    }

    /**
     * Returns whether any obstacle value between two points is larger than a threshold
     *
     * @param from      the first point
     * @param to        the second point
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

    public Map<Vector2d, Node2d> getNodeMap() {
        return nodeMap;
    }

    public Map<Node2d, Set<Node2d>> getConnections() {
        return connections;
    }

    public List<Node2d> getObstacles() {
        return obstacles;
    }
}
