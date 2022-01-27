package com.triton.module;

import com.triton.skill.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public abstract class SkillRunner extends Module {
    private final List<Future> skillFutures;

    public SkillRunner(ScheduledThreadPoolExecutor executor) {
        super(executor);
        skillFutures = new ArrayList<>();
    }

    @Override
    public void run() {
        super.run();
        while (!isInterrupted()) {
            try {
                execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

            skillFutures.forEach(future -> {
                try {
                    future.get();
                } catch (InterruptedException ignored) {
                } catch (ExecutionException e) {
                    e.getCause().printStackTrace();
                }
            });
        }
    }

    protected abstract void execute();

    protected void submitSkill(Skill skill) {
        skillFutures.add(executor.submit(skill));
    }

    @Override
    public void interrupt() {
        super.interrupt();
        skillFutures.forEach(future -> future.cancel(false));
    }
}
