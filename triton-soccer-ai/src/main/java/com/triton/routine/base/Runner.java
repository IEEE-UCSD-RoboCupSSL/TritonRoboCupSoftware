package com.triton.routine.base;

import com.triton.messaging.Exchange;
import com.triton.module.Module;

public class Runner extends Thread {
    private final Module module;
    private final int id;

    private Routine routine;
    private Context context;

    public Runner(Module module, int id, Routine routine, Context context) {
        this.module = module;
        this.id = id;
        this.routine = routine;
        this.context = context;
    }

    @Override
    public void run() {
        super.run();
        if (routine.getState() == null)
            routine.start();
        while (!isInterrupted()) {
            try {
                routine.act(this, context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int getAllyId() {
        return id;
    }

    public void setRoutine(Routine routine) {
        this.routine = routine;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void publish(Exchange exchange, Object object) {
        module.publish(exchange, object);
    }
}
