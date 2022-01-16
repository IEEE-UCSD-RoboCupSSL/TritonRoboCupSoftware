import pickle
from threading import Thread

from google.protobuf.message import Message
from constant.runtime_constants import RuntimeConstants
import pika


class Module(Thread):
    CONNECTION_FACTORY_HOST = 'localhost'
    EXCHANGE_TYPE = 'fanout'

    def __init__(self):
        super().__init__()
        self.setup_channel()
        self.load_config()
        self.prepare()
        self.declare_exchanges()

    def setup_channel(self):
        connection = pika.BlockingConnection(
            pika.ConnectionParameters(Module.CONNECTION_FACTORY_HOST))
        self.channel = connection.channel()

    def load_config(self):
        pass

    def prepare(self):
        pass

    def declare_exchanges(self):
        pass

    def declare_publish(self, exchange):
        self.channel.exchange_declare(
            exchange=exchange.name + str(RuntimeConstants.id), exchange_type=Module.EXCHANGE_TYPE)

    def declare_consume(self, exchange, callback):
        self.channel.exchange_declare(
            exchange=exchange.name + str(RuntimeConstants.id), exchange_type=Module.EXCHANGE_TYPE)

        queue_name = self.channel.queue_declare(
            queue='', exclusive=True).method.queue
        self.channel.queue_bind(exchange=exchange.name + str(RuntimeConstants.id), queue=queue_name)

        self.channel.basic_consume(
            queue=queue_name, on_message_callback=callback, auto_ack=True)

    def publish(self, exchange, object):
        if (isinstance(object, Message)):
            body = object.SerializeToString()
        else:
            body = pickle.dumps(object)

        self.channel.basic_publish(
            exchange=exchange.name + str(RuntimeConstants.id), routing_key='', body=body)
    
    def consume(self):
        self.channel.start_consuming()