package com.triton.skill;

import com.triton.module.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class Skill extends Thread {
    private final List<Future> skillFutures;
    protected Module module;

    public Skill(Module module) {
        this.module = module;
        skillFutures = new ArrayList<>();
    }

    @Override
    public void run() {
        super.run();
        execute();
        waitForSkills();
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
        skillFutures.add(module.executor.submit(skill));
    }
}
