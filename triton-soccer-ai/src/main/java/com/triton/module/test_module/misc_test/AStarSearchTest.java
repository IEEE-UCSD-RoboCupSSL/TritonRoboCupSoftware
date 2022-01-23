package com.triton.module.test_module.misc_test;

import com.triton.helper.Vector2d;
import com.triton.module.Module;
import com.triton.old.search.AStarSearch;
import com.triton.old.search.Node;
import com.triton.old.search.Search;
import com.triton.search.base.Graph;
import com.triton.search.base.RouteFinder;
import com.triton.search.base.Scorer;
import com.triton.search.node2d.Euclidean2dScorer;
import com.triton.search.node2d.Node2d;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class AStarSearchTest extends Module {

    public AStarSearchTest() throws IOException, TimeoutException {
        super();
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            Set<Node2d> nodes = new HashSet<>();
            Map<String, Set<String>> connections = new HashMap<>();

            Node2d from = null;
            Node2d to = null;

            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    Node2d node = new Node2d(x + " " + y, new Vector2d(x, y));
                    if (x == 0 && y == 0)
                        from = node;
                    if (x == 2 && y == 2)
                        to = node;
                    nodes.add(node);
                }
            }

            nodes.forEach(node -> {
                Set<String> neighbors = new HashSet<>();
                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y < 3; y++) {
                        if (x == 1 && y == 1)
                            continue;
                        if (Math.abs(x - node.getPos().x) <= 1 && Math.abs(y - node.getPos().y) <= 1)
                            neighbors.add(x + " " + y);
                    }
                }
                connections.put(node.getId(), neighbors);
            });

            Graph graph = new Graph(nodes, connections);
            Scorer<Node2d> nextNodeScorer = new Euclidean2dScorer();
            Scorer<Node2d> targetScorer = new Euclidean2dScorer();
            RouteFinder<Node2d> routeFinder = new RouteFinder<>(graph, nextNodeScorer, targetScorer);

            List<Node2d> route = routeFinder.findRoute(from, to);
            route.forEach(node -> {
                System.out.println(node.getPos());
            });
            System.out.println();

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
