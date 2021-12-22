import threading
import socket


class UDP_Client(threading.Thread):
    BUF_SIZE = 9999

    def __init__(self, serverAddress, serverPort, packetConsumer):
        super().__init__()
        self.serverAddress = serverAddress
        self.serverPort = serverPort
        self.packetConsumer = packetConsumer

        self.socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

    def run(self):
        super().run()
        while (True):
            self.receive()

    def receive(self):
        if (self.packetConsumer == None):
            return
        packet = self.socket.recvfrom(UDP_Client.BUF_SIZE)
        self.packetConsumer(packet)
        

    def send(self, bytes):
        self.socket.sendto(bytes, (self.serverAddress, self.serverPort))