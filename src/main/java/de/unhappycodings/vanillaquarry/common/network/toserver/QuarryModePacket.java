package de.unhappycodings.vanillaquarry.common.network.toserver;

import de.unhappycodings.vanillaquarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.vanillaquarry.common.blocks.QuarryBlock;
import de.unhappycodings.vanillaquarry.common.network.PacketHandler;
import de.unhappycodings.vanillaquarry.common.network.base.IPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class QuarryModePacket implements IPacket {

    private final BlockPos pos;
    private final int add;

    public QuarryModePacket(BlockPos pos, int add) {
        this.pos = pos;
        this.add = add;
    }

    public static QuarryModePacket decode(FriendlyByteBuf buffer) {
        return new QuarryModePacket(buffer.readBlockPos(), buffer.readInt());
    }

    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        BlockEntity machine = player.getCommandSenderWorld().getBlockEntity(pos);
        if (!(machine instanceof QuarryBlockEntity blockEntity)) return;
        if (add != -1) {
            int newMode = blockEntity.getMode() + add;
            if(newMode >= 0 && newMode <= 4) {
                blockEntity.setMode(newMode);
                PacketHandler.sendToClient(new QuarryClientModePacket(machine.getBlockPos(), newMode), player);
            } else {
                blockEntity.setMode(0);
                PacketHandler.sendToClient(new QuarryClientModePacket(machine.getBlockPos(), 0), player);
            }
        } else {
            PacketHandler.sendToClient(new QuarryClientModePacket(machine.getBlockPos(), blockEntity.getMode()), player);
        }
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(add);
    }
}
