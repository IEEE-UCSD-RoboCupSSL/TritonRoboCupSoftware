package com.triton.skill;

import com.triton.messaging.Exchange;
import com.triton.module.Module;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class Skill extends Thread {
    protected Module module;
    private ArrayList<Skill> subskills;
    private ScheduledExecutorService executor;

    public Skill(Module module) {
        this.module = module;
        subskills = new ArrayList<>();
        executor = Executors.newScheduledThreadPool(100);
    }

    protected void publish(Exchange exchange, Object object) {
        module.publish(exchange, object);
    }

    protected void scheduleSkill(Skill skill, long delay, long period, TimeUnit timeUnit) {
        executor.scheduleAtFixedRate(skill, delay, period, timeUnit);
        subskills.add(skill);
    }

    protected void scheduleSkill(Skill skill) {
        scheduleSkill(skill, 0, 10, TimeUnit.MILLISECONDS);
    }

    @Override
    public void interrupt() {
        super.interrupt();
        executor.shutdown();
        subskills.forEach(Skill::interrupt);
    }
}
