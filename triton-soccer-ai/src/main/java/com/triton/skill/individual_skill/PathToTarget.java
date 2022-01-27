package com.triton.skill.individual_skill;

import com.triton.module.Module;
import com.triton.search.implementation.PathfindGridGroup;
import com.triton.search.node2d.Node2d;
import com.triton.skill.Skill;
import com.triton.util.Vector2d;
import proto.triton.AiDebugInfo;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_DEBUG;
import static com.triton.util.ProtobufUtils.getPos;
import static proto.triton.ObjectWithMetadata.Robot;

public class PathToTarget extends Skill {
    private final Robot actor;
    private final Vector2d target;
    private final PathfindGridGroup pathfindGridGroup;
    private final float orientation;
    private final Vector2d facePos;

    public PathToTarget(Module module,
                        Robot actor,
                        Vector2d target,
                        float orientation,
                        PathfindGridGroup pathfindGridGroup) {
        super(module);
        this.actor = actor;
        this.target = target;
        this.orientation = orientation;
        this.facePos = null;
        this.pathfindGridGroup = pathfindGridGroup;
    }

    public PathToTarget(Module module,
                        Robot actor,
                        Vector2d target,
                        Vector2d facePos,
                        PathfindGridGroup pathfindGridGroup) {
        super(module);
        this.actor = actor;
        this.target = target;
        this.orientation = 0;
        this.facePos = facePos;
        this.pathfindGridGroup = pathfindGridGroup;
    }

    @Override
    protected void execute() {
        Vector2d from = getPos(actor);
        LinkedList<Node2d> route = pathfindGridGroup.findRoute(actor.getId(), from, target);
        Vector2d next = pathfindGridGroup.findNext(actor.getId(), route);
        if (next == null) return;

        MoveToTarget moveToTarget;
        if (facePos != null)
            moveToTarget = new MoveToTarget(module, actor, next, facePos);
        else
            moveToTarget = new MoveToTarget(module, actor, next, orientation);
        submitSkill(moveToTarget);
        publishDebug(route, from, target, next);
    }

    private void publishDebug(List<Node2d> route, Vector2d from, Vector2d to, Vector2d next) {
        AiDebugInfo.Debug.Builder debug = AiDebugInfo.Debug.newBuilder();
        AiDebugInfo.DebugPath.Builder path = AiDebugInfo.DebugPath.newBuilder();
        path.setId(actor.getId());
        path.setFromPos(AiDebugInfo.DebugVector.newBuilder().setX(from.x).setY(from.y));
        path.setToPos(AiDebugInfo.DebugVector.newBuilder().setX(to.x).setY(to.y));
        path.setNextPos(AiDebugInfo.DebugVector.newBuilder().setX(next.x).setY(next.y));
        route.forEach(node -> {
            Vector2d pos = node.getPos();
            path.addNodes(AiDebugInfo.DebugVector.newBuilder().setX(pos.x).setY(pos.y));
        });
        debug.setPath(path);
        publish(AI_DEBUG, debug.build());
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
        declarePublish(AI_DEBUG);
    }
}
