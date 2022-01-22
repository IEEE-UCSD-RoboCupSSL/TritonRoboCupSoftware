package com.triton.module.ai_module;

import com.rabbitmq.client.Delivery;
import com.triton.ai.skills.basic_skills.*;
import com.triton.module.Module;
import proto.vision.MessagesRobocupSslDetection;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslSimulationRobotControl.RobotCommand;
import static proto.triton.AiBasicSkills.BasicSkill;

public class BasicSkillsModule extends Module {
    private HashMap<Integer, MessagesRobocupSslDetection.SSL_DetectionRobot> allies;

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
        HashMap<Integer, MessagesRobocupSslDetection.SSL_DetectionRobot> allies;
        try {
            allies = (HashMap<Integer, MessagesRobocupSslDetection.SSL_DetectionRobot>) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        this.allies = allies;
    }

    private void callbackBasicSkill(String s, Delivery delivery) {
        BasicSkill basicSkill;
        try {
            basicSkill = (BasicSkill) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        int id = basicSkill.getId();

        try {
            switch (basicSkill.getCommandCase()) {
                case STOP -> StopSkill.stopSkill(this, id, basicSkill.getStop());
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
