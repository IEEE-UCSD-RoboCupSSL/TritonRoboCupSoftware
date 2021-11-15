package com.triton.module;

import com.rabbitmq.client.*;
import com.triton.networking.UDPSender;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class D {
    private static final String EXCHANGE_C = "c";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_C, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_C, "");

        UDPSender udpSender = new UDPSender("localhost", 1234);
        udpSender.start();

        System.out.println("[D] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (s, delivery) -> {
            String inMsg = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("[D] Received with RabbitMQ:\n" + inMsg);

            String outMsg = inMsg + "D";
            udpSender.putMsg(outMsg);

            System.out.println("[D] Sent to UDP server:\n" + outMsg);
        };

        CancelCallback cancelCallback = s -> System.out.println("[C] Callback Cancelled");

        channel.basicConsume(queueName, true, deliverCallback, cancelCallback);
    }
}
