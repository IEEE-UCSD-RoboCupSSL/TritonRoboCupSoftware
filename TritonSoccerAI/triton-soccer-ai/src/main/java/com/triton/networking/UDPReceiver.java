package com.triton.networking;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

public class UDPReceiver extends Thread {
    private DatagramSocket socket;
    private byte[] buf = new byte[256];

    private LinkedBlockingQueue<byte[]> msgQueue;

    public UDPReceiver(int port) throws SocketException, UnknownHostException {
        socket = new DatagramSocket(port);
        msgQueue = new LinkedBlockingQueue<byte[]>();
    }

    @Override
    public void run() {
        while (true) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgQueue.put(packet.getData());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] pollBytes() {
        return msgQueue.poll();
    }
}