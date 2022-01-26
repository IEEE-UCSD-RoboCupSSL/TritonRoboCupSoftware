package com.triton.module;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class TestRunner extends SkillRunner {
    public TestRunner(ScheduledThreadPoolExecutor executor) {
        super(executor);
    }

    @Override
    public void run() {
        super.run();
    }

    @Override
    public void interrupt() {
        super.interrupt();
        executor.remove(this::setupTest);
    }

    protected abstract void setupTest();

    protected void scheduleSetupTest(long delay, long period, TimeUnit timeUnit) {
        executor.scheduleAtFixedRate(this::setupTest, delay, period, timeUnit);
    }
}
