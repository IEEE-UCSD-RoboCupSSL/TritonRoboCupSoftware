package com.triton.skill.individual_skill;

import com.triton.module.Module;
import com.triton.search.implementation.PathfindGridGroup;
import com.triton.skill.Skill;
import com.triton.skill.basic_skill.Dribble;
import com.triton.util.Vector2d;
import proto.triton.FilteredObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.util.ProtobufUtils.getPos;
import static com.triton.util.ProtobufUtils.getVel;
import static proto.triton.FilteredObject.*;
import static proto.triton.FilteredObject.Ball;
import static proto.triton.FilteredObject.Robot;

public class CatchBall extends Skill {
    private final Robot actor;
    private final PathfindGridGroup pathfindGridGroup;
    private final FilteredWrapperPacket wrapper;

    public CatchBall(Module module,
                     Robot actor,
                     PathfindGridGroup pathfindGridGroup,
                     FilteredWrapperPacket wrapper) {
        super(module);
        this.actor = actor;
        this.pathfindGridGroup = pathfindGridGroup;
        this.wrapper = wrapper;
    }

    @Override
    protected void execute() {
        Ball ball = wrapper.getBall();

        Vector2d allyPos = getPos(actor);
        Vector2d ballPos = getPos(ball);
        Vector2d ballVel = getVel(ball);

        Vector2d diff = allyPos.sub(ballPos);
        Vector2d offset = diff.project(ballVel);
        Vector2d targetPos = ballPos.add(offset);

        PathToTarget pathToTarget = new PathToTarget(module,
                actor,
                targetPos,
                ballPos,
                pathfindGridGroup);
        submitSkill(pathToTarget);

        Dribble dribble = new Dribble(module, actor, true);
        submitSkill(dribble);
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {

    }
}
