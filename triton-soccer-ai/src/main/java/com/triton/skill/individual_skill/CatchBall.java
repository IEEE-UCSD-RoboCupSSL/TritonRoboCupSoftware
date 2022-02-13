package com.triton.skill.individual_skill;

import com.triton.module.Module;
import com.triton.search.implementation.PathfindGridGroup;
import com.triton.skill.Skill;
import com.triton.skill.basic_skill.Dribble;
import com.triton.util.Vector2d;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.util.ProtobufUtils.getPos;
import static com.triton.util.ProtobufUtils.getVel;
import static proto.triton.FilteredObject.*;

public class CatchBall extends Skill {
    private final Robot actor;
    private final FilteredWrapperPacket wrapper;
    private final PathfindGridGroup pathfindGridGroup;

    public CatchBall(Module module,
                     Robot actor,
                     FilteredWrapperPacket wrapper,
                     PathfindGridGroup pathfindGridGroup) {
        super(module);
        this.actor = actor;
        this.wrapper = wrapper;
        this.pathfindGridGroup = pathfindGridGroup;
    }

    @Override
    protected void execute() {
        Ball ball = wrapper.getBall();

        Vector2d ballPos = getPos(ball);
        Vector2d ballToActor = getPos(actor).sub(ballPos);
        Vector2d offset = ballToActor.project(getVel(ball));
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
