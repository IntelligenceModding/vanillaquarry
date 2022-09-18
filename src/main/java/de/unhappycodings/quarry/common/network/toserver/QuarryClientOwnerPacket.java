package de.unhappycodings.quarry.common.network.toserver;

import de.unhappycodings.quarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.quarry.common.network.base.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class QuarryClientOwnerPacket implements IPacket {

    private final BlockPos pos;
    private final boolean locked;

    public QuarryClientOwnerPacket(BlockPos pos, boolean uuid) {
        this.pos = pos;
        this.locked = uuid;
    }

    public static QuarryClientOwnerPacket decode(FriendlyByteBuf buffer) {
        return new QuarryClientOwnerPacket(buffer.readBlockPos(), buffer.readBoolean());
    }

    public void handle(NetworkEvent.Context context) {
        LocalPlayer player = Minecraft.getInstance().player;
        BlockEntity machine = player.level.getBlockEntity(pos);
        if (!(machine instanceof QuarryBlockEntity blockEntity)) return;
        blockEntity.setLocked(locked);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(locked);
    }
}
