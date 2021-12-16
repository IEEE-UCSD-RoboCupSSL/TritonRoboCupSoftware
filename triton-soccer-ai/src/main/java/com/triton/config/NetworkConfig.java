package com.triton.config;

public class NetworkConfig {
    private String cameraOutputAddress;
    private int cameraOutputPort;

    private String simulationControlAddress;
    private int simulationControlPort;

    private String simulationRobotControlBlueAddress;
    private int simulationRobotControlBluePort;

    private String simulationRobotControlYellowAddress;
    private int simulationRobotControlYellowPort;

    public String getCameraOutputAddress() {
        return cameraOutputAddress;
    }

    public void setCameraOutputAddress(String cameraOutputAddress) {
        this.cameraOutputAddress = cameraOutputAddress;
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
}
