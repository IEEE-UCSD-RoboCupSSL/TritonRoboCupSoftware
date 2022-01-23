package com.triton.module.ai_module.core;

import com.rabbitmq.client.Delivery;
import com.triton.module.Module;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;

public class CoordinatedSkillsModule extends Module {
    public CoordinatedSkillsModule() throws IOException, TimeoutException {
        super();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_COORDINATED_SKILL, this::callbackCoordinatedSkill);

        declarePublish(AI_INDIVIDUAL_SKILL);
        declarePublish(AI_BASIC_SKILL);
    }

    private void callbackCoordinatedSkill(String s, Delivery delivery) {
    }
}
