package de.unhappycodings.quarry.common.network.toclient;

import de.unhappycodings.quarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.quarry.common.network.base.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class QuarryClientBooleanPacket implements IPacket {

    private final BlockPos pos;
    private final boolean state;
    private final String type;

    public QuarryClientBooleanPacket(BlockPos pos, boolean state, String type) {
        this.pos = pos;
        this.state = state;
        this.type = type;
    }

    public static QuarryClientBooleanPacket decode(FriendlyByteBuf buffer) {
        return new QuarryClientBooleanPacket(buffer.readBlockPos(), buffer.readBoolean(), buffer.readUtf());
    }

    public void handle(NetworkEvent.Context context) {
        if (Minecraft.getInstance().level == null) return;

        BlockEntity machine = Minecraft.getInstance().level.getBlockEntity(pos);
        if (!(machine instanceof QuarryBlockEntity blockEntity)) return;
        if (type.contains("locked")) blockEntity.setLocked(state);
        if (type.contains("loop")) blockEntity.setLoop(state);
        if (type.contains("filter")) blockEntity.setFilter(state);
        if (type.contains("skip")) blockEntity.setSkip(state);
        if (type.contains("replace")) blockEntity.setReplace(state);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(state);
        buffer.writeUtf(type);
    }
}
