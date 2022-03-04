package com.triton.module.test_module;

import com.triton.module.TestModule;
import com.triton.routine.base.Context;
import com.triton.routine.base.Routine;
import com.triton.routine.base.Runner;
import com.triton.routine.routines.decorator.Repeater;
import com.triton.routine.routines.leaf.action.MatchVelocity;
import com.triton.util.Vector2d;

import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

import static com.triton.routine.base.StackId.TARGET_ANGULAR;
import static com.triton.routine.base.StackId.TARGET_VEL;

public class MatchVelocityTest extends TestModule {
    private static final int TEST_ALLY_ID = 1;

    private Runner runner;
    private Context context;

    public MatchVelocityTest(ScheduledThreadPoolExecutor executor) {
        super(executor);
    }

    @Override
    protected void prepare() {
        context = new Context();

        Routine matchVelocity = new MatchVelocity();
        Routine repeater = new Repeater(matchVelocity);

        context.pushToStack(TEST_ALLY_ID, TARGET_VEL, new Vector2d(0, 1));
        context.pushToStack(TEST_ALLY_ID, TARGET_ANGULAR, (float) (Math.PI * 2));

        runner = new Runner(this, TEST_ALLY_ID, repeater, context);
    }

    @Override
    protected void declareConsumes() throws IOException, TimeoutException {
    }

    @Override
    public void interrupt() {
        super.interrupt();
        runner.interrupt();
    }

    @Override
    public void run() {
        super.run();
        executor.submit(runner);
    }
}
