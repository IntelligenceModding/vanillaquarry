package de.unhappycodings.vanillaquarry.common.network.toserver;

import de.unhappycodings.vanillaquarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.vanillaquarry.common.network.base.IPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;

public class QuarryChangedPacket implements IPacket {
    BlockPos pos;
    String type;
    ItemStack stack;

    public QuarryChangedPacket(ItemStack stack, String type, BlockPos pos) {
        this.pos = pos;
        this.type = type;
        this.stack = stack;
    }

    public static QuarryChangedPacket decode(FriendlyByteBuf buffer) {
        return new QuarryChangedPacket(null, null, buffer.readBlockPos());
    }

    @SuppressWarnings("ConstantConditions")
    public void handle(NetworkEvent.Context context) {
        if (Objects.equals(type, "changed")) {
            ServerPlayer player = context.getSender();
            Level level = player.getCommandSenderWorld();
            level.getBlockEntity(pos).setChanged();
        }
        if (Objects.equals(type, "refresh")) {
            ServerPlayer player = context.getSender();
            Level level = player.getCommandSenderWorld();
            level.getBlockEntity(pos).setChanged();
            ((QuarryBlockEntity) level.getBlockEntity(pos)).refreshPositions(stack);
        }

    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }
}
