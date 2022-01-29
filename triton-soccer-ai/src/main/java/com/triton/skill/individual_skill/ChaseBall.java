package com.triton.skill.individual_skill;

import com.triton.module.Module;
import com.triton.search.implementation.PathfindGridGroup;
import com.triton.skill.Skill;
import com.triton.skill.basic_skill.Dribble;
import com.triton.util.ObjectHelper;
import com.triton.util.Vector2d;
import proto.triton.FilteredObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.util.ProtobufUtils.getPos;
import static proto.triton.FilteredObject.*;
import static proto.triton.FilteredObject.Ball;
import static proto.triton.FilteredObject.Robot;

public class ChaseBall extends Skill {
    private final Robot actor;
    private final PathfindGridGroup pathfindGridGroup;
    private final FilteredWrapperPacket wrapper;

    public ChaseBall(Module module,
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

        Vector2d ballPos = getPos(ball);
        Vector2d predictPos = ObjectHelper.predictPos(ball, 0.25f);

        PathToTarget pathToTarget = new PathToTarget(module, actor, predictPos, ballPos, pathfindGridGroup);
        submitSkill(pathToTarget);

        Dribble dribble = new Dribble(module, actor, true);
        submitSkill(dribble);
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
    }
}
