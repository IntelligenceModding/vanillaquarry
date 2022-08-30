package de.unhappycodings.vanillaquarry.common.network.toserver;

import de.unhappycodings.vanillaquarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.vanillaquarry.common.network.base.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class QuarryClientModePacket implements IPacket {

    private final BlockPos pos;
    private final int add;

    public QuarryClientModePacket(BlockPos pos, int add) {
        this.pos = pos;
        this.add = add;
    }

    public static QuarryClientModePacket decode(FriendlyByteBuf buffer) {
        return new QuarryClientModePacket(buffer.readBlockPos(), buffer.readInt());
    }

    public void handle(NetworkEvent.Context context) {
        LocalPlayer player = Minecraft.getInstance().player;
        BlockEntity machine = player.level.getBlockEntity(pos);
        if (!(machine instanceof QuarryBlockEntity blockEntity)) return;
        blockEntity.setMode(add);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(add);
    }
}
