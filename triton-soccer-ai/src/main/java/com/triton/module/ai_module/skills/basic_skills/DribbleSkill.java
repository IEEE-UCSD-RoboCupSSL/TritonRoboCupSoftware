package com.triton.module.ai_module.skills.basic_skills;

import com.triton.module.SkillModule;
import proto.simulation.SslSimulationRobotControl;

import java.io.IOException;

import static com.triton.messaging.Exchange.AI_BIASED_ROBOT_COMMAND;

public class DribbleSkill extends SkillModule {
    private int allyId;
    private boolean dribbleOn;

    public DribbleSkill(int allyId, boolean dribbleOn) {
        super();
        this.allyId = allyId;
        this.dribbleOn = dribbleOn;
    }

    @Override
    protected void declareExchanges() throws IOException {
        super.declareExchanges();
        declarePublish(AI_BIASED_ROBOT_COMMAND);
    }

    @Override
    public void run() {
        SslSimulationRobotControl.RobotCommand.Builder robotCommand = SslSimulationRobotControl.RobotCommand.newBuilder();
        robotCommand.setId(allyId);
        // TODO WORK ON DRIBBLE
        if (dribbleOn)
            robotCommand.setDribblerSpeed(1);
        else
            robotCommand.setDribblerSpeed(0);
        publish(AI_BIASED_ROBOT_COMMAND, robotCommand.build());
    }

    public void setAllyId(int allyId) {
        this.allyId = allyId;
    }

    public void setDribbleOn(boolean dribbleOn) {
        this.dribbleOn = dribbleOn;
    }
}
