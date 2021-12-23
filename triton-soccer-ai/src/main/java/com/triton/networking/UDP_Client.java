package com.triton.networking;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class UDP_Client extends Thread {
    protected static final int BUF_SIZE = 9999;

    private final InetAddress serverAddress;
    private final int serverPort;
    private final Consumer<DatagramPacket> callbackPacket;

    private final DatagramSocket socket;
    private final BlockingQueue<byte[]> sendQueue;

    public UDP_Client(String serverAddress, int serverPort, Consumer<DatagramPacket> callbackPacket) throws UnknownHostException, SocketException {
        super();
        this.serverAddress = InetAddress.getByName(serverAddress);
        this.serverPort = serverPort;
        this.callbackPacket = callbackPacket;

        this.socket = new DatagramSocket();
        sendQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            receive(send());
        }
    }

    private boolean send() {
        byte[] bytes = sendQueue.poll();
        if (bytes == null)
            return false;

        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, serverAddress, serverPort);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void receive(boolean receive) {
        // TODO: CONSIDER WHEN MESSAGE IS NOT RECEIVED
        if (!receive || callbackPacket == null) return;

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

    public void addSend(byte[] bytes) {
        sendQueue.add(bytes);
    }
}
