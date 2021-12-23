package com.triton.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.function.Consumer;
import java.util.function.Function;

public class UDP_Server extends Thread {
    protected static final int BUF_SIZE = 9999;

    private final int serverPort;
    private final Function<DatagramPacket, byte[]> callbackPacket;

    private final DatagramSocket socket;

    private InetAddress clientAddress;
    private int clientPort;

    public UDP_Server(int serverPort, Function<DatagramPacket, byte[]> callbackPacket) throws SocketException {
        super();
        this.serverPort = serverPort;
        this.callbackPacket = callbackPacket;

        this.socket = new DatagramSocket(serverPort);
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            send(receive());
        }
    }

    private byte[] receive() {
        // TODO: CONSIDER WHEN MESSAGE IS NOT RECEIVED
        byte[] buf = new byte[BUF_SIZE];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        clientAddress = packet.getAddress();
        clientPort = packet.getPort();

        // TODO: CONSIDER CHANGING TO CONSUME BYTES INSTEAD OF PACKETS
        try {
            return callbackPacket.apply(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void send(byte[] bytes) {
        if (bytes == null || clientAddress == null) return;

        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, clientAddress, clientPort);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
