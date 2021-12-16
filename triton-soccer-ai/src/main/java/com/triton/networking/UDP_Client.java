package com.triton.networking;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

public class UDP_Client extends Thread {
    private final InetAddress serverAddress;
    private final int serverPort;
    private final DatagramSocket socket;

    private final LinkedBlockingQueue<byte[]> sendQueue;

    public UDP_Client(String serverAddress, int serverPort) throws UnknownHostException, SocketException {
        super();
        this.serverAddress = InetAddress.getByName(serverAddress);
        this.serverPort = serverPort;
        this.socket = new DatagramSocket();
        sendQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            sendPacket();
        }
    }

    private void sendPacket() {
        byte[] outBytes = sendQueue.poll();
        if (outBytes == null) return;

        DatagramPacket packet = new DatagramPacket(outBytes, outBytes.length, serverAddress, serverPort);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addSend(byte[] outBytes) {
        try {
            sendQueue.put(outBytes);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
