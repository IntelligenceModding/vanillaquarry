package de.unhappycodings.vanillaquarry.common.network.toserver;

import de.unhappycodings.vanillaquarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.vanillaquarry.common.network.PacketHandler;
import de.unhappycodings.vanillaquarry.common.network.base.IPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import org.checkerframework.checker.units.qual.C;

public class QuarryDarkModePacket implements IPacket {
    private final boolean darkmode;

    public QuarryDarkModePacket(boolean darkmode) {
        this.darkmode = darkmode;
    }

    public static QuarryDarkModePacket decode(FriendlyByteBuf buffer) {
        return new QuarryDarkModePacket(buffer.readBoolean());
    }

    @SuppressWarnings("ConstantConditions")
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();

        player.getPersistentData().putBoolean("quarry-darkmode", darkmode);
        System.out.println("server packet: " + player.getPersistentData().getBoolean("quarry-darkmode"));

        PacketHandler.sendToClient(new QuarryClientDarkModePacket(darkmode), player);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(darkmode);
    }
}
