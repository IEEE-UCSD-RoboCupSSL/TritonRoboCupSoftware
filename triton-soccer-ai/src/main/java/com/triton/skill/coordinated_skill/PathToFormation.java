package com.triton.skill.coordinated_skill;

import com.triton.module.Module;
import com.triton.search.node2d.PathfindGrid;
import com.triton.skill.Skill;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;

public class PathToFormation extends Skill {
    private final Robot passer;
    private final Robot receiver;
    private final Ball ball;
    private final Map<Integer, Robot> allies;
    private final Map<Integer, Robot> foes;
    private final PathfindGrid pathfindGrid;

    public PathToFormation(Module module,
                           Robot passer,
                           Robot receiver,
                           Ball ball,
                           Map<Integer, Robot> allies,
                           Map<Integer, Robot> foes,
                           PathfindGrid pathfindGrid) {
        super(module);
        this.passer = passer;
        this.receiver = receiver;
        this.ball = ball;
        this.allies = allies;
        this.foes = foes;
        this.pathfindGrid = pathfindGrid;
    }

    @Override
    protected void execute() {
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {

    }
}
