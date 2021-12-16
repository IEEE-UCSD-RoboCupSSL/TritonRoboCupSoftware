package com.triton.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.function.Consumer;

public class UDP_Server extends Thread {
    protected static final int IN_BUF_SIZE = 9999;

    private final int serverPort;
    private final DatagramSocket socket;
    private final Consumer<DatagramPacket> packetConsumer;

    private final byte[] inBuf;

    public UDP_Server(int serverPort, Consumer<DatagramPacket> packetConsumer) throws SocketException {
        super();
        this.serverPort = serverPort;
        this.socket = new DatagramSocket(serverPort);
        this.packetConsumer = packetConsumer;
        inBuf = new byte[IN_BUF_SIZE];
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            receivePacket();
        }
    }

    private void receivePacket() {
        DatagramPacket packet = new DatagramPacket(inBuf, inBuf.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            packetConsumer.accept(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
