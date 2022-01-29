package com.triton.module.test_module.coordinated_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.constant.ProgramConstants;
import com.triton.module.TestRunner;
import com.triton.search.implementation.PathfindGridGroup;
import com.triton.skill.coordinated_skill.Pass;
import com.triton.util.Vector2d;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

import static com.triton.constant.ProgramConstants.gameConfig;
import static com.triton.messaging.Exchange.AI_BIASED_SIMULATOR_CONTROL;
import static com.triton.messaging.Exchange.AI_FILTERED_VISION_WRAPPER;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static com.triton.util.ProtobufUtils.createTeleportBall;
import static com.triton.util.ProtobufUtils.createTeleportRobot;
import static proto.simulation.SslSimulationControl.SimulatorControl;
import static proto.triton.FilteredObject.FilteredWrapperPacket;
import static proto.triton.FilteredObject.Robot;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class PassTest extends TestRunner {
    private PathfindGridGroup pathfindGridGroup;
    private FilteredWrapperPacket wrapper;

    private int passerId = 1;
    private int receiverId = 2;

    public PassTest(ScheduledThreadPoolExecutor executor) {
        super(executor);
        setupTest();
//        scheduleSetupTest(0, 5000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void setupTest() {
        SimulatorControl.Builder simulatorControl = SimulatorControl.newBuilder();
        simulatorControl.addTeleportRobot(createTeleportRobot(ProgramConstants.team, 1, 0, 2000, 0));
        simulatorControl.addTeleportRobot(createTeleportRobot(ProgramConstants.team, 2, 0, -2000, 0));
        simulatorControl.setTeleportBall(createTeleportBall(0, 0, 0));
        publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());
    }

    @Override
    protected void prepare() {
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
        declarePublish(AI_BIASED_SIMULATOR_CONTROL);
    }

    @Override
    protected void declareConsumes() throws IOException, TimeoutException {
        declareConsume(AI_FILTERED_VISION_WRAPPER, this::callbackWrapper);
    }

    private void callbackWrapper(String s, Delivery delivery) {
        wrapper = (FilteredWrapperPacket) simpleDeserialize(delivery.getBody());
    }


    @Override
    protected void execute() {
        if (wrapper == null) return;
        SSL_GeometryFieldSize field = wrapper.getField();
        Map<Integer, Robot> allies = wrapper.getAlliesMap();
        Map<Integer, Robot> foes = wrapper.getFoesMap();

        if (pathfindGridGroup == null)
            pathfindGridGroup = new PathfindGridGroup(gameConfig.numBots, field);
        pathfindGridGroup.updateObstacles(allies, foes);

        Robot passer = allies.get(passerId);
        Robot receiver = allies.get(receiverId);

        Vector2d passFrom = new Vector2d(0, 2000);
        Vector2d passTo = new Vector2d(0, -2000);

        if (allies.get(receiverId).getHasBall()) {
            int tmp = receiverId;
            receiverId = passerId;
            passerId = tmp;

            System.out.println("PASSER: " + passerId);
            System.out.println("RECEIVER: " + receiverId);
        } else {
            Pass pass = new Pass(this, passer, receiver, passFrom, passTo, wrapper, pathfindGridGroup);
            submitSkill(pass);
        }
    }
}
