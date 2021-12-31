from math import degrees
from config.config_path import ConfigPath
from config.config_reader import read_config
from constant.runtime_constants import RuntimeConstants
from constant.team import Team
from generated_sources.proto.triton_bot_communication_pb2 import \
    TritonBotMessage
from messaging.exchange import Exchange
from module.module import Module
from networking.udp_server import UDP_Server


class AI_Interface(Module):
    def __init__(self):
        super().__init__()

    def load_config(self):
        super().load_config()
        self.network_config = read_config(
            ConfigPath.NETWORK_CONFIG)

    def prepare(self):
        super().prepare()
        self.init_received = False
        self.setup_client()

    def declare_exchanges(self):
        super().declare_exchanges()
        self.declare_publish(Exchange.TB_MESSAGE)

    def run(self):
        super().run()
        self.consume()

    def setup_client(self):
        if (RuntimeConstants.team == Team.YELLOW):
            server_port = self.network_config['tritonBotPortBaseYellow'] + \
                RuntimeConstants.id * self.network_config['tritonBotPortIncr']
        else:
            server_port = self.network_config['tritonBotPortBaseBlue'] + \
                RuntimeConstants.id * self.network_config['tritonBotPortIncr']

        self.server = UDP_Server(
            server_port=server_port, callback=self.callback_message)
        self.server.start()

    def callback_message(self, bytes):
        message = TritonBotMessage()
        message.ParseFromString(bytes)

        self.publish(exchange=Exchange.TB_MESSAGE, object=message)
        return None