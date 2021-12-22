package com.triton.modules;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.triton.messaging.Exchange;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.SimpleSerialize.simpleSerialize;

public abstract class Module extends Thread {
    private static final String CONNECTION_FACTORY_HOST = "localhost";
    private static final String EXCHANGE_TYPE = "fanout";

    private Channel channel;

    public Module() throws IOException, TimeoutException {
        setupChannel();
        loadConfig();
        prepare();
        declareExchanges();
    }

    private void setupChannel() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(CONNECTION_FACTORY_HOST);
        Connection connection = factory.newConnection();
        channel = connection.createChannel();
    }

    protected void loadConfig() throws IOException {
    }

    protected void prepare() {
    }

    /**
     * Override to declare exchanges.
     *
     * @throws IOException
     * @throws TimeoutException
     */
    protected void declareExchanges() throws IOException, TimeoutException {
    }

    /**
     * Declares an exchange to publish to
     *
     * @param exchange the exchange
     * @throws IOException
     */
    protected void declarePublish(Exchange exchange) throws IOException {
        channel.exchangeDeclare(exchange.name(), EXCHANGE_TYPE);
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
        channel.exchangeDeclare(exchange.name(), EXCHANGE_TYPE);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, exchange.name(), "");
        channel.basicConsume(queueName, true, callback, consumerTag -> {
        });
    }

    /**
     * Publishes to an exchange
     *
     * @param exchange the exchange to publish to
     * @param object   the object to send
     * @throws IOException
     */
    protected void publish(Exchange exchange, Object object) throws IOException {
        channel.basicPublish(exchange.name(), "", null, simpleSerialize(object));
    }
}
