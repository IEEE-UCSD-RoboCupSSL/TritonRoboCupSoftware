package com.triton.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;

public class UDP_Receiver extends Thread {
    private static final int PACKET_BUFFER_SIZE = 9999;

    private final DatagramSocket socket;
    private final byte[] buf = new byte[PACKET_BUFFER_SIZE];
    private final LinkedBlockingQueue<DatagramPacket> msgQueue;

    public UDP_Receiver(int port) throws SocketException, UnknownHostException {
        socket = new DatagramSocket(port);
        msgQueue = new LinkedBlockingQueue<>();
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
                msgQueue.put(packet);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public DatagramPacket pollPacket() {
        return msgQueue.poll();
    }
}