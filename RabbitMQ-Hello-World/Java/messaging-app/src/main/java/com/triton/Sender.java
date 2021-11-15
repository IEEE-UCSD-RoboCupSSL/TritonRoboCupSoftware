package com.triton;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

public class Sender {
    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            while (true) {
                String message = "The current time is: " + LocalDateTime.now();
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
                System.out.printf(" [x] Sent '%s'\n", message);
                Thread.sleep(1000);
            }
        }
    }
}
