package com.triton.module.debug_module;

import com.triton.module.Module;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_BIASED_SIMULATOR_CONTROL;

public class SimulatorControlSource extends Module {

    public SimulatorControlSource() throws IOException, TimeoutException {
        super();
        declareExchanges();
    }

    @Override
    protected void loadConfig() throws IOException {
        super.loadConfig();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declarePublish(AI_BIASED_SIMULATOR_CONTROL);
    }

    @Override
    public void run() {
        super.run();
    }
}
