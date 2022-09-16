package de.unhappycodings.vanillaquarry.common.network.toserver;

import de.unhappycodings.vanillaquarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.vanillaquarry.common.network.PacketHandler;
import de.unhappycodings.vanillaquarry.common.network.base.IPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;

public class QuarryOwnerPacket implements IPacket {

    private final BlockPos pos;
    private final boolean refresh;

    public QuarryOwnerPacket(BlockPos pos, boolean refresh) {
        this.pos = pos;
        this.refresh = refresh;
    }

    public static QuarryOwnerPacket decode(FriendlyByteBuf buffer) {
        return new QuarryOwnerPacket(buffer.readBlockPos(), buffer.readBoolean());
    }

    @SuppressWarnings("ConstantConditions")
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        BlockEntity machine = player.getCommandSenderWorld().getBlockEntity(pos);
        if (!(machine instanceof QuarryBlockEntity blockEntity)) return;
        if (!refresh) {
            blockEntity.setLocked(!blockEntity.getLocked());
            PacketHandler.sendToClient(new QuarryClientOwnerPacket(machine.getBlockPos(), !blockEntity.getLocked()), player);
        } else {
            PacketHandler.sendToClient(new QuarryClientOwnerPacket(machine.getBlockPos(), blockEntity.getLocked()), player);
        }
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(refresh);
    }
}
