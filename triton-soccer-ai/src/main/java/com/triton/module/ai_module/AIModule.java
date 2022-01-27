package com.triton.module.ai_module;

import com.triton.module.Module;
import com.triton.module.SkillRunner;

import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

public class AIModule extends SkillRunner {
    public AIModule(ScheduledThreadPoolExecutor executor) {
        super(executor);
    }

    @Override
    protected void execute() {

    }

    @Override
    protected void prepare() {

    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
    }

    @Override
    protected void declareConsumes() throws IOException, TimeoutException {

    }
}
