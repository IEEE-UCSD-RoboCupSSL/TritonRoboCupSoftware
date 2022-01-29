package com.triton.skill.coordinated_skill;

import com.triton.module.Module;
import com.triton.search.implementation.PathfindGridGroup;
import com.triton.skill.Skill;
import com.triton.skill.individual_skill.PathToTarget;
import com.triton.util.Vector2d;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

import static com.triton.constant.ProgramConstants.*;
import static com.triton.util.ObjectHelper.*;
import static com.triton.util.ProtobufUtils.getPos;
import static proto.triton.FilteredObject.*;
import static proto.triton.FilteredObject.Robot;

public class PathToFormation extends Skill {
    private final Map<Vector2d, Float> positions;
    private final List<Robot> actors;
    private final FilteredWrapperPacket wrapper;
    private final PathfindGridGroup pathfindGridGroup;

    public PathToFormation(Module module,
                           Map<Vector2d, Float> positions,
                           List<Robot> actors,
                           FilteredWrapperPacket wrapper,
                           PathfindGridGroup pathfindGridGroup) {
        super(module);
        this.positions = positions;
        this.actors = actors;
        this.wrapper = wrapper;
        this.pathfindGridGroup = pathfindGridGroup;
    }

    @Override
    protected void execute() {
        Map<Integer, Robot> allies = wrapper.getAlliesMap();

        Set<Integer> usedIds = new HashSet<>();

        for (Map.Entry<Vector2d, Float> positionEntry : positions.entrySet()) {
            Vector2d pos = positionEntry.getKey();
            float orientation = positionEntry.getValue();

            Robot closetAlly = null;
            float minDist = Float.MAX_VALUE;

            for (Robot actor : actors) {
                int id = actor.getId();
                if (usedIds.contains(id)) continue;

                Vector2d allyPos = getPos(actor);
                float dist = pos.dist(allyPos);
                if (dist < minDist) {
                    closetAlly = actor;
                    minDist = dist;
                }
            }

            if (closetAlly == null) return;

            List<Robot> alliesWithoutClosestAlly = new ArrayList<>(allies.values());
            alliesWithoutClosestAlly.remove(closetAlly);
            if (isWithinDist(pos, alliesWithoutClosestAlly, aiConfig.pathToFormationOccupyDist))
                continue;

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
