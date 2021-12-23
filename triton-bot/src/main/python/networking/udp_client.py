import socket
from threading import Thread


class UDP_Client(Thread):
    BUF_SIZE = 9999

    def __init__(self, serverAddress, serverPort, callbackPacket):
        super().__init__()
        self.serverAddress = serverAddress
        self.serverPort = serverPort
        self.callbackPacket = callbackPacket

        self.socket = socket.socket(family=socket.AF_INET, type=socket.SOCK_DGRAM)

    def run(self):
        super().run()
        while (True):
            self.receive()

    def receive(self):
        if (self.callbackPacket == None):
            return
        packet = self.socket.recvfrom(UDP_Client.BUF_SIZE)
        self.callbackPacket(packet=packet)

    def send(self, bytes):
        self.socket.sendto(bytes, (self.serverAddress, self.serverPort))
