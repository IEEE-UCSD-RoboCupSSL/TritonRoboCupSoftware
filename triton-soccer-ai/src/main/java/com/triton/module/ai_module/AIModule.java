package com.triton.module.ai_module;

import com.triton.constant.ProgramConstants;
import com.triton.module.Module;
import com.triton.routine.base.Context;
import com.triton.routine.base.Routine;
import com.triton.routine.base.Runner;
import com.triton.routine.routines.decorator.Repeater;
import com.triton.routine.routines.leaf.action.MatchVelocity;
import com.triton.util.Vector2d;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

import static com.triton.routine.base.StackId.TARGET_ANGULAR;
import static com.triton.routine.base.StackId.TARGET_VEL;

public class AIModule extends Module {
    private Map<Integer, Runner> runnerMap;
    private Context context;

    public AIModule(ScheduledThreadPoolExecutor executor) {
        super(executor);
    }

    @Override
    protected void prepare() {
        runnerMap = new HashMap<>();
        context = new Context();

        for (int id = 0; id < ProgramConstants.gameConfig.numBots; id++) {
            Routine matchVelocity = new MatchVelocity();
            Routine repeater = new Repeater(matchVelocity);

            context.pushToStack(id, TARGET_VEL, new Vector2d(0, 1));
            context.pushToStack(id, TARGET_ANGULAR, (float) (Math.PI * 2));

            Runner runner = new Runner(this, id, repeater, context);
            runnerMap.put(id, runner);
        }
    }

    @Override
    protected void declareConsumes() throws IOException, TimeoutException {
    }

    @Override
    public void run() {
        super.run();
        runnerMap.forEach((id, runner) -> executor.submit(runner));
    }
}
