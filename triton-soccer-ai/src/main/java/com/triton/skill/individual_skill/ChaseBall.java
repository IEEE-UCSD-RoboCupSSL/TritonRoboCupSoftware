package com.triton.skill.individual_skill;

import com.triton.module.Module;
import com.triton.search.implementation.PathfindGridGroup;
import com.triton.skill.Skill;
import com.triton.skill.basic_skill.Dribble;
import com.triton.util.ObjectHelper;
import com.triton.util.Vector2d;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.util.ProtobufUtils.getPos;
import static proto.triton.FilteredObject.*;

public class ChaseBall extends Skill {
    private final Robot actor;
    private final FilteredWrapperPacket wrapper;
    private final PathfindGridGroup pathfindGridGroup;

    public ChaseBall(Module module,
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
