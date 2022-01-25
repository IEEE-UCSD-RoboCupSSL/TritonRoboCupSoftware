package com.triton.module;

import java.util.concurrent.TimeUnit;

public abstract class TestModule extends SkillModule {

    public TestModule() {
        super();
    }

    protected abstract void setupTest();

    protected void scheduleSetupTest(long delay, long period, TimeUnit timeUnit) {
        scheduledExecutorService.scheduleAtFixedRate(this::setupTest, delay, period, timeUnit);
    }
}
