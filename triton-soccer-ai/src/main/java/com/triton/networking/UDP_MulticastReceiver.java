package com.triton.networking;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.*;
import java.util.function.Consumer;

public class UDP_MulticastReceiver extends Thread {
    private static final String NETWORK_INTERFACE = "bge0";
    private static final int PACKET_BUFFER_SIZE = 9999;

    private final MulticastSocket socket;
    private final byte[] buf = new byte[PACKET_BUFFER_SIZE];
    private final Consumer<byte[]> callbackPacket;

    public UDP_MulticastReceiver(String address, int port, Consumer<byte[]> callbackPacket) throws IOException {
        socket = new MulticastSocket(port);
        InetAddress multicastAddress = InetAddress.getByName(address);
        InetSocketAddress group = new InetSocketAddress(multicastAddress, port);
        NetworkInterface networkInterface = NetworkInterface.getByName(NETWORK_INTERFACE);
        socket.joinGroup(group, networkInterface);
        this.callbackPacket = callbackPacket;
    }

    @Override
    public void run() {
        while (true) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            try {
                socket.receive(packet);
                ByteArrayInputStream stream = new ByteArrayInputStream(packet.getData(),
                        packet.getOffset(),
                        packet.getLength());
                byte[] bytes = stream.readAllBytes();
                stream.close();
                callbackPacket.accept(bytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}