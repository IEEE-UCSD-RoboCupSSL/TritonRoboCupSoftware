package com.triton.skill.coordinated_skill;

import com.triton.module.Module;
import com.triton.search.node2d.PathfindGrid;
import com.triton.skill.Skill;
import com.triton.skill.individual_skill.PathToPoint;
import com.triton.util.Vector2d;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import static proto.triton.ObjectWithMetadata.Robot;

public class PathToFormation extends Skill {
    private final HashMap<Vector2d, Float> positions;
    private final Map<Integer, Robot> allies;
    private final Map<Integer, Robot> foes;
    private final Map<Integer, PathfindGrid> pathfindGrids;

    public PathToFormation(Module module,
                           HashMap<Vector2d, Float> positions,
                           Map<Integer, Robot> allies,
                           Map<Integer, Robot> foes,
                           Map<Integer, PathfindGrid> pathfindGrids) {
        super(module);
        this.positions = positions;
        this.allies = allies;
        this.foes = foes;
        this.pathfindGrids = pathfindGrids;
    }

    @Override
    protected void execute() {
        Set<Integer> usedIds = new HashSet<>();

        for (Map.Entry<Vector2d, Float> positionEntry : positions.entrySet()) {
            Vector2d pos = positionEntry.getKey();
            float orientation = positionEntry.getValue();

            Robot closetAlly = null;
            float minDist = Float.MAX_VALUE;

            for (Map.Entry<Integer, Robot> allyEntry : allies.entrySet()) {
                int id = allyEntry.getKey();
                Robot ally = allyEntry.getValue();

                if (usedIds.contains(id)) continue;

                Vector2d allyPos = new Vector2d(ally.getX(), ally.getY());
                float dist = pos.dist(allyPos);
                if (dist < minDist) {
                    closetAlly = ally;
                    minDist = dist;
                }
            }

            PathToPoint pathToPoint = new PathToPoint(module,
                    closetAlly,
                    pos,
                    orientation,
                    pathfindGrids.get(closetAlly.getId()),
                    allies,
                    foes);
            submitSkill(pathToPoint);
            usedIds.add(closetAlly.getId());
        }
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {

    }
}
