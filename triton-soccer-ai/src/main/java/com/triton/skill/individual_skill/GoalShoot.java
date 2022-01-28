package com.triton.skill.individual_skill;

import com.triton.module.Module;
import com.triton.search.implementation.PathfindGridGroup;
import com.triton.skill.Skill;
import com.triton.util.Vector2d;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.triton.constant.ProgramConstants.aiConfig;
import static com.triton.util.ObjectHelper.distToPath;
import static com.triton.util.ProtobufUtils.getPos;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class GoalShoot extends Skill {
    private final Robot actor;
    private final Vector2d kickFromNear;
    private final PathfindGridGroup pathfindGridGroup;
    private final SSL_GeometryFieldSize field;
    private final Ball ball;
    private final Map<Integer, Robot> allies;
    private final Map<Integer, Robot> foes;

    public GoalShoot(Module module,
                     Robot actor,
                     Vector2d kickFromNear,
                     PathfindGridGroup pathfindGridGroup,
                     SSL_GeometryFieldSize field,
                     Ball ball,
                     Map<Integer, Robot> allies,
                     Map<Integer, Robot> foes) {
        super(module);
        this.actor = actor;
        this.kickFromNear = kickFromNear;
        this.pathfindGridGroup = pathfindGridGroup;
        this.field = field;
        this.ball = ball;
        this.allies = allies;
        this.foes = foes;
    }

    @Override
    protected void execute() {
        List<Robot> obstacles = new ArrayList<>();
        allies.forEach((id, ally) -> {
            if (ally.getId() != actor.getId())
                obstacles.add(ally);
        });
        obstacles.addAll(foes.values());

        List<Vector2d> kickFroms = kickFromNear.getGridNeighbors(aiConfig.goalKickFromSearchDist,
                aiConfig.goalKickFromSearchSpacing);

        float goalMinX = -field.getGoalWidth() / 2f + 300f;
        float goalMaxX = field.getGoalWidth() / 2f - 300f;
        float goalY = field.getFieldLength() / 2f;

        List<Vector2d> kickTos = new ArrayList<>();
        for (float goalX = goalMinX; goalX < goalMaxX; goalX += aiConfig.goalKickToSearchSpacing)
            kickTos.add(new Vector2d(goalX, goalY));

        Vector2d bestKickFrom = null;
        Vector2d bestKickTo = null;
        float maxScore = -Float.MAX_VALUE;
        for (Vector2d kickFrom : kickFroms) {
            for (Vector2d kickTo : kickTos) {
                float distToObstacles = distToPath(kickFrom, kickTo, obstacles);
                float distToShooter = getPos(actor).dist(kickFrom);
                float score = aiConfig.goalDistToObstaclesScoreFactor * distToObstacles
                        - aiConfig.goalDistToShooterScoreFactor * distToShooter;
                if (score > maxScore) {
                    bestKickFrom = kickFrom;
                    bestKickTo = kickTo;
                    maxScore = score;
                }
            }
        }

        KickFromPosition kickFromPosition = new KickFromPosition(module, actor, bestKickFrom, bestKickTo, pathfindGridGroup,
                ball);
        submitSkill(kickFromPosition);
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {

    }
}
