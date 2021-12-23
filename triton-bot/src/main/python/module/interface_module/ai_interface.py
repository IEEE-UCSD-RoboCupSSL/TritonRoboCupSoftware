from config.config_path import ConfigPath
from config.config_reader import read_config
from generated_sources.proto.triton_bot_init_pb2 import Init
from constant.runtime_constants import RuntimeConstants
from generated_sources.proto.ssl_simulation_robot_control_pb2 import RobotCommand
from module.module import Module
from networking.udp_client import UDP_Client


class AI_Interface(Module):
    def __init__(self):
        super().__init__()

    def load_config(self):
        super().load_config()
        self.network_config = read_config(
            ConfigPath.NETWORK_CONFIG)

    def prepare(self):
        super().prepare()
        self.setup_client()

    def declare_exchanges(self):
        super().declare_exchanges()

    def setup_client(self):
        self.client = UDP_Client(
            serverAddress=self.network_config['aiAddress'],
            serverPort=self.network_config['aiTritonBotPort'],
            callbackPacket=self.callbackTritonBotCommand)
        self.client.start()

    def run(self):
        super().run()

        while (True):
            self.send_init()

    def send_init(self):
        init = Init() 
        init.id = RuntimeConstants.id
        self.client.send(init.SerializeToString())

    def callbackTritonBotCommand(self, packet):
        bytes = packet[0]
        print(packet[1])
        command = RobotCommand()
        command.ParseFromString(bytes)