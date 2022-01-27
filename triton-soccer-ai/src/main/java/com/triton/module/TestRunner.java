package com.triton.module;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class TestRunner extends SkillRunner {
    Future setupTestFuture;

    public TestRunner(ScheduledThreadPoolExecutor executor) {
        super(executor);
    }

    @Override
    public void interrupt() {
        super.interrupt();
        if (setupTestFuture != null)
            setupTestFuture.cancel(false);
    }

    protected void scheduleSetupTest(long delay, long period, TimeUnit timeUnit) {
        setupTestFuture = executor.scheduleAtFixedRate(this::setupTest, delay, period, timeUnit);
    }

    protected abstract void setupTest();
}
