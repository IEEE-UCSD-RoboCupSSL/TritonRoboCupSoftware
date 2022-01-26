package com.triton.module.test_module.misc_test;

import com.triton.module.TestRunner;
import com.triton.search.base.Graph;
import com.triton.search.base.RouteFinder;
import com.triton.search.base.Scorer;
import com.triton.search.node2d.Euclidean2dScorer;
import com.triton.search.node2d.Node2d;
import com.triton.util.Vector2d;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

public class AStarSearchTest extends TestRunner {

    public AStarSearchTest(ScheduledThreadPoolExecutor executor) {
        super(executor);
    }

    @Override
    protected void prepare() {
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
    }

    @Override
    protected void declareConsumes() throws IOException, TimeoutException {
    }

    @Override
    protected void setupTest() {
    }

    @Override
    public void run() {
        super.run();

        while (!isInterrupted())
            execute();
    }

    @Override
    protected void execute() {
        Map<Vector2d, Node2d> nodeMap = new HashMap<>();
        Map<Node2d, Set<Node2d>> connections = new HashMap<>();

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                nodeMap.put(new Vector2d(x, y), new Node2d(new Vector2d(x, y)));
            }
        }

        nodeMap.forEach((vector, node) -> {
            Set<Node2d> neighbors = new HashSet<>();
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    if (x == 1 && y == 1)
                        continue;
                    if (Math.abs(x - node.getPos().x) <= 1 && Math.abs(y - node.getPos().y) <= 1) {
                        Node2d neighbor = nodeMap.get(new Vector2d(x, y));
                        if (neighbor != null)
                            neighbors.add(neighbor);
                    }
                }
            }
            connections.put(node, neighbors);
        });

        Graph<Node2d> graph = new Graph<>(new HashSet<>(nodeMap.values()), connections);
        Scorer<Node2d> nextNodeScorer = new Euclidean2dScorer();
        Scorer<Node2d> targetScorer = new Euclidean2dScorer();
        RouteFinder<Node2d> routeFinder = new RouteFinder<>(graph, nextNodeScorer, targetScorer);

        Node2d from = nodeMap.get(new Vector2d(0, 0));
        Node2d to = nodeMap.get(new Vector2d(2, 2));
        List<Node2d> route = routeFinder.findRoute(from, to);
        route.forEach(node -> {
            System.out.println(node.getPos());
        });
        System.out.println();
    }
}
