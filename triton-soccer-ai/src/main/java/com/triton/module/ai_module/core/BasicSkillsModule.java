package com.triton.module.ai_module.core;

import com.rabbitmq.client.Delivery;
import com.triton.ai.skills.basic_skills.DribbleSkill;
import com.triton.ai.skills.basic_skills.KickSkill;
import com.triton.ai.skills.basic_skills.MatchVelocitySkill;
import com.triton.ai.skills.basic_skills.MoveToPointSkill;
import com.triton.module.Module;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.triton.AiBasicSkills.BasicSkill;
import static proto.triton.ObjectWithMetadata.Robot;

public class BasicSkillsModule extends Module {
    private HashMap<Integer, Robot> allies;

    public BasicSkillsModule() throws IOException, TimeoutException {
        super();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_FILTERED_BIASED_ALLIES, this::callbackAllies);
        declareConsume(AI_BASIC_SKILL, this::callbackBasicSkill);

        declarePublish(AI_BIASED_ROBOT_COMMAND);
    }

    private void callbackAllies(String s, Delivery delivery) {
        this.allies = (HashMap<Integer, Robot>) simpleDeserialize(delivery.getBody());
    }

    private void callbackBasicSkill(String s, Delivery delivery) {
        BasicSkill basicSkill = (BasicSkill) simpleDeserialize(delivery.getBody());

        int id = basicSkill.getId();

        try {
            switch (basicSkill.getCommandCase()) {
                case MATCH_VELOCITY -> MatchVelocitySkill.matchVelocitySkill(this, id, basicSkill.getMatchVelocity());
                case MOVE_TO_POINT -> MoveToPointSkill.moveToPointSkill(this, id, basicSkill.getMoveToPoint(), allies.get(id));
                case KICK -> KickSkill.kickSkill(this, id, basicSkill.getKick());
                case DRIBBLE -> DribbleSkill.dribbleSkill(this, id, basicSkill.getDribble());
                default -> throw new IllegalStateException("Unexpected value: " + basicSkill.getCommandCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
