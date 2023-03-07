package de.unhappycodings.quarry.common.network.toserver;

import de.unhappycodings.quarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.quarry.common.network.base.IPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

public class QuarryChangedPacket implements IPacket {
    BlockPos pos;
    ItemStack stack;
    int type;

    public QuarryChangedPacket(ItemStack stack, int type, BlockPos pos) {
        this.pos = pos;
        this.type = type;
        this.stack = stack;
    }

    public static QuarryChangedPacket decode(FriendlyByteBuf buffer) {
        return new QuarryChangedPacket(buffer.readItem(), buffer.readInt(), buffer.readBlockPos());
    }

    @SuppressWarnings("ConstantConditions")
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        Level level = player.getCommandSenderWorld();
        level.getBlockEntity(pos).setChanged();
        System.out.println("Type: " + type);
        if (type == 1) ((QuarryBlockEntity) level.getBlockEntity(pos)).refreshPositions(stack); // refresh
        if (type == 2) ((QuarryBlockEntity) level.getBlockEntity(pos)).resetPositions(); // reset
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeItem(stack != null ? stack : new ItemStack(Items.AIR));
        buffer.writeInt(type);
        buffer.writeBlockPos(pos);
    }
}
