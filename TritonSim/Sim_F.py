import socket
from proto_dst import to_triton_sim_pb2

IP = 'localhost'
PORT = 5678
BUFFER_SIZE = 1024

socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
socket.bind((IP, PORT))

print("[Sim_F] Waiting for messages. To exit press CTRL+C")

while True:
    in_data, addr = socket.recvfrom(BUFFER_SIZE)
    in_proto = to_triton_sim_pb2.ToTritonSimMsg()
    in_proto.ParseFromString(in_data)
    in_msg = in_proto.msg
    print("[Sim_F] Received from UDP client:\n" + str(in_proto))