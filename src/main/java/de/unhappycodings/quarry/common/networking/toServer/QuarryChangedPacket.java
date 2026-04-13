package de.unhappycodings.quarry.common.networking.toServer;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record QuarryChangedPacket(int packetType, BlockPos pos, ItemStack stack) implements CustomPacketPayload {

    public static final Type<QuarryChangedPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("quarry", "changed"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // 'name' will be encoded and decoded as a string
    // 'age' will be encoded and decoded as an integer
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<RegistryFriendlyByteBuf, QuarryChangedPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, QuarryChangedPacket::packetType,
            BlockPos.STREAM_CODEC, QuarryChangedPacket::pos,
            ItemStack.STREAM_CODEC, QuarryChangedPacket::stack,
            QuarryChangedPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}