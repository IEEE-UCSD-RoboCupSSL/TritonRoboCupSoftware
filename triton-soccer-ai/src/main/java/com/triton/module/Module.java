package com.triton.module;

import com.rabbitmq.client.*;
import com.triton.constant.RuntimeConstants;
import com.triton.messaging.Exchange;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.SimpleSerialize.simpleSerialize;

public abstract class Module extends Thread {
    private static final String CONNECTION_FACTORY_HOST = "localhost";
    private static final String FANOUT = "fanout";

    private ConnectionFactory factory;

    private Channel publish_channel;
    private Channel consume_channel;

    public Module() {
        try {
            setupChannel();
            prepare();
            declarePublishes();
            declareConsumes();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void setupChannel() throws IOException, TimeoutException {
        factory = new ConnectionFactory();
        factory.setHost(CONNECTION_FACTORY_HOST);
        factory.setRequestedHeartbeat(10);

        Connection publish_connection = factory.newConnection();
        publish_channel = publish_connection.createChannel();

        Connection consume_connection = factory.newConnection();
        consume_channel = consume_connection.createChannel();
    }

    protected void prepare() {
    }

    protected abstract void declarePublishes() throws IOException, TimeoutException;

    protected abstract void declareConsumes() throws IOException, TimeoutException;

    /**
     * Declares an exchange to publish to
     *
     * @param exchange the exchange
     * @throws IOException
     */
    public void declarePublish(Exchange exchange) throws IOException, TimeoutException {
        publish_channel.exchangeDeclare(exchange.name() + RuntimeConstants.team.name(), FANOUT);
    }

    /**
     * Declares an exchange to consume from. The messageConsumer function will be called when an message is received
     * from the exchange.
     *
     * @param exchange the exchange to consume from
     * @param callback the function to call once a message is received
     * @throws IOException
     */
    public void declareConsume(Exchange exchange, DeliverCallback callback) throws IOException, TimeoutException {
        consume_channel.exchangeDeclare(exchange.name() + RuntimeConstants.team.name(), FANOUT);

        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 1000);
        args.put("x-expires", 1000);
//        args.put("x-max-length", 1);
        String queueName = consume_channel.queueDeclare("",
                false,
                false,
                false,
                args).getQueue();
        consume_channel.queueBind(queueName, exchange.name() + RuntimeConstants.team.name(), "");
        consume_channel.queuePurge(queueName);

        DeliverCallback wrappedCallback = (s, delivery) -> {
            try {
                long timeDiff = new Date().getTime() - delivery.getProperties().getTimestamp().getTime();
                if (timeDiff > 2000) {
                    System.out.println(exchange);
                    System.out.println(this.getClass());
                } else {
                    callback.handle(s, delivery);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        consume_channel.basicConsume(queueName, true, wrappedCallback, consumerTag -> {
        });
    }

    /**
     * Publishes to an exchange
     *
     * @param exchange the exchange to publish to
     * @param object   the object to send
     * @throws IOException
     */
    public void publish(Exchange exchange, Object object) {
        Date date = new Date();
        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().timestamp(date).build();
        if (publish_channel.isOpen()) {
            try {
                publish_channel.basicPublish(exchange.name() + RuntimeConstants.team.name(), "", properties, simpleSerialize(object));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            consume_channel.close();
            publish_channel.close();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
