package de.unhappycodings.quarry.common.network.toclient;

import de.unhappycodings.quarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.quarry.common.network.base.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class QuarryClientBooleanPacket implements IPacket {

    private final BlockPos pos;
    private final boolean locked;
    private final String type;

    public QuarryClientBooleanPacket(BlockPos pos, boolean uuid, String type) {
        this.pos = pos;
        this.locked = uuid;
        this.type = type;
    }

    public static QuarryClientBooleanPacket decode(FriendlyByteBuf buffer) {
        return new QuarryClientBooleanPacket(buffer.readBlockPos(), buffer.readBoolean(), buffer.readUtf());
    }

    public void handle(NetworkEvent.Context context) {
        LocalPlayer player = Minecraft.getInstance().player;
        BlockEntity machine = player.level().getBlockEntity(pos);
        if (!(machine instanceof QuarryBlockEntity blockEntity)) return;
        if (type.contains("locked")) blockEntity.setLocked(locked);
        if (type.contains("loop")) blockEntity.setLoop(locked);
        if (type.contains("filter")) blockEntity.setFilter(locked);
        if (type.contains("skip")) blockEntity.setSkip(locked);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(locked);
        buffer.writeUtf(type);
    }
}
