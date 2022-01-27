package com.triton.skill.coordinated_skill;

import com.triton.module.Module;
import com.triton.search.implementation.PathfindGridGroup;
import com.triton.skill.Skill;
import com.triton.skill.individual_skill.CatchBall;
import com.triton.skill.individual_skill.ChaseBall;
import com.triton.skill.individual_skill.KickFromPosition;
import com.triton.skill.individual_skill.PathToTarget;
import com.triton.util.Vector2d;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.triton.constant.ProgramConstants.aiConfig;
import static com.triton.util.ObjectHelper.isMovingTowardTarget;
import static com.triton.util.ObjectHelper.predictPos;
import static com.triton.util.ProtobufUtils.getPos;
import static proto.simulation.SslSimulationRobotFeedback.RobotFeedback;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;

public class Pass extends Skill {
    private final Robot passer;
    private final Robot receiver;
    private final Vector2d passFrom;
    private final Vector2d passTo;
    private final PathfindGridGroup pathfindGridGroup;
    private final Ball ball;
    private final Map<Integer, RobotFeedback> feedbacks;

    public Pass(Module module,
                Robot passer,
                Robot receiver,
                Vector2d passFrom,
                Vector2d passTo,
                PathfindGridGroup pathfindGridGroup,
                Ball ball,
                Map<Integer, RobotFeedback> feedbacks) {
        super(module);
        this.passer = passer;
        this.receiver = receiver;
        this.passFrom = passFrom;
        this.passTo = passTo;
        this.pathfindGridGroup = pathfindGridGroup;
        this.ball = ball;
        this.feedbacks = feedbacks;
    }

    @Override
    protected void execute() {
        if (feedbacks.get(receiver.getId()).getDribblerBallContact()) return;

        if (feedbacks.get(passer.getId()).getDribblerBallContact()) {
            KickFromPosition kickFromPosition = new KickFromPosition(module,
                    passer,
                    passFrom,
                    passTo,
                    pathfindGridGroup,
                    ball);
            submitSkill(kickFromPosition);

            PathToTarget pathToTarget = new PathToTarget(module, receiver, passTo, passFrom, pathfindGridGroup);
            submitSkill(pathToTarget);
        } else {
            if (isMovingTowardTarget(ball, getPos(receiver), aiConfig.passCatchBallSpeedThreshold,
                    aiConfig.passCatchBallAngleTolerance)) {
                CatchBall catchBall = new CatchBall(module, receiver, pathfindGridGroup, ball);
                submitSkill(catchBall);
            } else {
                Vector2d passerPos = getPos(passer);
                Vector2d receiverPos = getPos(receiver);
                Vector2d predictBallPos = predictPos(ball, 0.5f);
                if (receiverPos.dist(predictBallPos) < passerPos.dist(predictBallPos)) {
                    ChaseBall chaseBall = new ChaseBall(module, receiver, pathfindGridGroup, ball);
                    submitSkill(chaseBall);
                } else {
                    ChaseBall chaseBall = new ChaseBall(module, passer, pathfindGridGroup, ball);
                    submitSkill(chaseBall);
                    PathToTarget pathToTarget = new PathToTarget(module, receiver, passTo, passFrom, pathfindGridGroup);
                    submitSkill(pathToTarget);
                }
            }
        }
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
    }
}
