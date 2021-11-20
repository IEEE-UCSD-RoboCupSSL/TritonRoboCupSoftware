package com.triton.module;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class AI_B2 {
    private static final String EXCHANGE_A = "a";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_A, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_A, "");

        System.out.println("[AI_B2] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (s, delivery) -> {
            String inMsg = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("[AI_B2] Received with RabbitMQ:\n" + inMsg);
        };

        CancelCallback cancelCallback = s -> System.out.println("[AI_B2] Callback Cancelled");

        channel.basicConsume(queueName, true, deliverCallback, cancelCallback);
    }
}
