# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: to_triton_sim.proto

import sys
_b=sys.version_info[0]<3 and (lambda x:x) or (lambda x:x.encode('latin1'))
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()




DESCRIPTOR = _descriptor.FileDescriptor(
  name='to_triton_sim.proto',
  package='proto',
  syntax='proto3',
  serialized_options=None,
  serialized_pb=_b('\n\x13to_triton_sim.proto\x12\x05proto\"\x1d\n\x0eToTritonSimMsg\x12\x0b\n\x03msg\x18\x01 \x01(\tb\x06proto3')
)




_TOTRITONSIMMSG = _descriptor.Descriptor(
  name='ToTritonSimMsg',
  full_name='proto.ToTritonSimMsg',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='msg', full_name='proto.ToTritonSimMsg.msg', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=30,
  serialized_end=59,
)

DESCRIPTOR.message_types_by_name['ToTritonSimMsg'] = _TOTRITONSIMMSG
_sym_db.RegisterFileDescriptor(DESCRIPTOR)

ToTritonSimMsg = _reflection.GeneratedProtocolMessageType('ToTritonSimMsg', (_message.Message,), dict(
  DESCRIPTOR = _TOTRITONSIMMSG,
  __module__ = 'to_triton_sim_pb2'
  # @@protoc_insertion_point(class_scope:proto.ToTritonSimMsg)
  ))
_sym_db.RegisterMessage(ToTritonSimMsg)


# @@protoc_insertion_point(module_scope)
