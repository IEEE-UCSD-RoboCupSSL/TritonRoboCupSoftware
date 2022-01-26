package com.triton.module;

import com.triton.skill.Skill;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public abstract class SkillRunner extends Module {
    private final Set<Future> skillFuture;

    public SkillRunner(ScheduledThreadPoolExecutor executor) {
        super(executor);
        skillFuture = new HashSet<>();
    }

    @Override
    public void run() {
        super.run();
        while (!isInterrupted()) {
            execute();
            waitForSkills();
        }
    }

    protected abstract void execute();

    private void waitForSkills() {
        skillFuture.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException ignored) {
            }
        });
        skillFuture.clear();
    }

    protected void submitSkill(Skill skill) {
        skillFuture.add(executor.submit(skill));
    }
}
