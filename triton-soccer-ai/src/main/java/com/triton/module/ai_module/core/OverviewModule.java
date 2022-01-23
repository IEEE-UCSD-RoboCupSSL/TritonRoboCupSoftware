package com.triton.module.ai_module.core;

import com.triton.module.Module;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;

public class OverviewModule extends Module {
    public OverviewModule() throws IOException, TimeoutException {
        super();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declarePublish(AI_STRATEGY);
        declarePublish(AI_TEAM_SKILL);
        declarePublish(AI_COORDINATED_SKILL);
        declarePublish(AI_INDIVIDUAL_SKILL);
        declarePublish(AI_BASIC_SKILL);
    }
}
