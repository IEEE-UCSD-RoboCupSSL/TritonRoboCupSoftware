import socket
from threading import Thread


class UDP_Server(Thread):
    LOCALHOST_ADDRESS = "127.0.0.1"
    BUF_SIZE = 9999

    def __init__(self, serverPort, callbackPacket):
        super().__init__()
        self.serverPort = serverPort
        self.callbackPacket = callbackPacket

        self.socket = socket.socket(family=socket.AF_INET, type=socket.SOCK_DGRAM)
        self.socket.bind((UDP_Server.LOCALHOST_ADDRESS, serverPort))

    def run(self):
        super().run()
        while (True):
            self.receive()

    def receive(self):
        if (self.callbackPacket == None):
            return

        packet = self.socket.recvfrom(UDP_Server.BUF_SIZE)
        self.callbackPacket(packet=packet)

    def send(self, bytes, clientAddress, clientPort):
        self.socket.sendto(bytes, (clientAddress, clientPort))
