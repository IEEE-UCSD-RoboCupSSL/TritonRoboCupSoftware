package com.triton.module;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class SkillModule extends Module implements Runnable {
    ArrayList<SkillModule> submodules;
    ScheduledExecutorService scheduledExecutorService;

    public SkillModule() {
        super();
        submodules = new ArrayList<>();
        scheduledExecutorService = Executors.newScheduledThreadPool(100);
    }

    public void scheduleSkill(SkillModule skill) {
        scheduleSkill(skill, 0, 100, TimeUnit.MILLISECONDS);
    }

    public void scheduleSkill(SkillModule skill, long delay, long period, TimeUnit timeUnit) {
        submodules.add(skill);
        scheduledExecutorService.scheduleAtFixedRate(skill, delay, period, timeUnit);
    }

    public void shutdown() {
        super.shutdown();
        submodules.forEach(SkillModule::shutdown);
        scheduledExecutorService.shutdown();
    }
}
