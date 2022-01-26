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
            execute();
            waitForSkills();
        }
    }

    protected abstract void execute();

    private void waitForSkills() {
        skillFutures.forEach(skillFuture -> {
            try {
                skillFuture.get();
            } catch (InterruptedException | ExecutionException ignored) {
            }
        });
        skillFutures.clear();
    }

    protected void submitSkill(Skill skill) {
        skillFutures.add(executor.submit(skill));
    }
}
