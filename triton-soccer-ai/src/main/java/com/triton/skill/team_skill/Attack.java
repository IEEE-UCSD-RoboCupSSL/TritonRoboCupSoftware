package com.triton.skill.team_skill;

import com.triton.module.Module;
import com.triton.search.implementation.PathfindGridGroup;
import com.triton.skill.Skill;
import com.triton.skill.coordinated_skill.Pass;
import com.triton.skill.coordinated_skill.PathToFormation;
import com.triton.skill.individual_skill.CatchBall;
import com.triton.skill.individual_skill.ChaseBall;
import com.triton.skill.individual_skill.GoalKeep;
import com.triton.skill.individual_skill.GoalShoot;
import com.triton.util.Vector2d;
import proto.vision.MessagesRobocupSslGeometry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.triton.constant.ProgramConstants.aiConfig;
import static com.triton.util.ObjectHelper.*;
import static com.triton.util.ProtobufUtils.getDribbleStartPos;
import static com.triton.util.ProtobufUtils.getPos;
import static proto.triton.FilteredObject.*;

public class Attack extends Skill {
    private final PathfindGridGroup pathfindGridGroup;
    private final FilteredWrapperPacket wrapper;

    public Attack(Module module, PathfindGridGroup pathfindGridGroup, FilteredWrapperPacket wrapper) {
        super(module);
        this.pathfindGridGroup = pathfindGridGroup;
        this.wrapper = wrapper;
    }

    @Override
    protected void execute() {
        if (wrapper == null) return;
        MessagesRobocupSslGeometry.SSL_GeometryFieldSize field = wrapper.getField();
        Ball ball = wrapper.getBall();
        Map<Integer, Robot> allies = wrapper.getAlliesMap();

        Robot goalkeeper = allies.get(aiConfig.goalkeeperId);
        Map<Integer, Robot> fieldAllies = new HashMap<>(allies);
        fieldAllies.remove(goalkeeper.getId());

        Robot nearestAllyToBall = null;
        for (Robot ally : allies.values()) {
            if (ally.getHasBall())
                nearestAllyToBall = ally;
        }
        if (nearestAllyToBall == null)
            nearestAllyToBall = getNearestRobot(getPos(ball), fieldAllies.values().stream().toList());

        Map<Vector2d, Float> positions = new HashMap<>();
        positions.put(new Vector2d(-1000f, 3000f), 0f);
        positions.put(new Vector2d(0f, 3000f), 0f);
        positions.put(new Vector2d(1000f, 3000f), 0f);
        positions.put(new Vector2d(-2000f, 0f), 0f);
        positions.put(new Vector2d(0f, -2000f), 0f);
        positions.put(new Vector2d(2000f, 0f), 0f);

        if (nearestAllyToBall.getHasBall()) {
            Vector2d dribbleStartPos = getDribbleStartPos(nearestAllyToBall);
            Vector2d foeGoal = getFoeGoal(field);

            if (dribbleStartPos.dist(foeGoal) < 2000) {

                GoalShoot goalShoot = new GoalShoot(module,
                        nearestAllyToBall,
                        dribbleStartPos,
                        pathfindGridGroup,
                        wrapper);
                submitSkill(goalShoot);

                List<Robot> formationActors = new ArrayList<>(fieldAllies.values());
                formationActors.remove(nearestAllyToBall);
                PathToFormation pathToFormation = new PathToFormation(module,
                        positions,
                        formationActors,
                        wrapper,
                        pathfindGridGroup);
                submitSkill(pathToFormation);
            } else {
                List<Robot> formationActors = new ArrayList<>(fieldAllies.values());

                List<Robot> nearRobots = getNearRobots(getPos(nearestAllyToBall),
                        fieldAllies.values().stream().toList(), 5000);
                if (!nearRobots.isEmpty()) {
                    Robot allyToPass = getNearestRobot(foeGoal, nearRobots);
                    Pass pass = new Pass(module, nearestAllyToBall, allyToPass, dribbleStartPos,
                            getPos(allyToPass), wrapper, pathfindGridGroup);
                    submitSkill(pass);
                    formationActors.remove(nearestAllyToBall);
                    formationActors.remove(allyToPass);
                }

                PathToFormation pathToFormation = new PathToFormation(module,
                        positions,
                        formationActors,
                        wrapper,
                        pathfindGridGroup);
                submitSkill(pathToFormation);
            }
        } else {
            boolean ableToCatch = false;
            Robot capturingAlly = nearestAllyToBall;
            float minDist = Float.MAX_VALUE;

            for (Robot fieldAlly : fieldAllies.values()) {
                if (isMovingTowardTarget(ball, getPos(fieldAlly), aiConfig.passCatchBallSpeedThreshold,
                        aiConfig.passCatchBallAngleTolerance)) {
                    ableToCatch = true;

                    float dist = getPos(fieldAlly).dist(getPos(ball));
                    if (dist < minDist) {
                        capturingAlly = fieldAlly;
                        minDist = dist;
                    }
                }
            }

            if (!ableToCatch) {
                ChaseBall chaseBall = new ChaseBall(module,
                        capturingAlly,
                        wrapper,
                        pathfindGridGroup);
                submitSkill(chaseBall);
            } else {
                CatchBall catchBall = new CatchBall(module,
                        capturingAlly,
                        wrapper,
                        pathfindGridGroup);
                submitSkill(catchBall);
            }

            List<Robot> formationActors = new ArrayList<>(fieldAllies.values());
            formationActors.remove(capturingAlly);
            PathToFormation pathToFormation = new PathToFormation(module,
                    positions,
                    formationActors,
                    wrapper,
                    pathfindGridGroup);
            submitSkill(pathToFormation);
        }

        GoalKeep goalKeep = new GoalKeep(module, goalkeeper, wrapper);
        submitSkill(goalKeep);
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {

    }
}
