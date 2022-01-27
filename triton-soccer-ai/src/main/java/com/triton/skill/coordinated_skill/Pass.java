package com.triton.skill.coordinated_skill;

import com.triton.module.Module;
import com.triton.search.implementation.PathfindGridGroup;
import com.triton.skill.Skill;
import com.triton.skill.individual_skill.CatchBall;
import com.triton.skill.individual_skill.KickToPoint;
import com.triton.skill.individual_skill.PathToPoint;
import com.triton.util.ObjectHelper;
import com.triton.util.Vector2d;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.constant.ProgramConstants.aiConfig;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;

public class Pass extends Skill {
    private final Robot passer;
    private final Robot receiver;
    private final Vector2d passFrom;
    private final Vector2d passTo;
    private final PathfindGridGroup pathfindGridGroup;
    private final Ball ball;

    public Pass(Module module,
                Robot passer,
                Robot receiver,
                Vector2d passFrom,
                Vector2d passTo,
                PathfindGridGroup pathfindGridGroup,
                Ball ball) {
        super(module);
        this.passer = passer;
        this.receiver = receiver;
        this.passFrom = passFrom;
        this.passTo = passTo;
        this.pathfindGridGroup = pathfindGridGroup;
        this.ball = ball;
    }

    @Override
    protected void execute() {
        if (ObjectHelper.matchPos(passer, passFrom, aiConfig.passPosTolerance)) {
            KickToPoint kickToPoint = new KickToPoint(module, passer, passTo);
            submitSkill(kickToPoint);
        } else {
            PathToPoint pathToPoint = new PathToPoint(module,
                    passer,
                    passFrom,
                    passTo,
                    pathfindGridGroup);
            submitSkill(pathToPoint);
        }

        if (ObjectHelper.matchPos(receiver, passTo, aiConfig.passPosTolerance)) {
        } else {
            PathToPoint pathToPoint = new PathToPoint(module,
                    receiver,
                    passTo,
                    passFrom,
                    pathfindGridGroup);
            submitSkill(pathToPoint);
        }

        CatchBall catchBall = new CatchBall(module, receiver, pathfindGridGroup, ball);
        submitSkill(catchBall);
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
    }
}
