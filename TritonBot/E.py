import socket

RECEIVE_IP = 'localhost'
RECEIVE_PORT = 1234
BUFFER_SIZE = 1024

SEND_IP = 'localhost'
SEND_PORT = 5678

receive_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
receive_socket.bind((RECEIVE_IP, RECEIVE_PORT))

send_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

while True:
    data, addr = receive_socket.recvfrom(BUFFER_SIZE)
    in_msg = data.decode('utf-8')
    print("[E] Received from UDP client:\n" + in_msg)

    out_msg = in_msg + "E"
    send_socket.sendto(out_msg.encode('utf-8'), (SEND_IP, SEND_PORT))
    print("[E] Sent to UDP server:\n" + out_msg)