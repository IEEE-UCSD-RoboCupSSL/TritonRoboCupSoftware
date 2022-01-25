package com.triton.module;

import com.rabbitmq.client.*;
import com.triton.messaging.Exchange;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.SimpleSerialize.simpleSerialize;

public abstract class Module {
    private static final String CONNECTION_FACTORY_HOST = "localhost";
    private static final String FANOUT = "fanout";

    private Channel publish_channel;
    private Channel consume_channel;

    public Module() {
        try {
            setupChannel();
            prepare();
            declareExchanges();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void setupChannel() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(CONNECTION_FACTORY_HOST);

        Connection publish_connection = factory.newConnection();
        publish_channel = publish_connection.createChannel();

        Connection consume_connection = factory.newConnection();
        consume_channel = consume_connection.createChannel();
    }

    protected void prepare() {
    }

    /**
     * Override to declare exchanges.
     */
    protected void declareExchanges() throws IOException {
    }

    /**
     * Declares an exchange to publish to
     *
     * @param exchange the exchange
     * @throws IOException
     */
    protected void declarePublish(Exchange exchange) throws IOException {
        publish_channel.exchangeDeclare(exchange.name(), FANOUT);
    }

    /**
     * Declares an exchange to consume from. The messageConsumer function will be called when an message is received
     * from the exchange.
     *
     * @param exchange the exchange to consume from
     * @param callback the function to call once a message is received
     * @throws IOException
     */
    protected void declareConsume(Exchange exchange, DeliverCallback callback) throws IOException {
        consume_channel.exchangeDeclare(exchange.name(), FANOUT);

        Map<String, Object> args = new HashMap<>();
        String queueName = consume_channel.queueDeclare("",
                false,
                false,
                false,
                args).getQueue();
        consume_channel.queueBind(queueName, exchange.name(), "");
        consume_channel.queuePurge(queueName);

        DeliverCallback wrappedCallback = (s, delivery) -> {
            try {
                callback.handle(s, delivery);
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
        publish(exchange, object, properties);
    }

    public void publish(Exchange exchange, Object object, AMQP.BasicProperties properties) {
        if (publish_channel.isOpen()) {
            try {
                publish_channel.basicPublish(exchange.name(), "", properties, simpleSerialize(object));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void shutdown() {
        try {
            consume_channel.close();
            publish_channel.close();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
