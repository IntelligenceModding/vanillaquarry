package de.unhappycodings.vanillaquarry.common.network.toserver;

import de.unhappycodings.vanillaquarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.vanillaquarry.common.network.base.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class QuarryClientDarkModePacket implements IPacket {
    private final boolean darkmode;

    public QuarryClientDarkModePacket(boolean darkmode) {
        this.darkmode = darkmode;
    }

    public static QuarryClientDarkModePacket decode(FriendlyByteBuf buffer) {
        return new QuarryClientDarkModePacket(buffer.readBoolean());
    }


    @SuppressWarnings("ConstantConditions")
    public void handle(NetworkEvent.Context context) {
        LocalPlayer player = Minecraft.getInstance().player;
        player.getPersistentData().putBoolean("quarry-darkmode", darkmode);
        System.out.println("client packet: " + player.getPersistentData().getBoolean("quarry-darkmode"));
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(darkmode);
    }
}
