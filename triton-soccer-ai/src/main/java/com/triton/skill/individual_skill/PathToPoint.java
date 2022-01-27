package com.triton.skill.individual_skill;

import com.triton.module.Module;
import com.triton.search.node2d.Node2d;
import com.triton.search.node2d.PathfindGrid;
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

public class PathToPoint extends Skill {
    private final Robot ally;
    private final Vector2d pos;
    private final PathfindGrid pathfindGrid;
    private final float orientation;
    private final Vector2d facePos;

    public PathToPoint(Module module,
                       Robot ally,
                       Vector2d pos,
                       float orientation,
                       PathfindGrid pathfindGrid) {
        super(module);
        this.ally = ally;
        this.pos = pos;
        this.orientation = orientation;
        this.facePos = null;
        this.pathfindGrid = pathfindGrid;
    }

    public PathToPoint(Module module,
                       Robot ally,
                       Vector2d pos,
                       Vector2d facePos,
                       PathfindGrid pathfindGrid) {
        super(module);
        this.ally = ally;
        this.pos = pos;
        this.orientation = 0;
        this.facePos = facePos;
        this.pathfindGrid = pathfindGrid;
    }

    @Override
    protected void execute() {
        Vector2d from = getPos(ally);
        LinkedList<Node2d> route = pathfindGrid.findRoute(from, pos);
        Vector2d next = pathfindGrid.findNext(route);
        if (next == null) return;

        MoveToPoint moveToPoint;
        if (facePos != null)
            moveToPoint = new MoveToPoint(module, ally, next, facePos);
        else
            moveToPoint = new MoveToPoint(module, ally, next, orientation);
        submitSkill(moveToPoint);
        publishDebug(route, from, pos, next);
    }

    private void publishDebug(List<Node2d> route, Vector2d from, Vector2d to, Vector2d next) {
        AiDebugInfo.Debug.Builder debug = AiDebugInfo.Debug.newBuilder();
        AiDebugInfo.DebugPath.Builder path = AiDebugInfo.DebugPath.newBuilder();
        path.setId(ally.getId());
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
