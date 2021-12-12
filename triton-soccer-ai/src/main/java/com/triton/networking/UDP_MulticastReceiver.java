package com.triton.networking;

import java.io.IOException;
import java.net.*;
import java.util.function.Consumer;

public class UDP_MulticastReceiver extends Thread {
    private static final String NETWORK_INTERFACE = "bge0";
    private static final int PACKET_BUFFER_SIZE = 9999;

    private final MulticastSocket socket;
    private final byte[] buf = new byte[PACKET_BUFFER_SIZE];
    Consumer<DatagramPacket> consumer;

    public UDP_MulticastReceiver(int port, String multicastAddressName, Consumer<DatagramPacket> consumer) throws IOException {
        socket = new MulticastSocket(port);
        InetAddress multicastAddress = InetAddress.getByName(multicastAddressName);
        InetSocketAddress group = new InetSocketAddress(multicastAddress, port);
        NetworkInterface networkInterface = NetworkInterface.getByName(NETWORK_INTERFACE);
        socket.joinGroup(group, networkInterface);
        this.consumer = consumer;
    }

    @Override
    public void run() {
        while (true) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            try {
                socket.receive(packet);
                consumer.accept(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}