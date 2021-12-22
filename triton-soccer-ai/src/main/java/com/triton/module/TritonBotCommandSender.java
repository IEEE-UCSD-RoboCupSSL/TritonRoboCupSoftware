package com.triton.module;

import com.triton.messaging.Module;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TritonBotCommandSender extends Module {
    public TritonBotCommandSender() throws IOException, TimeoutException {
        super();
    }

    @Override
    protected void loadConfig() throws IOException {
        super.loadConfig();
    }

    @Override
    protected void prepare() {
        super.prepare();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
    }
}
