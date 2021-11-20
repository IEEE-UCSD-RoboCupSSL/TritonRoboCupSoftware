package com.triton.networking;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

public class UDPSender extends Thread {
    private DatagramSocket socket;
    private InetAddress address;
    private int port;

    private LinkedBlockingQueue<byte[]> msgQueue;

    public UDPSender(String ip, int port) throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        address = InetAddress.getByName(ip);
        this.port = port;

        msgQueue = new LinkedBlockingQueue<byte[]>();
    }

    public UDPSender(int port) throws SocketException, UnknownHostException {
        this("localhost", port);
    }

    @Override
    public void run() {
        while (true) {
            byte[] outBytes = msgQueue.poll();
            if (outBytes == null)
                continue;

            DatagramPacket packet = new DatagramPacket(outBytes, outBytes.length, address, port);
            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void putBytes(byte[] outBytes) {
        try {
            msgQueue.put(outBytes);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}