package com.triton.module;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class TestRunner extends SkillRunner {
    private Future setupTestFuture;
    private long delay;
    private long period;
    private TimeUnit timeUnit;

    public TestRunner(ScheduledThreadPoolExecutor executor, long delay, long period, TimeUnit timeUnit) {
        super(executor);
        this.delay = delay;
        this.period = period;
        this.timeUnit = timeUnit;
        scheduleSetupTest(delay, period, timeUnit);
    }

    private void scheduleSetupTest(long delay, long period, TimeUnit timeUnit) {
        this.delay = delay;
        this.period = period;
        this.timeUnit = timeUnit;
        setupTestFuture = executor.scheduleAtFixedRate(this::setupTest, delay, period, timeUnit);
    }

    protected abstract void setupTest();

    @Override
    public void interrupt() {
        super.interrupt();
        if (setupTestFuture != null)
            setupTestFuture.cancel(false);
    }

    protected void reset() {
        if (setupTestFuture != null)
            setupTestFuture.cancel(false);
        scheduleSetupTest(delay, period, timeUnit);
    }
}
