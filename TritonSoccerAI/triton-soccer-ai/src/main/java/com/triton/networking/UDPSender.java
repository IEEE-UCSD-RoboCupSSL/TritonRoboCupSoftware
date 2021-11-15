package com.triton.networking;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

public class UDPSender<T> extends Thread {
    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private byte[] buf;

    private LinkedBlockingQueue<String> msgQueue;

    public UDPSender(String ip, int port) throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        address = InetAddress.getByName(ip);
        this.port = port;

        msgQueue = new LinkedBlockingQueue<String>();
    }

    public UDPSender(int port) throws SocketException, UnknownHostException {
        this("localhost", port);
    }

    @Override
    public void run() {
        while (true) {
            String msg = msgQueue.poll();
            if (msg == null)
                continue;

            buf = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void putMsg(String msg) {
        try {
            msgQueue.put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}