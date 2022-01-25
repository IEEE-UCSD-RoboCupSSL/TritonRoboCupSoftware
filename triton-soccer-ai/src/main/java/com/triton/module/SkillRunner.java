package com.triton.module;

import com.triton.skill.Skill;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class SkillRunner extends Module implements Runnable {
    private ArrayList<Skill> skills;
    protected ScheduledExecutorService executor;

    public SkillRunner() {
        super();
        skills = new ArrayList<>();
    }

    protected void scheduleSkill(Skill skill, long delay, long period, TimeUnit timeUnit) {
        executor.scheduleAtFixedRate(skill, delay, period, timeUnit);
        skills.add(skill);
    }

    protected void scheduleSkill(Skill skill) {
        scheduleSkill(skill, 0, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public void interrupt() {
        super.interrupt();
        executor.shutdown();
        skills.forEach(Skill::interrupt);
    }
}
