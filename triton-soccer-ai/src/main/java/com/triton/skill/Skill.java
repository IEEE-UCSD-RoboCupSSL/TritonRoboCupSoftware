package com.triton.skill;

import com.triton.module.Module;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class Skill extends Thread {
    protected Module module;
    private final Set<Future> skillFuture;

    public Skill(Module module) {
        this.module = module;
        skillFuture = new HashSet<>();
    }

    @Override
    public void run() {
        super.run();
        execute();
        waitForSkills();
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
        skillFuture.add(module.executor.submit(skill));
    }
}
