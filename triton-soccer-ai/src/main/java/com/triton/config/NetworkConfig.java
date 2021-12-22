package com.triton.config;

public class NetworkConfig {
    private String cameraAddress;
    private int cameraOutputPort;

    private String simulationControlAddress;
    private int simulationControlPort;

    private String simulationRobotControlBlueAddress;
    private int simulationRobotControlBluePort;

    private String simulationRobotControlYellowAddress;
    private int simulationRobotControlYellowPort;

    private String aiAddress;
    private int aiTritonBotPort;

    public String getCameraAddress() {
        return cameraAddress;
    }

    public void setCameraAddress(String cameraAddress) {
        this.cameraAddress = cameraAddress;
    }

    public int getCameraOutputPort() {
        return cameraOutputPort;
    }

    public void setCameraOutputPort(int cameraOutputPort) {
        this.cameraOutputPort = cameraOutputPort;
    }

    public String getSimulationControlAddress() {
        return simulationControlAddress;
    }

    public void setSimulationControlAddress(String simulationControlAddress) {
        this.simulationControlAddress = simulationControlAddress;
    }

    public int getSimulationControlPort() {
        return simulationControlPort;
    }

    public void setSimulationControlPort(int simulationControlPort) {
        this.simulationControlPort = simulationControlPort;
    }

    public String getSimulationRobotControlBlueAddress() {
        return simulationRobotControlBlueAddress;
    }

    public void setSimulationRobotControlBlueAddress(String simulationRobotControlBlueAddress) {
        this.simulationRobotControlBlueAddress = simulationRobotControlBlueAddress;
    }

    public int getSimulationRobotControlBluePort() {
        return simulationRobotControlBluePort;
    }

    public void setSimulationRobotControlBluePort(int simulationRobotControlBluePort) {
        this.simulationRobotControlBluePort = simulationRobotControlBluePort;
    }

    public String getSimulationRobotControlYellowAddress() {
        return simulationRobotControlYellowAddress;
    }

    public void setSimulationRobotControlYellowAddress(String simulationRobotControlYellowAddress) {
        this.simulationRobotControlYellowAddress = simulationRobotControlYellowAddress;
    }

    public int getSimulationRobotControlYellowPort() {
        return simulationRobotControlYellowPort;
    }

    public void setSimulationRobotControlYellowPort(int simulationRobotControlYellowPort) {
        this.simulationRobotControlYellowPort = simulationRobotControlYellowPort;
    }

    public String getAiAddress() {
        return aiAddress;
    }

    public void setAiAddress(String aiAddress) {
        this.aiAddress = aiAddress;
    }

    public int getAiTritonBotPort() {
        return aiTritonBotPort;
    }

    public void setAiTritonBotPort(int aiTritonBotPort) {
        this.aiTritonBotPort = aiTritonBotPort;
    }
}
