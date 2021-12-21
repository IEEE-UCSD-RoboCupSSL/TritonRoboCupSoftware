import pickle
import exchange


class Module:
    CONNECTION_FACTORY_HOST = 'localhost'
    EXCHANGE_TYPE = 'fanout'

    def __init__(self):
        self.setup_channel()
        self.load_config()
        self.prepare()
        self.declare_exchanges()

    def setup_channel(self):
        connection = pika.BlockingConnection(
            pika.ConnectionParameters(self.CONNECTION_FACTORY_HOST))
        self.channel = connection.channel()

    def load_config(self):
        return

    def prepare(self):
        return

    def declare_exchanges(self, exchange):
        return

    def declare_publish(self, exchange):
        self.channel.exchange_declare(
            exchange=exchange.name, exchange_type=self.EXCHANGE_TYPE)

    def declare_consume(self, exchange, consumer):
        self.channel.exchange_declare(
            exchange=exchange.name, exchange_type=self.EXCHANGE_TYPE)

        queue_name = self.channel.queue_declare(
            queue='', exclusive=True).method.queue
        self.channel.queue_bind(exchange=exchange.name, queue=queue_name)

        self.channel.basic_consume(
            queue=queue_name, on_message_callback=consumer, auto_ack=True)
        self.channel.start_consuming()

    def publish(self, exchange, object):
        self.channel.basic_publish(
            exchange=exchange.name, routing_key='', body=pickle.dumps(object))
