package com.triton.networking;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

public class UDPReceiver<T> extends Thread {
    private DatagramSocket socket;
    private byte[] buf = new byte[256];

    private LinkedBlockingQueue<String> msgQueue;

    public UDPReceiver(int port) throws SocketException, UnknownHostException {
        socket = new DatagramSocket(port);
        msgQueue = new LinkedBlockingQueue<String>();
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

            String msg = new String(packet.getData(), 0, packet.getLength());
            try {
                msgQueue.put(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public String pollMsg() {
        return msgQueue.poll();
    }
}