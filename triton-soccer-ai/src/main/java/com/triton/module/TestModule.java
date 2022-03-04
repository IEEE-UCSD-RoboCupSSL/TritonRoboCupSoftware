package com.triton.module;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public abstract class TestModule extends Module {
    public TestModule(ScheduledThreadPoolExecutor executor) {
        super(executor);
    }
}
