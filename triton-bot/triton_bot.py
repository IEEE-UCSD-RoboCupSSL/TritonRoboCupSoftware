import socket
from proto_dst import to_triton_bot_pb2
from proto_dst import to_triton_sim_pb2

RECEIVE_IP = 'localhost'
RECEIVE_PORT = 1234
BUFFER_SIZE = 1024

SEND_IP = 'localhost'
SEND_PORT = 5678

receive_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
receive_socket.bind((RECEIVE_IP, RECEIVE_PORT))

send_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

print("[Bot_E] Waiting for messages. To exit press CTRL+C")

while True:
    in_data, addr = receive_socket.recvfrom(BUFFER_SIZE)
    in_proto = to_triton_bot_pb2.ToTritonBotMsg()
    in_proto.ParseFromString(in_data)
    in_msg = in_proto.msg
    print("[Bot_E] Received from UDP client:\n" + str(in_proto))

    out_msg = in_msg + "E"
    out_proto = to_triton_sim_pb2.ToTritonSimMsg()
    out_proto.msg = out_msg
    out_data = out_proto.SerializeToString()

    send_socket.sendto(out_data, (SEND_IP, SEND_PORT))
    print("[Bot_E] Sent to UDP server:\n" + str(out_proto))