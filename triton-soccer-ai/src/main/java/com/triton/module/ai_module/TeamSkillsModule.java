package com.triton.module.ai_module;

import com.rabbitmq.client.Delivery;
import com.triton.module.Module;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;

public class TeamSkillsModule extends Module {
    public TeamSkillsModule() throws IOException, TimeoutException {
        super();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_TEAM_SKILL, this::callbackTeamSkill);

        declarePublish(AI_COORDINATED_SKILL);
        declarePublish(AI_INDIVIDUAL_SKILL);
        declarePublish(AI_BASIC_SKILL);
    }

    private void callbackTeamSkill(String s, Delivery delivery) {
    }
}
