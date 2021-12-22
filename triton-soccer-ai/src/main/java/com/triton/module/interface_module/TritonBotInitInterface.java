package com.triton.module.interface_module;

import com.triton.module.Module;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TritonBotInitInterface extends Module {
    public TritonBotInitInterface() throws IOException, TimeoutException {
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
