package com.triton.config;

public class NetworkConfig {
    private int cameraInputPort;
    private String cameraInputMulticastAddress;

    public int getCameraInputPort() {
        return cameraInputPort;
    }

    public void setCameraInputPort(int cameraInputPort) {
        this.cameraInputPort = cameraInputPort;
    }

    public String getCameraInputMulticastAddress() {
        return cameraInputMulticastAddress;
    }

    public void setCameraInputMulticastAddress(String cameraInputMulticastAddress) {
        this.cameraInputMulticastAddress = cameraInputMulticastAddress;
    }
}
