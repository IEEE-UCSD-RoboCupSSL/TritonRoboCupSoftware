package com.triton.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.function.Consumer;

public class UDP_Server extends Thread {
    protected static final int IN_BUF_SIZE = 9999;

    private final DatagramSocket socket;
    private final Consumer<DatagramPacket> packetConsumer;

    private final byte[] inBuf = new byte[IN_BUF_SIZE];

    public UDP_Server(int serverPort, Consumer<DatagramPacket> packetConsumer) throws SocketException {
        super();
        this.socket = new DatagramSocket(serverPort);
        this.packetConsumer = packetConsumer;
    }

    @Override
    public void run() {
        super.run();

        while (true)
            receive();
    }

    private void receive() {
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

    public void send(byte[] outBytes, InetAddress clientAddress, int clientPort) {
        DatagramPacket packet = new DatagramPacket(outBytes, outBytes.length, clientAddress, clientPort);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
