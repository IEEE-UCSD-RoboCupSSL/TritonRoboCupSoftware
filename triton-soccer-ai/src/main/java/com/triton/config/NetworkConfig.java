package com.triton.config;

public class NetworkConfig {
    public String cameraAddress;
    public int cameraOutputPort;

    public String simulationCommandAddress;
    public int simulationCommandPort;

    public String simulationRobotControlAddressBlue;
    public int simulationRobotControlPortBlue;
    public String simulationRobotControlAddressYellow;
    public int simulationRobotControlPortYellow;

    public String tritonBotAddressBlue;
    public int tritonBotPortBaseBlue;
    public String tritonBotAddressYellow;
    public int tritonBotPortBaseYellow;
    public int tritonBotPortIncr;
}
