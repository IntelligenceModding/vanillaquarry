package de.unhappycodings.quarry.common.networking.toServer;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record QuarryBooleanPacket(BlockPos pos, boolean clientRefresh, String packetType) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<QuarryBooleanPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("quarry", "boolean"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // 'name' will be encoded and decoded as a string
    // 'age' will be encoded and decoded as an integer
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, QuarryBooleanPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, QuarryBooleanPacket::pos,
            ByteBufCodecs.BOOL, QuarryBooleanPacket::clientRefresh,
            ByteBufCodecs.STRING_UTF8, QuarryBooleanPacket::packetType,
            QuarryBooleanPacket::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}