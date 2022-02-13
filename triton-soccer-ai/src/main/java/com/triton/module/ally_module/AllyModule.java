package com.triton.module.ally_module;

import com.triton.module.Module;
import com.triton.routine.base.Routine;
import com.triton.routine.base.Context;

import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

public abstract class AllyModule extends Module {
    private Context context;
    private Routine routine;

    public AllyModule(ScheduledThreadPoolExecutor executor) {
        super(executor);
    }

    @Override
    protected void prepare() {
        // IMPORT ROUTINE
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
    }

    @Override
    protected void declareConsumes() throws IOException, TimeoutException {
    }

    @Override
    public void run() {
        super.run();
        while (!isInterrupted()) {
            if (routine.getState() == null)
                routine.start();
            routine.act(this, context);
        }
    }
}
