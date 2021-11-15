import socket

IP = 'localhost'
PORT = 5678
BUFFER_SIZE = 1024

socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
socket.bind((IP, PORT))

while True:
    data, addr = socket.recvfrom(BUFFER_SIZE)
    msg = data.decode('utf-8')
    print("[F] Received from UDP client:\n" + msg)
