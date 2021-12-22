package com.triton.networking;

import java.io.IOException;
import java.net.*;
import java.util.function.Consumer;

public class UDP_Client extends Thread {
    protected static final int BUF_SIZE = 9999;

    private final InetAddress serverAddress;
    private final int serverPort;
    private final Consumer<DatagramPacket> callbackPacket;

    private final DatagramSocket socket;

    public UDP_Client(String serverAddress, int serverPort, Consumer<DatagramPacket> callbackPacket) throws UnknownHostException, SocketException {
        super();
        this.serverAddress = InetAddress.getByName(serverAddress);
        this.serverPort = serverPort;
        this.callbackPacket = callbackPacket;

        this.socket = new DatagramSocket();
    }

    @Override
    public void run() {
        super.run();

        while (true)
            receive();
    }

    private void receive() {
        if (callbackPacket == null) return;

        byte[] buf = new byte[BUF_SIZE];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            callbackPacket.accept(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] bytes) {
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, serverAddress, serverPort);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
