package com.triton.skill.coordinated_skill;

import com.triton.module.Module;
import com.triton.search.implementation.PathfindGridGroup;
import com.triton.skill.Skill;
import com.triton.skill.individual_skill.PathToTarget;
import com.triton.util.Vector2d;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import static com.triton.util.ProtobufUtils.getPos;
import static proto.triton.ObjectWithMetadata.Robot;

public class PathToFormation extends Skill {
    private final HashMap<Vector2d, Float> positions;
    private final Map<Integer, Robot> allies;
    private final Map<Integer, Robot> foes;
    private final PathfindGridGroup pathfindGridGroup;

    public PathToFormation(Module module,
                           HashMap<Vector2d, Float> positions,
                           Map<Integer, Robot> allies,
                           Map<Integer, Robot> foes,
                           PathfindGridGroup pathfindGridGroup) {
        super(module);
        this.positions = positions;
        this.allies = allies;
        this.foes = foes;
        this.pathfindGridGroup = pathfindGridGroup;
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

                Vector2d allyPos = getPos(ally);
                float dist = pos.dist(allyPos);
                if (dist < minDist) {
                    closetAlly = ally;
                    minDist = dist;
                }
            }

            PathToTarget pathToTarget = new PathToTarget(module,
                    closetAlly,
                    pos,
                    orientation,
                    pathfindGridGroup);
            submitSkill(pathToTarget);
            usedIds.add(closetAlly.getId());
        }
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {

    }
}
