package com.triton.module.processing_module;

import com.rabbitmq.client.Delivery;
import com.triton.module.Module;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_BIASED_ROBOT_COMMAND;
import static com.triton.messaging.Exchange.AI_BIASED_ROBOT_CONTROL;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslSimulationRobotControl.RobotCommand;
import static proto.simulation.SslSimulationRobotControl.RobotControl;

public class RobotControlSplitter extends Module {
    public RobotControlSplitter() throws IOException, TimeoutException {
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
        declareConsume(AI_BIASED_ROBOT_CONTROL, this::callbackBiasedRobotControl);
        declarePublish(AI_BIASED_ROBOT_COMMAND);
    }

    private void callbackBiasedRobotControl(String s, Delivery delivery) {
        RobotControl biasedRobotControl;
        try {
            biasedRobotControl = (RobotControl) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        for (RobotCommand biasedRobotCommand : biasedRobotControl.getRobotCommandsList()) {
            try {
                publish(AI_BIASED_ROBOT_COMMAND, biasedRobotCommand);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
