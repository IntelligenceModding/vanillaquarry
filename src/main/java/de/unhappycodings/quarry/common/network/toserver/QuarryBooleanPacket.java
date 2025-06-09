package de.unhappycodings.quarry.common.network.toserver;

import de.unhappycodings.quarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.quarry.common.network.PacketHandler;
import de.unhappycodings.quarry.common.network.base.IPacket;
import de.unhappycodings.quarry.common.network.toclient.QuarryClientBooleanPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class QuarryBooleanPacket implements IPacket {

    private final BlockPos pos;
    private final boolean clientRefresh; // if true only passes through variables without updating them in server level
    private final String type;

    public QuarryBooleanPacket(BlockPos pos, boolean state, String type) {
        this.pos = pos;
        this.clientRefresh = state;
        this.type = type;
    }

    public static QuarryBooleanPacket decode(FriendlyByteBuf buffer) {
        return new QuarryBooleanPacket(buffer.readBlockPos(), buffer.readBoolean(), buffer.readUtf());
    }

    @SuppressWarnings("ConstantConditions")
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        BlockEntity machine = player.getCommandSenderWorld().getBlockEntity(pos);
        if (!(machine instanceof QuarryBlockEntity blockEntity)) return;

        if (type.contains("locked")) {
            if (!clientRefresh) blockEntity.setLocked(!blockEntity.getLocked());

            PacketHandler.sendToClient(new QuarryClientBooleanPacket(machine.getBlockPos(), blockEntity.getLocked(), "locked"), player);
        } else if (type.contains("loop")) {
            if (!clientRefresh) blockEntity.setLoop(!blockEntity.getLoop());

            PacketHandler.sendToClient(new QuarryClientBooleanPacket(machine.getBlockPos(), blockEntity.getLoop(), "loop"), player);
        } else if (type.contains("filter")) {
            if (!clientRefresh) blockEntity.setFilter(!blockEntity.getFilter());

            PacketHandler.sendToClient(new QuarryClientBooleanPacket(machine.getBlockPos(), blockEntity.getFilter(), "filter"), player);
        } else if (type.contains("skip")) {
            if (!clientRefresh) blockEntity.setSkip(!blockEntity.getSkip());

            PacketHandler.sendToClient(new QuarryClientBooleanPacket(machine.getBlockPos(), blockEntity.getSkip(), "skip"), player);
        } else if (type.contains("replace")) {
            if (!clientRefresh) blockEntity.setReplace(!blockEntity.getReplace());

            PacketHandler.sendToClient(new QuarryClientBooleanPacket(machine.getBlockPos(), blockEntity.getReplace(), "replace"), player);
        }
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(clientRefresh);
        buffer.writeUtf(type);
    }
}
