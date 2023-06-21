package de.unhappycodings.quarry.common.network;

import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.common.network.base.IPacket;
import de.unhappycodings.quarry.common.network.toclient.QuarryClientBooleanPacket;
import de.unhappycodings.quarry.common.network.toclient.QuarryClientIntPacket;
import de.unhappycodings.quarry.common.network.toclient.QuarryClientModePacket;
import de.unhappycodings.quarry.common.network.toserver.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.Function;

public class PacketHandler {

    private static final String PROTOCOL_VERSION = ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString();

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(Quarry.MOD_ID, "main_channel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    private static int index = 0;

    public static void init() {
        registerClientToServer(QuarryIntPacket.class, QuarryIntPacket::decode);
        registerClientToServer(QuarryPowerPacket.class, QuarryPowerPacket::decode);
        registerClientToServer(QuarryModePacket.class, QuarryModePacket::decode);
        registerClientToServer(QuarryChangedPacket.class, QuarryChangedPacket::decode);
        registerClientToServer(QuarryBooleanPacket.class, QuarryBooleanPacket::decode);
        registerClientToServer(AreaCardItemPacket.class, AreaCardItemPacket::decode);
        registerServerToClient(QuarryClientBooleanPacket.class, QuarryClientBooleanPacket::decode);
        registerServerToClient(QuarryClientIntPacket.class, QuarryClientIntPacket::decode);
        registerServerToClient(QuarryClientModePacket.class, QuarryClientModePacket::decode);
    }

    public static <MSG extends IPacket> void registerServerToClient(Class<MSG> packet, Function<FriendlyByteBuf, MSG> decode) {
        CHANNEL.registerMessage(index++, packet, IPacket::encode, decode, IPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static <MSG extends IPacket> void registerClientToServer(Class<MSG> packet, Function<FriendlyByteBuf, MSG> decode) {
        CHANNEL.registerMessage(index++, packet, IPacket::encode, decode, IPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    public static void sendToClient(Object packet, ServerPlayer player) {
        CHANNEL.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToServer(Object packet) {
        CHANNEL.sendToServer(packet);
    }

    public static void sendTo(Object packet, ServerPlayer player) {
        if (!(player instanceof FakePlayer)) {
            CHANNEL.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }
}
