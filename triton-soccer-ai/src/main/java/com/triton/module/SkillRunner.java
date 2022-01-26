package com.triton.module;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class SkillRunner extends Module {
    private static final long DEFAULT_SKILL_EXECUTION_PERIOD = 100;

    public SkillRunner(ScheduledThreadPoolExecutor executor) {
        super(executor);
    }

    @Override
    public void run() {
        super.run();
        executor.scheduleAtFixedRate(this::execute, 0, DEFAULT_SKILL_EXECUTION_PERIOD, TimeUnit.MILLISECONDS);
    }

    protected abstract void execute();

    @Override
    public void interrupt() {
        super.interrupt();
        executor.remove(this::execute);
    }
}
