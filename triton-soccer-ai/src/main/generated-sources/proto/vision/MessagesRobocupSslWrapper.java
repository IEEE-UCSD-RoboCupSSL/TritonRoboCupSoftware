// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: messages_robocup_ssl_wrapper.proto

package proto.vision;

public final class MessagesRobocupSslWrapper {
  private MessagesRobocupSslWrapper() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface SSL_WrapperPacketOrBuilder extends
      // @@protoc_insertion_point(interface_extends:proto.vision.SSL_WrapperPacket)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>optional .proto.vision.SSL_DetectionFrame detection = 1;</code>
     */
    boolean hasDetection();
    /**
     * <code>optional .proto.vision.SSL_DetectionFrame detection = 1;</code>
     */
    proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame getDetection();
    /**
     * <code>optional .proto.vision.SSL_DetectionFrame detection = 1;</code>
     */
    proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrameOrBuilder getDetectionOrBuilder();

    /**
     * <code>optional .proto.vision.SSL_GeometryData geometry = 2;</code>
     */
    boolean hasGeometry();
    /**
     * <code>optional .proto.vision.SSL_GeometryData geometry = 2;</code>
     */
    proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData getGeometry();
    /**
     * <code>optional .proto.vision.SSL_GeometryData geometry = 2;</code>
     */
    proto.vision.MessagesRobocupSslGeometry.SSL_GeometryDataOrBuilder getGeometryOrBuilder();
  }
  /**
   * Protobuf type {@code proto.vision.SSL_WrapperPacket}
   */
  public  static final class SSL_WrapperPacket extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:proto.vision.SSL_WrapperPacket)
      SSL_WrapperPacketOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use SSL_WrapperPacket.newBuilder() to construct.
    private SSL_WrapperPacket(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private SSL_WrapperPacket() {
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private SSL_WrapperPacket(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 10: {
              proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame.Builder subBuilder = null;
              if (((bitField0_ & 0x00000001) == 0x00000001)) {
                subBuilder = detection_.toBuilder();
              }
              detection_ = input.readMessage(proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame.PARSER, extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(detection_);
                detection_ = subBuilder.buildPartial();
              }
              bitField0_ |= 0x00000001;
              break;
            }
            case 18: {
              proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData.Builder subBuilder = null;
              if (((bitField0_ & 0x00000002) == 0x00000002)) {
                subBuilder = geometry_.toBuilder();
              }
              geometry_ = input.readMessage(proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData.PARSER, extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(geometry_);
                geometry_ = subBuilder.buildPartial();
              }
              bitField0_ |= 0x00000002;
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return proto.vision.MessagesRobocupSslWrapper.internal_static_proto_vision_SSL_WrapperPacket_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return proto.vision.MessagesRobocupSslWrapper.internal_static_proto_vision_SSL_WrapperPacket_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket.class, proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket.Builder.class);
    }

    private int bitField0_;
    public static final int DETECTION_FIELD_NUMBER = 1;
    private proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame detection_;
    /**
     * <code>optional .proto.vision.SSL_DetectionFrame detection = 1;</code>
     */
    public boolean hasDetection() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>optional .proto.vision.SSL_DetectionFrame detection = 1;</code>
     */
    public proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame getDetection() {
      return detection_ == null ? proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame.getDefaultInstance() : detection_;
    }
    /**
     * <code>optional .proto.vision.SSL_DetectionFrame detection = 1;</code>
     */
    public proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrameOrBuilder getDetectionOrBuilder() {
      return detection_ == null ? proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame.getDefaultInstance() : detection_;
    }

    public static final int GEOMETRY_FIELD_NUMBER = 2;
    private proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData geometry_;
    /**
     * <code>optional .proto.vision.SSL_GeometryData geometry = 2;</code>
     */
    public boolean hasGeometry() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>optional .proto.vision.SSL_GeometryData geometry = 2;</code>
     */
    public proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData getGeometry() {
      return geometry_ == null ? proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData.getDefaultInstance() : geometry_;
    }
    /**
     * <code>optional .proto.vision.SSL_GeometryData geometry = 2;</code>
     */
    public proto.vision.MessagesRobocupSslGeometry.SSL_GeometryDataOrBuilder getGeometryOrBuilder() {
      return geometry_ == null ? proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData.getDefaultInstance() : geometry_;
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (hasDetection()) {
        if (!getDetection().isInitialized()) {
          memoizedIsInitialized = 0;
          return false;
        }
      }
      if (hasGeometry()) {
        if (!getGeometry().isInitialized()) {
          memoizedIsInitialized = 0;
          return false;
        }
      }
      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeMessage(1, getDetection());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeMessage(2, getGeometry());
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(1, getDetection());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, getGeometry());
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket)) {
        return super.equals(obj);
      }
      proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket other = (proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket) obj;

      boolean result = true;
      result = result && (hasDetection() == other.hasDetection());
      if (hasDetection()) {
        result = result && getDetection()
            .equals(other.getDetection());
      }
      result = result && (hasGeometry() == other.hasGeometry());
      if (hasGeometry()) {
        result = result && getGeometry()
            .equals(other.getGeometry());
      }
      result = result && unknownFields.equals(other.unknownFields);
      return result;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (hasDetection()) {
        hash = (37 * hash) + DETECTION_FIELD_NUMBER;
        hash = (53 * hash) + getDetection().hashCode();
      }
      if (hasGeometry()) {
        hash = (37 * hash) + GEOMETRY_FIELD_NUMBER;
        hash = (53 * hash) + getGeometry().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code proto.vision.SSL_WrapperPacket}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:proto.vision.SSL_WrapperPacket)
        proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacketOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return proto.vision.MessagesRobocupSslWrapper.internal_static_proto_vision_SSL_WrapperPacket_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return proto.vision.MessagesRobocupSslWrapper.internal_static_proto_vision_SSL_WrapperPacket_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket.class, proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket.Builder.class);
      }

      // Construct using proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
          getDetectionFieldBuilder();
          getGeometryFieldBuilder();
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        if (detectionBuilder_ == null) {
          detection_ = null;
        } else {
          detectionBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000001);
        if (geometryBuilder_ == null) {
          geometry_ = null;
        } else {
          geometryBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return proto.vision.MessagesRobocupSslWrapper.internal_static_proto_vision_SSL_WrapperPacket_descriptor;
      }

      @java.lang.Override
      public proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket getDefaultInstanceForType() {
        return proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket.getDefaultInstance();
      }

      @java.lang.Override
      public proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket build() {
        proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket buildPartial() {
        proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket result = new proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        if (detectionBuilder_ == null) {
          result.detection_ = detection_;
        } else {
          result.detection_ = detectionBuilder_.build();
        }
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        if (geometryBuilder_ == null) {
          result.geometry_ = geometry_;
        } else {
          result.geometry_ = geometryBuilder_.build();
        }
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return (Builder) super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return (Builder) super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return (Builder) super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return (Builder) super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return (Builder) super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return (Builder) super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket) {
          return mergeFrom((proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket other) {
        if (other == proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket.getDefaultInstance()) return this;
        if (other.hasDetection()) {
          mergeDetection(other.getDetection());
        }
        if (other.hasGeometry()) {
          mergeGeometry(other.getGeometry());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        if (hasDetection()) {
          if (!getDetection().isInitialized()) {
            return false;
          }
        }
        if (hasGeometry()) {
          if (!getGeometry().isInitialized()) {
            return false;
          }
        }
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame detection_ = null;
      private com.google.protobuf.SingleFieldBuilderV3<
          proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame, proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame.Builder, proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrameOrBuilder> detectionBuilder_;
      /**
       * <code>optional .proto.vision.SSL_DetectionFrame detection = 1;</code>
       */
      public boolean hasDetection() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>optional .proto.vision.SSL_DetectionFrame detection = 1;</code>
       */
      public proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame getDetection() {
        if (detectionBuilder_ == null) {
          return detection_ == null ? proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame.getDefaultInstance() : detection_;
        } else {
          return detectionBuilder_.getMessage();
        }
      }
      /**
       * <code>optional .proto.vision.SSL_DetectionFrame detection = 1;</code>
       */
      public Builder setDetection(proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame value) {
        if (detectionBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          detection_ = value;
          onChanged();
        } else {
          detectionBuilder_.setMessage(value);
        }
        bitField0_ |= 0x00000001;
        return this;
      }
      /**
       * <code>optional .proto.vision.SSL_DetectionFrame detection = 1;</code>
       */
      public Builder setDetection(
          proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame.Builder builderForValue) {
        if (detectionBuilder_ == null) {
          detection_ = builderForValue.build();
          onChanged();
        } else {
          detectionBuilder_.setMessage(builderForValue.build());
        }
        bitField0_ |= 0x00000001;
        return this;
      }
      /**
       * <code>optional .proto.vision.SSL_DetectionFrame detection = 1;</code>
       */
      public Builder mergeDetection(proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame value) {
        if (detectionBuilder_ == null) {
          if (((bitField0_ & 0x00000001) == 0x00000001) &&
              detection_ != null &&
              detection_ != proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame.getDefaultInstance()) {
            detection_ =
              proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame.newBuilder(detection_).mergeFrom(value).buildPartial();
          } else {
            detection_ = value;
          }
          onChanged();
        } else {
          detectionBuilder_.mergeFrom(value);
        }
        bitField0_ |= 0x00000001;
        return this;
      }
      /**
       * <code>optional .proto.vision.SSL_DetectionFrame detection = 1;</code>
       */
      public Builder clearDetection() {
        if (detectionBuilder_ == null) {
          detection_ = null;
          onChanged();
        } else {
          detectionBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000001);
        return this;
      }
      /**
       * <code>optional .proto.vision.SSL_DetectionFrame detection = 1;</code>
       */
      public proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame.Builder getDetectionBuilder() {
        bitField0_ |= 0x00000001;
        onChanged();
        return getDetectionFieldBuilder().getBuilder();
      }
      /**
       * <code>optional .proto.vision.SSL_DetectionFrame detection = 1;</code>
       */
      public proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrameOrBuilder getDetectionOrBuilder() {
        if (detectionBuilder_ != null) {
          return detectionBuilder_.getMessageOrBuilder();
        } else {
          return detection_ == null ?
              proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame.getDefaultInstance() : detection_;
        }
      }
      /**
       * <code>optional .proto.vision.SSL_DetectionFrame detection = 1;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame, proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame.Builder, proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrameOrBuilder> 
          getDetectionFieldBuilder() {
        if (detectionBuilder_ == null) {
          detectionBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame, proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame.Builder, proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrameOrBuilder>(
                  getDetection(),
                  getParentForChildren(),
                  isClean());
          detection_ = null;
        }
        return detectionBuilder_;
      }

      private proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData geometry_ = null;
      private com.google.protobuf.SingleFieldBuilderV3<
          proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData, proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData.Builder, proto.vision.MessagesRobocupSslGeometry.SSL_GeometryDataOrBuilder> geometryBuilder_;
      /**
       * <code>optional .proto.vision.SSL_GeometryData geometry = 2;</code>
       */
      public boolean hasGeometry() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>optional .proto.vision.SSL_GeometryData geometry = 2;</code>
       */
      public proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData getGeometry() {
        if (geometryBuilder_ == null) {
          return geometry_ == null ? proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData.getDefaultInstance() : geometry_;
        } else {
          return geometryBuilder_.getMessage();
        }
      }
      /**
       * <code>optional .proto.vision.SSL_GeometryData geometry = 2;</code>
       */
      public Builder setGeometry(proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData value) {
        if (geometryBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          geometry_ = value;
          onChanged();
        } else {
          geometryBuilder_.setMessage(value);
        }
        bitField0_ |= 0x00000002;
        return this;
      }
      /**
       * <code>optional .proto.vision.SSL_GeometryData geometry = 2;</code>
       */
      public Builder setGeometry(
          proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData.Builder builderForValue) {
        if (geometryBuilder_ == null) {
          geometry_ = builderForValue.build();
          onChanged();
        } else {
          geometryBuilder_.setMessage(builderForValue.build());
        }
        bitField0_ |= 0x00000002;
        return this;
      }
      /**
       * <code>optional .proto.vision.SSL_GeometryData geometry = 2;</code>
       */
      public Builder mergeGeometry(proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData value) {
        if (geometryBuilder_ == null) {
          if (((bitField0_ & 0x00000002) == 0x00000002) &&
              geometry_ != null &&
              geometry_ != proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData.getDefaultInstance()) {
            geometry_ =
              proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData.newBuilder(geometry_).mergeFrom(value).buildPartial();
          } else {
            geometry_ = value;
          }
          onChanged();
        } else {
          geometryBuilder_.mergeFrom(value);
        }
        bitField0_ |= 0x00000002;
        return this;
      }
      /**
       * <code>optional .proto.vision.SSL_GeometryData geometry = 2;</code>
       */
      public Builder clearGeometry() {
        if (geometryBuilder_ == null) {
          geometry_ = null;
          onChanged();
        } else {
          geometryBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }
      /**
       * <code>optional .proto.vision.SSL_GeometryData geometry = 2;</code>
       */
      public proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData.Builder getGeometryBuilder() {
        bitField0_ |= 0x00000002;
        onChanged();
        return getGeometryFieldBuilder().getBuilder();
      }
      /**
       * <code>optional .proto.vision.SSL_GeometryData geometry = 2;</code>
       */
      public proto.vision.MessagesRobocupSslGeometry.SSL_GeometryDataOrBuilder getGeometryOrBuilder() {
        if (geometryBuilder_ != null) {
          return geometryBuilder_.getMessageOrBuilder();
        } else {
          return geometry_ == null ?
              proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData.getDefaultInstance() : geometry_;
        }
      }
      /**
       * <code>optional .proto.vision.SSL_GeometryData geometry = 2;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData, proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData.Builder, proto.vision.MessagesRobocupSslGeometry.SSL_GeometryDataOrBuilder> 
          getGeometryFieldBuilder() {
        if (geometryBuilder_ == null) {
          geometryBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData, proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData.Builder, proto.vision.MessagesRobocupSslGeometry.SSL_GeometryDataOrBuilder>(
                  getGeometry(),
                  getParentForChildren(),
                  isClean());
          geometry_ = null;
        }
        return geometryBuilder_;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:proto.vision.SSL_WrapperPacket)
    }

    // @@protoc_insertion_point(class_scope:proto.vision.SSL_WrapperPacket)
    private static final proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket();
    }

    public static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    @java.lang.Deprecated public static final com.google.protobuf.Parser<SSL_WrapperPacket>
        PARSER = new com.google.protobuf.AbstractParser<SSL_WrapperPacket>() {
      @java.lang.Override
      public SSL_WrapperPacket parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new SSL_WrapperPacket(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<SSL_WrapperPacket> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<SSL_WrapperPacket> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_proto_vision_SSL_WrapperPacket_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_proto_vision_SSL_WrapperPacket_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\"messages_robocup_ssl_wrapper.proto\022\014pr" +
      "oto.vision\032$messages_robocup_ssl_detecti" +
      "on.proto\032#messages_robocup_ssl_geometry." +
      "proto\"z\n\021SSL_WrapperPacket\0223\n\tdetection\030" +
      "\001 \001(\0132 .proto.vision.SSL_DetectionFrame\022" +
      "0\n\010geometry\030\002 \001(\0132\036.proto.vision.SSL_Geo" +
      "metryData"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          proto.vision.MessagesRobocupSslDetection.getDescriptor(),
          proto.vision.MessagesRobocupSslGeometry.getDescriptor(),
        }, assigner);
    internal_static_proto_vision_SSL_WrapperPacket_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_proto_vision_SSL_WrapperPacket_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_proto_vision_SSL_WrapperPacket_descriptor,
        new java.lang.String[] { "Detection", "Geometry", });
    proto.vision.MessagesRobocupSslDetection.getDescriptor();
    proto.vision.MessagesRobocupSslGeometry.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
