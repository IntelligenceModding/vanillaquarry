package de.unhappycodings.vanillaquarry.common.network.toserver;

import de.unhappycodings.vanillaquarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.vanillaquarry.common.blocks.QuarryBlock;
import de.unhappycodings.vanillaquarry.common.network.base.IPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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

    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        Level level = player.getCommandSenderWorld();
        if (level.getBlockState(pos).getValue(QuarryBlock.POWERED)) {
            level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(QuarryBlock.ACTIVE, add));
        }
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(add);
    }
}
