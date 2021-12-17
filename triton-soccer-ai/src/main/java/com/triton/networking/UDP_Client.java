package com.triton.networking;

import java.io.IOException;
import java.net.*;
import java.util.function.Consumer;

public class UDP_Client extends Thread {
    protected static final int IN_BUF_SIZE = 9999;

    private final InetAddress serverAddress;
    private final int serverPort;
    private final DatagramSocket socket;
    private final Consumer<DatagramPacket> packetConsumer;

    private final byte[] inBuf = new byte[IN_BUF_SIZE];

    public UDP_Client(String serverAddress, int serverPort, Consumer<DatagramPacket> packetConsumer) throws UnknownHostException, SocketException {
        super();
        this.serverAddress = InetAddress.getByName(serverAddress);
        this.serverPort = serverPort;
        this.socket = new DatagramSocket();
        this.packetConsumer = packetConsumer;
    }

    @Override
    public void run() {
        super.run();

        while (true)
            receive();
    }

    private void receive() {
        if (packetConsumer == null) return;

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

    public void send(byte[] outBytes) {
        DatagramPacket packet = new DatagramPacket(outBytes, outBytes.length, serverAddress, serverPort);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
