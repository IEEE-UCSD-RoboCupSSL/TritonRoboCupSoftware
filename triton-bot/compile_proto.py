import os
import subprocess

proto_src_dir = "src/main/resources/proto"
proto_dst_dir = "src/main/generated-sources/proto"
proto_files = os.listdir(proto_src_dir)

for proto_file in proto_files:
    subprocess.run(["protoc", "-I=" + proto_src_dir, "--python_out=" + proto_dst_dir, proto_file])