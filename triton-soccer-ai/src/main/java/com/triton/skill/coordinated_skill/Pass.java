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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.triton.constant.ProgramConstants.aiConfig;
import static com.triton.util.ObjectHelper.*;
import static com.triton.util.ProtobufUtils.getPos;
import static proto.triton.FilteredObject.*;

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
        Map<Integer, Robot> foes = wrapper.getFoesMap();

        if (allies.get(receiver.getId()).getHasBall()) return;

        List<Robot> obstacles = new ArrayList<>();
        allies.forEach((id, ally) -> {
            if (ally.getId() != passer.getId() || ally.getId() != receiver.getId())
                obstacles.add(ally);
        });
        obstacles.addAll(foes.values());

        List<Vector2d> passFroms = passFrom.getGridNeighbors(aiConfig.passKickFromSearchDist,
                aiConfig.passKickFromSearchSpacing);

        Vector2d bestPassFrom = null;
        float maxScore = -Float.MAX_VALUE;
        for (Vector2d passFrom : passFroms) {
            float distToObstacles = distToPath(passFrom, passTo, obstacles);
            float distToShooter = getPos(passer).dist(passFrom);
            float score = aiConfig.passDistToObstaclesScoreFactor * distToObstacles
                    - aiConfig.passDistToPasserScoreFactor * distToShooter;
            if (score > maxScore) {
                bestPassFrom = passFrom;
                maxScore = score;
            }
        }

        if (allies.get(passer.getId()).getHasBall()) {
            if (hasPos(receiver, passTo, aiConfig.passKickReceiverDistThreshold)) {
                KickFromPosition kickFromPosition = new KickFromPosition(module,
                        passer,
                        bestPassFrom,
                        passTo,
                        aiConfig.passKickSpeed,
                        pathfindGridGroup);
                submitSkill(kickFromPosition);

                PathToTarget pathToTarget = new PathToTarget(module, receiver, passTo, bestPassFrom,
                        pathfindGridGroup);
                submitSkill(pathToTarget);
            } else {
                PathToTarget passerPathToTarget = new PathToTarget(module, passer, bestPassFrom, passTo, pathfindGridGroup);
                submitSkill(passerPathToTarget);

                PathToTarget receiverPathToTarget = new PathToTarget(module, receiver, passTo, bestPassFrom,
                        pathfindGridGroup);
                submitSkill(receiverPathToTarget);
            }
        } else {
            if (isMovingTowardTarget(ball, getPos(receiver), aiConfig.passCatchBallSpeedThreshold,
                    aiConfig.passCatchBallAngleTolerance)) {
                CatchBall catchBall = new CatchBall(module, receiver, wrapper, pathfindGridGroup);
                submitSkill(catchBall);
            } else {
                Vector2d passerPos = getPos(passer);
                Vector2d receiverPos = getPos(receiver);
                Vector2d predictBallPos = predictPos(ball, 0.25f);
                if (receiverPos.dist(predictBallPos) < passerPos.dist(predictBallPos)) {
                    ChaseBall chaseBall = new ChaseBall(module, receiver, wrapper, pathfindGridGroup);
                    submitSkill(chaseBall);

                    PathToTarget pathToTarget = new PathToTarget(module, passer, bestPassFrom, passTo, pathfindGridGroup);
                    submitSkill(pathToTarget);
                } else {
                    ChaseBall chaseBall = new ChaseBall(module, passer, wrapper, pathfindGridGroup);
                    submitSkill(chaseBall);

                    PathToTarget pathToTarget = new PathToTarget(module, receiver, passTo, bestPassFrom, pathfindGridGroup);
                    submitSkill(pathToTarget);
                }
            }
        }
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
    }
}
