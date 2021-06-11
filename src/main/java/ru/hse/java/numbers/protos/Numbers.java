// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: numbers.proto

package ru.hse.java.numbers.protos;

/**
 * Protobuf type {@code Numbers}
 */
public final class Numbers extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:Numbers)
    NumbersOrBuilder {
private static final long serialVersionUID = 0L;
  // Use Numbers.newBuilder() to construct.
  private Numbers(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private Numbers() {
    numbers_ = emptyIntList();
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new Numbers();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private Numbers(
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
          case 8: {
            bitField0_ |= 0x00000001;
            size_ = input.readInt32();
            break;
          }
          case 16: {
            if (!((mutable_bitField0_ & 0x00000002) != 0)) {
              numbers_ = newIntList();
              mutable_bitField0_ |= 0x00000002;
            }
            numbers_.addInt(input.readInt32());
            break;
          }
          case 18: {
            int length = input.readRawVarint32();
            int limit = input.pushLimit(length);
            if (!((mutable_bitField0_ & 0x00000002) != 0) && input.getBytesUntilLimit() > 0) {
              numbers_ = newIntList();
              mutable_bitField0_ |= 0x00000002;
            }
            while (input.getBytesUntilLimit() > 0) {
              numbers_.addInt(input.readInt32());
            }
            input.popLimit(limit);
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
      if (((mutable_bitField0_ & 0x00000002) != 0)) {
        numbers_.makeImmutable(); // C
      }
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return ru.hse.java.numbers.protos.NumbersProtos.internal_static_Numbers_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return ru.hse.java.numbers.protos.NumbersProtos.internal_static_Numbers_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            ru.hse.java.numbers.protos.Numbers.class, ru.hse.java.numbers.protos.Numbers.Builder.class);
  }

  private int bitField0_;
  public static final int SIZE_FIELD_NUMBER = 1;
  private int size_;
  /**
   * <code>required int32 size = 1;</code>
   * @return Whether the size field is set.
   */
  @java.lang.Override
  public boolean hasSize() {
    return ((bitField0_ & 0x00000001) != 0);
  }
  /**
   * <code>required int32 size = 1;</code>
   * @return The size.
   */
  @java.lang.Override
  public int getSize() {
    return size_;
  }

  public static final int NUMBERS_FIELD_NUMBER = 2;
  private com.google.protobuf.Internal.IntList numbers_;
  /**
   * <code>repeated int32 numbers = 2;</code>
   * @return A list containing the numbers.
   */
  @java.lang.Override
  public java.util.List<java.lang.Integer>
      getNumbersList() {
    return numbers_;
  }
  /**
   * <code>repeated int32 numbers = 2;</code>
   * @return The count of numbers.
   */
  public int getNumbersCount() {
    return numbers_.size();
  }
  /**
   * <code>repeated int32 numbers = 2;</code>
   * @param index The index of the element to return.
   * @return The numbers at the given index.
   */
  public int getNumbers(int index) {
    return numbers_.getInt(index);
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    if (!hasSize()) {
      memoizedIsInitialized = 0;
      return false;
    }
    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (((bitField0_ & 0x00000001) != 0)) {
      output.writeInt32(1, size_);
    }
    for (int i = 0; i < numbers_.size(); i++) {
      output.writeInt32(2, numbers_.getInt(i));
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (((bitField0_ & 0x00000001) != 0)) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(1, size_);
    }
    {
      int dataSize = 0;
      for (int i = 0; i < numbers_.size(); i++) {
        dataSize += com.google.protobuf.CodedOutputStream
          .computeInt32SizeNoTag(numbers_.getInt(i));
      }
      size += dataSize;
      size += 1 * getNumbersList().size();
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
    if (!(obj instanceof ru.hse.java.numbers.protos.Numbers)) {
      return super.equals(obj);
    }
    ru.hse.java.numbers.protos.Numbers other = (ru.hse.java.numbers.protos.Numbers) obj;

    if (hasSize() != other.hasSize()) return false;
    if (hasSize()) {
      if (getSize()
          != other.getSize()) return false;
    }
    if (!getNumbersList()
        .equals(other.getNumbersList())) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    if (hasSize()) {
      hash = (37 * hash) + SIZE_FIELD_NUMBER;
      hash = (53 * hash) + getSize();
    }
    if (getNumbersCount() > 0) {
      hash = (37 * hash) + NUMBERS_FIELD_NUMBER;
      hash = (53 * hash) + getNumbersList().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static ru.hse.java.numbers.protos.Numbers parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ru.hse.java.numbers.protos.Numbers parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ru.hse.java.numbers.protos.Numbers parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ru.hse.java.numbers.protos.Numbers parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ru.hse.java.numbers.protos.Numbers parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ru.hse.java.numbers.protos.Numbers parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ru.hse.java.numbers.protos.Numbers parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static ru.hse.java.numbers.protos.Numbers parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static ru.hse.java.numbers.protos.Numbers parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static ru.hse.java.numbers.protos.Numbers parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static ru.hse.java.numbers.protos.Numbers parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static ru.hse.java.numbers.protos.Numbers parseFrom(
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
  public static Builder newBuilder(ru.hse.java.numbers.protos.Numbers prototype) {
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
   * Protobuf type {@code Numbers}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:Numbers)
      ru.hse.java.numbers.protos.NumbersOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return ru.hse.java.numbers.protos.NumbersProtos.internal_static_Numbers_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return ru.hse.java.numbers.protos.NumbersProtos.internal_static_Numbers_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ru.hse.java.numbers.protos.Numbers.class, ru.hse.java.numbers.protos.Numbers.Builder.class);
    }

    // Construct using ru.hse.java.numbers.protos.Numbers.newBuilder()
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
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      size_ = 0;
      bitField0_ = (bitField0_ & ~0x00000001);
      numbers_ = emptyIntList();
      bitField0_ = (bitField0_ & ~0x00000002);
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return ru.hse.java.numbers.protos.NumbersProtos.internal_static_Numbers_descriptor;
    }

    @java.lang.Override
    public ru.hse.java.numbers.protos.Numbers getDefaultInstanceForType() {
      return ru.hse.java.numbers.protos.Numbers.getDefaultInstance();
    }

    @java.lang.Override
    public ru.hse.java.numbers.protos.Numbers build() {
      ru.hse.java.numbers.protos.Numbers result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public ru.hse.java.numbers.protos.Numbers buildPartial() {
      ru.hse.java.numbers.protos.Numbers result = new ru.hse.java.numbers.protos.Numbers(this);
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.size_ = size_;
        to_bitField0_ |= 0x00000001;
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        numbers_.makeImmutable();
        bitField0_ = (bitField0_ & ~0x00000002);
      }
      result.numbers_ = numbers_;
      result.bitField0_ = to_bitField0_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof ru.hse.java.numbers.protos.Numbers) {
        return mergeFrom((ru.hse.java.numbers.protos.Numbers)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(ru.hse.java.numbers.protos.Numbers other) {
      if (other == ru.hse.java.numbers.protos.Numbers.getDefaultInstance()) return this;
      if (other.hasSize()) {
        setSize(other.getSize());
      }
      if (!other.numbers_.isEmpty()) {
        if (numbers_.isEmpty()) {
          numbers_ = other.numbers_;
          bitField0_ = (bitField0_ & ~0x00000002);
        } else {
          ensureNumbersIsMutable();
          numbers_.addAll(other.numbers_);
        }
        onChanged();
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      if (!hasSize()) {
        return false;
      }
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      ru.hse.java.numbers.protos.Numbers parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (ru.hse.java.numbers.protos.Numbers) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private int size_ ;
    /**
     * <code>required int32 size = 1;</code>
     * @return Whether the size field is set.
     */
    @java.lang.Override
    public boolean hasSize() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>required int32 size = 1;</code>
     * @return The size.
     */
    @java.lang.Override
    public int getSize() {
      return size_;
    }
    /**
     * <code>required int32 size = 1;</code>
     * @param value The size to set.
     * @return This builder for chaining.
     */
    public Builder setSize(int value) {
      bitField0_ |= 0x00000001;
      size_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>required int32 size = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearSize() {
      bitField0_ = (bitField0_ & ~0x00000001);
      size_ = 0;
      onChanged();
      return this;
    }

    private com.google.protobuf.Internal.IntList numbers_ = emptyIntList();
    private void ensureNumbersIsMutable() {
      if (!((bitField0_ & 0x00000002) != 0)) {
        numbers_ = mutableCopy(numbers_);
        bitField0_ |= 0x00000002;
       }
    }
    /**
     * <code>repeated int32 numbers = 2;</code>
     * @return A list containing the numbers.
     */
    public java.util.List<java.lang.Integer>
        getNumbersList() {
      return ((bitField0_ & 0x00000002) != 0) ?
               java.util.Collections.unmodifiableList(numbers_) : numbers_;
    }
    /**
     * <code>repeated int32 numbers = 2;</code>
     * @return The count of numbers.
     */
    public int getNumbersCount() {
      return numbers_.size();
    }
    /**
     * <code>repeated int32 numbers = 2;</code>
     * @param index The index of the element to return.
     * @return The numbers at the given index.
     */
    public int getNumbers(int index) {
      return numbers_.getInt(index);
    }
    /**
     * <code>repeated int32 numbers = 2;</code>
     * @param index The index to set the value at.
     * @param value The numbers to set.
     * @return This builder for chaining.
     */
    public Builder setNumbers(
        int index, int value) {
      ensureNumbersIsMutable();
      numbers_.setInt(index, value);
      onChanged();
      return this;
    }
    /**
     * <code>repeated int32 numbers = 2;</code>
     * @param value The numbers to add.
     * @return This builder for chaining.
     */
    public Builder addNumbers(int value) {
      ensureNumbersIsMutable();
      numbers_.addInt(value);
      onChanged();
      return this;
    }
    /**
     * <code>repeated int32 numbers = 2;</code>
     * @param values The numbers to add.
     * @return This builder for chaining.
     */
    public Builder addAllNumbers(
        java.lang.Iterable<? extends java.lang.Integer> values) {
      ensureNumbersIsMutable();
      com.google.protobuf.AbstractMessageLite.Builder.addAll(
          values, numbers_);
      onChanged();
      return this;
    }
    /**
     * <code>repeated int32 numbers = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearNumbers() {
      numbers_ = emptyIntList();
      bitField0_ = (bitField0_ & ~0x00000002);
      onChanged();
      return this;
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


    // @@protoc_insertion_point(builder_scope:Numbers)
  }

  // @@protoc_insertion_point(class_scope:Numbers)
  private static final ru.hse.java.numbers.protos.Numbers DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new ru.hse.java.numbers.protos.Numbers();
  }

  public static ru.hse.java.numbers.protos.Numbers getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  @java.lang.Deprecated public static final com.google.protobuf.Parser<Numbers>
      PARSER = new com.google.protobuf.AbstractParser<Numbers>() {
    @java.lang.Override
    public Numbers parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new Numbers(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<Numbers> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<Numbers> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public ru.hse.java.numbers.protos.Numbers getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

