package com.triton.skill.coordinated_skill;

import com.triton.module.Module;
import com.triton.search.implementation.PathfindGridGroup;
import com.triton.skill.Skill;
import com.triton.skill.individual_skill.CatchBall;
import com.triton.skill.individual_skill.ChaseBall;
import com.triton.skill.individual_skill.KickFromPosition;
import com.triton.skill.individual_skill.PathToTarget;
import com.triton.util.Vector2d;
import proto.triton.ObjectWithMetadata;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.triton.constant.ProgramConstants.aiConfig;
import static com.triton.util.ObjectHelper.*;
import static com.triton.util.ProtobufUtils.getPos;
import static proto.simulation.SslSimulationRobotFeedback.RobotFeedback;
import static proto.triton.ObjectWithMetadata.*;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;

public class Pass extends Skill {
    private final Robot passer;
    private final Robot receiver;
    private final Vector2d passFrom;
    private final Vector2d passTo;
    private final FilteredWrapperPacket wrapper;
    private final PathfindGridGroup pathfindGridGroup;

    public Pass(Module module,
                Robot passer,
                Robot receiver,
                Vector2d passFrom,
                Vector2d passTo,
                FilteredWrapperPacket wrapper,
                PathfindGridGroup pathfindGridGroup) {
        super(module);
        this.passer = passer;
        this.receiver = receiver;
        this.passFrom = passFrom;
        this.passTo = passTo;
        this.wrapper = wrapper;
        this.pathfindGridGroup = pathfindGridGroup;
    }

    @Override
    protected void execute() {
        Ball ball = wrapper.getBall();
        Map<Integer, Robot> allies = wrapper.getAlliesMap();

        if (allies.get(receiver.getId()).getHasBall()) return;

        if (allies.get(passer.getId()).getHasBall()) {
            if (hasPos(receiver, passTo, aiConfig.passKickReceiverDistThreshold)) {
                KickFromPosition kickFromPosition = new KickFromPosition(module,
                        passer,
                        passFrom,
                        passTo,
                        pathfindGridGroup,
                        ball);
                submitSkill(kickFromPosition);

                PathToTarget pathToTarget = new PathToTarget(module, receiver, passTo, passFrom,
                        pathfindGridGroup);
                submitSkill(pathToTarget);
            } else {
                PathToTarget passerPathToTarget = new PathToTarget(module, passer, passFrom, passTo, pathfindGridGroup);
                submitSkill(passerPathToTarget);

                PathToTarget receiverPathToTarget = new PathToTarget(module, receiver, passTo, passFrom,
                        pathfindGridGroup);
                submitSkill(receiverPathToTarget);
            }
        } else {
            if (isMovingTowardTarget(ball, getPos(receiver), aiConfig.passCatchBallSpeedThreshold,
                    aiConfig.passCatchBallAngleTolerance)) {
                CatchBall catchBall = new CatchBall(module, receiver, pathfindGridGroup, ball);
                submitSkill(catchBall);
            } else {
                Vector2d passerPos = getPos(passer);
                Vector2d receiverPos = getPos(receiver);
                Vector2d predictBallPos = predictPos(ball, 0.25f);
                if (receiverPos.dist(predictBallPos) < passerPos.dist(predictBallPos)) {
                    ChaseBall chaseBall = new ChaseBall(module, receiver, pathfindGridGroup, ball);
                    submitSkill(chaseBall);

                    PathToTarget pathToTarget = new PathToTarget(module, passer, passFrom, passTo, pathfindGridGroup);
                    submitSkill(pathToTarget);
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
