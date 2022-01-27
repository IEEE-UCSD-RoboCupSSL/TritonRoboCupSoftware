package com.triton.skill;

import com.triton.messaging.Exchange;
import com.triton.module.Module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

public abstract class Skill extends Thread {
    private final List<Future> skillFutures;
    protected Module module;

    public Skill(Module module) {
        this.module = module;
        skillFutures = new ArrayList<>();
    }

    @Override
    public void run() {
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

    protected abstract void execute();

    @Override
    public void interrupt() {
        super.interrupt();
        skillFutures.forEach(future -> future.cancel(false));
    }

    protected void submitSkill(Skill skill) {
        skillFutures.add(module.executor.submit(skill));
    }

    protected abstract void declarePublishes() throws IOException, TimeoutException;

    protected void declarePublish(Exchange exchange) throws IOException, TimeoutException {
        module.declarePublish(exchange);
    }

    protected void publish(Exchange exchange, Object object) {
        module.publish(exchange, object);
    }
}
