package com.triton.skill;

import com.triton.messaging.Exchange;
import com.triton.module.Module;

public abstract class Skill extends Thread {
    protected Module module;

    public Skill(Module module) {
        this.module = module;
    }

    protected void publish(Exchange exchange, Object object) {
        module.publish(exchange, object);
    }
}
