package de.unhappycodings.quarry.common.networking.toServer;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public record AreaCardItemPacket(UUID player, ItemStack stack) implements CustomPacketPayload {

    public static final Type<AreaCardItemPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("quarry", "areacarditempacket"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // 'name' will be encoded and decoded as a string
    // 'age' will be encoded and decoded as an integer
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<RegistryFriendlyByteBuf, AreaCardItemPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, AreaCardItemPacket::player,
            ItemStack.STREAM_CODEC, AreaCardItemPacket::stack,
            AreaCardItemPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}