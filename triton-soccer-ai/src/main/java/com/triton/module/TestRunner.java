package com.triton.module;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class TestRunner extends SkillRunner {
    public TestRunner() {
        super();
        executor = Executors.newScheduledThreadPool(100);
        executor.scheduleAtFixedRate(this::run, 0, 100, TimeUnit.MILLISECONDS);
    }

    protected abstract void setupTest();

    protected void scheduleSetupTest(long delay, long period, TimeUnit timeUnit) {
        executor.scheduleAtFixedRate(this::setupTest, 0, period, timeUnit);
    }
}
