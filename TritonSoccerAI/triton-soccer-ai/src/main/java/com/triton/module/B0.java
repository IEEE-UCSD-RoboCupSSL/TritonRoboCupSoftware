package com.triton.module;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class B0 {
    private static final String EXCHANGE_A = "a";
    private static final String EXCHANGE_B = "b";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_A, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_A, "");

        channel.exchangeDeclare(EXCHANGE_B, "fanout");

        DeliverCallback deliverCallback = (s, delivery) -> {
            String inMsg = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("[B0] Received with RabbitMQ:\n" + inMsg);

            String outMsg = inMsg + "B";
            channel.basicPublish(EXCHANGE_B, "", null, outMsg.getBytes(StandardCharsets.UTF_8));

            System.out.println("[A] Sent with RabbitMQ:\n" + outMsg);
        };

        CancelCallback cancelCallback = s -> System.out.println("[B0] Callback Cancelled");

        channel.basicConsume(queueName, true, deliverCallback, cancelCallback);
    }
}
