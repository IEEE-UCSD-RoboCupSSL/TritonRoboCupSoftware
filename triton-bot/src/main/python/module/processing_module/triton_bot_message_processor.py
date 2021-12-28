from pickle import loads
from config.config_path import ConfigPath
from config.config_reader import read_config
from generated_sources.proto.triton_bot_communication_pb2 import TritonBotMessage
from messaging.exchange import Exchange
from module.module import Module


class TritonBotMessageProcessor(Module):
    def __init__(self):
        super().__init__()

    def load_config(self):
        super().load_config()
        self.network_config = read_config(
            ConfigPath.NETWORK_CONFIG)

    def prepare(self):
        super().prepare()

    def declare_exchanges(self):
        super().declare_exchanges()
        self.declare_consume(Exchange.TB_MESSAGE, self.callback_message)
        self.declare_publish(Exchange.TB_VISION)
        self.declare_publish(Exchange.TB_GLOBAL_COMMAND)
        self.declare_publish(Exchange.TB_LOCAL_COMMAND)
        self.declare_publish(Exchange.TB_WHEEL_COMMAND)

    def run(self):
        super().run()
        self.consume()

    def callback_message(self, ch, method, properties, body):        
        message = TritonBotMessage()
        message.ParseFromString(body)
        
        if (message.HasField('vision')):
            self.publish(exchange=Exchange.TB_VISION, object=message.vision)

        elif (message.HasField('command')):
            exchange = Exchange.TB_WHEEL_COMMAND
            if (message.command.HasField('move_command')):
                if (message.command.move_command.HasField('global_velocity')):
                    exchange = Exchange.TB_GLOBAL_COMMAND
                elif (message.command.move_command.HasField('local_velocity')):
                    exchange = Exchange.TB_LOCAL_COMMAND
            self.publish(exchange=exchange, object=message.command)