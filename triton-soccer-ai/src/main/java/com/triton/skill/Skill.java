package com.triton.skill;

import com.triton.messaging.Exchange;
import com.triton.module.Module;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class Skill extends Thread {
    protected Module module;

    public Skill(Module module) {
        this.module = module;
    }

    protected void publish(Exchange exchange, Object object) {
        module.publish(exchange, object);
    }
}
