package de.unhappycodings.quarry.common.network.toserver;

import de.unhappycodings.quarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.quarry.common.network.PacketHandler;
import de.unhappycodings.quarry.common.network.base.IPacket;
import de.unhappycodings.quarry.common.network.toclient.QuarryClientIntPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class QuarryIntPacket implements IPacket {

    private final BlockPos pos;
    private final byte add;
    private final String type;

    public QuarryIntPacket(BlockPos pos, byte add, String type) {
        this.pos = pos;
        this.add = add;
        this.type = type;
    }

    public static QuarryIntPacket decode(FriendlyByteBuf buffer) {
        return new QuarryIntPacket(buffer.readBlockPos(), buffer.readByte(), buffer.readUtf());
    }

    @SuppressWarnings("ConstantConditions")
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        BlockEntity machine = player.getCommandSenderWorld().getBlockEntity(pos);
        if (!(machine instanceof QuarryBlockEntity blockEntity)) return;
        if (type.contains("speed")) {
            if (add != 0) {
                int newSpeed = blockEntity.getSpeed() + add;
                if (newSpeed >= 0 && newSpeed <= 2) {
                    blockEntity.setSpeed(newSpeed);
                    PacketHandler.sendToClient(new QuarryClientIntPacket(machine.getBlockPos(), newSpeed, "speed"), player);
                }
            } else {
                PacketHandler.sendToClient(new QuarryClientIntPacket(machine.getBlockPos(), blockEntity.getSpeed(), "speed"), player);
            }
        } else if (type.contains("eject")) {
            if (add != 0) {
                int newEject = blockEntity.getEject() + add;
                if (newEject >= 0 && newEject <= 3) {
                    blockEntity.setEject(newEject);
                    PacketHandler.sendToClient(new QuarryClientIntPacket(machine.getBlockPos(), newEject, "eject"), player);
                } else {
                    blockEntity.setEject(0);
                    PacketHandler.sendToClient(new QuarryClientIntPacket(machine.getBlockPos(), 0, "eject"), player);
                }
            } else {
                PacketHandler.sendToClient(new QuarryClientIntPacket(machine.getBlockPos(), blockEntity.getEject(), "eject"), player);
            }
        }
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeByte(add);
        buffer.writeUtf(type);
    }
}
