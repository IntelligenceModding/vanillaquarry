package de.unhappycodings.vanillaquarry.common.network.toserver;

import de.unhappycodings.vanillaquarry.common.blocks.QuarryBlock;
import de.unhappycodings.vanillaquarry.common.network.base.IPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

public class QuarryChangedPacket implements IPacket {
    private final BlockPos pos;

    public QuarryChangedPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static QuarryChangedPacket decode(FriendlyByteBuf buffer) {
        return new QuarryChangedPacket(buffer.readBlockPos());
    }

    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        Level level = player.getCommandSenderWorld();
        level.getBlockEntity(pos).setChanged();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }
}
