package com.triton.module.ai_module;

import com.rabbitmq.client.Delivery;
import com.triton.ai.skills.individual_skills.*;
import com.triton.module.Module;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_BASIC_SKILL;
import static com.triton.messaging.Exchange.AI_INDIVIDUAL_SKILL;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.triton.AiBasicSkills.BasicSkill;
import static proto.triton.AiIndividualSkills.IndividualSkill;

public class IndividualSkillsModule extends Module {
    public IndividualSkillsModule() throws IOException, TimeoutException {
        super();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_INDIVIDUAL_SKILL, this::callbackSkill);

        declarePublish(AI_BASIC_SKILL);
    }

    private void callbackSkill(String s, Delivery delivery) {
        IndividualSkill individualSkill;
        try {
            individualSkill = (IndividualSkill) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        int id = individualSkill.getId();

        BasicSkill basicSkill = null;
        switch (individualSkill.getCommandCase()) {
            case GOAL_KEEP -> basicSkill = GoalKeepSkill.goalKeepSkill(id, individualSkill.getGoalKeep());
            case PATHFIND_TO_POINT -> basicSkill = PathFindToPointSkill.pathFindToPointSkill(id, individualSkill.getPathfindToPoint());
            case CHASE_BALL -> basicSkill = ChaseBallSkill.chaseBallSkill(id, individualSkill.getChaseBall());
            case CATCH_BALL -> basicSkill = CatchBallSkill.catchBallSkill(id, individualSkill.getCatchBall());
            case KICK_BALL_TO_POINT -> basicSkill = KickBallToPointSkill.kickBallToPointSkill(id, individualSkill.getKickBallToPoint());
            case DRIBBLE_BALL -> basicSkill = DribbleBallSkill.dribbleBallSkill(id, individualSkill.getDribbleBall());
            case SHOOT -> basicSkill = ShootSkill.shootSkill(id, individualSkill.getShoot());
            case STEAL -> basicSkill = Stealskill.stealSkill(id, individualSkill.getSteal());
            case JUKE -> basicSkill = JukeSkill.jukeSkill(id, individualSkill.getJuke());
            default -> throw new IllegalStateException("Unexpected value: " + individualSkill.getCommandCase());
        }

        if (basicSkill != null) {
            try {
                publish(AI_BASIC_SKILL, basicSkill);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
