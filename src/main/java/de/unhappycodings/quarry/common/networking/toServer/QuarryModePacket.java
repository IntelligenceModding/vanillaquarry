package de.unhappycodings.quarry.common.networking.toServer;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record QuarryModePacket(BlockPos pos, int add) implements CustomPacketPayload {

    public static final Type<QuarryModePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("quarry", "mode"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // 'name' will be encoded and decoded as a string
    // 'age' will be encoded and decoded as an integer
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, QuarryModePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, QuarryModePacket::pos,
            ByteBufCodecs.VAR_INT, QuarryModePacket::add,
            QuarryModePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}