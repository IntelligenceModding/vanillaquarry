package de.unhappycodings.quarry.common.network.toserver;

import de.unhappycodings.quarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.quarry.common.network.PacketHandler;
import de.unhappycodings.quarry.common.network.base.IPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class QuarrySpeedPacket implements IPacket {

    private final BlockPos pos;
    private final byte add;

    public QuarrySpeedPacket(BlockPos pos, byte add) {
        this.pos = pos;
        this.add = add;
    }

    public static QuarrySpeedPacket decode(FriendlyByteBuf buffer) {
        return new QuarrySpeedPacket(buffer.readBlockPos(), buffer.readByte());
    }

    @SuppressWarnings("ConstantConditions")
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        BlockEntity machine = player.getCommandSenderWorld().getBlockEntity(pos);
        if (!(machine instanceof QuarryBlockEntity blockEntity)) return;
        if (add != 0) {
            int newSpeed = blockEntity.getSpeed() + add;
            if (newSpeed >= 0 && newSpeed <= 2) {
                blockEntity.setSpeed(newSpeed);
                PacketHandler.sendToClient(new QuarryClientSpeedPacket(machine.getBlockPos(), newSpeed), player);
            }
        } else {
            PacketHandler.sendToClient(new QuarryClientSpeedPacket(machine.getBlockPos(), blockEntity.getSpeed()), player);
        }
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeByte(add);
    }
}
