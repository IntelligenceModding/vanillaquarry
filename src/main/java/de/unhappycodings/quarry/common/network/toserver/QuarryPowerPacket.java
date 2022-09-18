package de.unhappycodings.quarry.common.network.toserver;

import de.unhappycodings.quarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.quarry.common.blocks.QuarryBlock;
import de.unhappycodings.quarry.common.network.base.IPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class QuarryPowerPacket implements IPacket {

    private final BlockPos pos;
    private final boolean add;

    public QuarryPowerPacket(BlockPos pos, boolean add) {
        this.pos = pos;
        this.add = add;
    }

    public static QuarryPowerPacket decode(FriendlyByteBuf buffer) {
        return new QuarryPowerPacket(buffer.readBlockPos(), buffer.readBoolean());
    }

    @SuppressWarnings("ConstantConditions")
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        Level level = player.getCommandSenderWorld();
        BlockEntity machine = player.getCommandSenderWorld().getBlockEntity(pos);
        if (!(machine instanceof QuarryBlockEntity)) return;
        if (level.getBlockState(pos).getValue(QuarryBlock.POWERED)) {
            level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(QuarryBlock.ACTIVE, add));
            if (!add) {
                level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(QuarryBlock.WORKING, false));
            }
        }
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(add);
    }
}
