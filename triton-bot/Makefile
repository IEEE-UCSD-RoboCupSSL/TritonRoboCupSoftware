proto_src_dir = ./proto_src
proto_dst_dir = ./proto_dst
proto_files = to_triton_bot.proto to_triton_sim.proto

proto:
	protoc -I=${proto_src_dir} --python_out=${proto_dst_dir} ${proto_files}