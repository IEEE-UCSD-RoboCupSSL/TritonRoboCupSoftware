package com.triton.module.ai_module;

import com.triton.constant.ProgramConstants;
import com.triton.module.Module;
import com.triton.routine.base.Context;
import com.triton.routine.base.Routine;
import com.triton.routine.base.Runner;
import com.triton.routine.routines.leaf.action.MatchVelocity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

public class AIModule extends Module {
    private Map<Integer, Runner> runnerMap;
    private Context context;

    public AIModule(ScheduledThreadPoolExecutor executor) {
        super(executor);
        runnerMap = new HashMap<>();
        context = new Context();
    }

    @Override
    protected void prepare() {
        for (int i = 0; i < ProgramConstants.gameConfig.numBots; i++) {
            Routine routine = generateRoutine();
            Context context = new Context();
            Runner runner = new Runner(routine, context);
            runnerMap.put(i, runner);
        }
    }

    private Routine generateRoutine() {
        Routine routine = new MatchVelocity();
        return routine;
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
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
