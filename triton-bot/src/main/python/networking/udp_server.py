import threading
import socket


class UDP_Server(threading.Thread):
    LOCALHOST_ADDRESS = "127.0.0.1"
    BUF_SIZE = 9999

    def __init__(self, serverPort, packetConsumer):
        super().__init__()
        self.serverPort = serverPort
        self.packetConsumer = packetConsumer

        self.socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.socket.bind((UDP_Server.LOCALHOST_ADDRESS, serverPort))

    def run(self):
        super().run()
        while (True):
            self.receive()

    def receive(self):
        if (self.packetConsumer == None):
            return

        packet = self.socket.recvfrom(UDP_Server.BUF_SIZE)
        self.packetConsumer(packet)
        

    def send(self, bytes, clientAddress, clientPort):
        self.socket.sendto(bytes, (clientAddress, clientPort))