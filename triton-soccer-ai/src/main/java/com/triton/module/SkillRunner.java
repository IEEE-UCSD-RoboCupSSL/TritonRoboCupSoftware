package com.triton.module;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class SkillRunner extends Module {
    public SkillRunner(ScheduledThreadPoolExecutor executor) {
        super(executor);
    }

    @Override
    public void run() {
        super.run();
        executor.scheduleAtFixedRate(this::execute, 0, 1, TimeUnit.MILLISECONDS);
    }

    protected abstract void execute();

    @Override
    public void interrupt() {
        super.interrupt();
        executor.remove(this::execute);
    }
}
