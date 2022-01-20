package com.triton.module.debug_module;

import com.triton.config.ObjectConfig;
import com.triton.module.Module;
import proto.simulation.SslSimulationControl.TeleportBall;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.config.ConfigPath.OBJECT_CONFIG;
import static com.triton.config.ConfigReader.readConfig;
import static com.triton.messaging.Exchange.AI_BIASED_SIMULATOR_CONTROL;
import static proto.simulation.SslSimulationControl.SimulatorControl;

public class SimulatorControlSource extends Module {
    private ObjectConfig objectConfig;

    public SimulatorControlSource() throws IOException, TimeoutException {
        super();
        declareExchanges();
    }

    @Override
    protected void loadConfig() throws IOException {
        super.loadConfig();
        objectConfig = (ObjectConfig) readConfig(OBJECT_CONFIG);
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declarePublish(AI_BIASED_SIMULATOR_CONTROL);
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            SimulatorControl.Builder simulatorControl = SimulatorControl.newBuilder();

            TeleportBall.Builder teleportBall = TeleportBall.newBuilder();
            teleportBall.setX(0);
            teleportBall.setY(0);
            teleportBall.setZ(0);
            teleportBall.setVx(0);
            teleportBall.setVy(0);
            teleportBall.setVz(0);
            teleportBall.setByForce(false);
            simulatorControl.setTeleportBall(teleportBall);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
