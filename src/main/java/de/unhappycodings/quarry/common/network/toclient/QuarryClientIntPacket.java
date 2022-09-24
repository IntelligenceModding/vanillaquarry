package de.unhappycodings.quarry.common.network.toclient;

import de.unhappycodings.quarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.quarry.common.network.base.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class QuarryClientIntPacket implements IPacket {

    private final BlockPos pos;
    private final int add;
    private final String type;

    public QuarryClientIntPacket(BlockPos pos, int add, String type) {
        this.pos = pos;
        this.add = add;
        this.type = type;
    }

    public static QuarryClientIntPacket decode(FriendlyByteBuf buffer) {
        return new QuarryClientIntPacket(buffer.readBlockPos(), buffer.readInt(), buffer.readUtf());
    }


    @SuppressWarnings("ConstantConditions")
    public void handle(NetworkEvent.Context context) {
        LocalPlayer player = Minecraft.getInstance().player;
        BlockEntity machine = player.level.getBlockEntity(pos);
        if (!(machine instanceof QuarryBlockEntity blockEntity)) return;
        if (type.contains("speed"))
            blockEntity.setSpeed(add);
        if (type.contains("eject"))
            blockEntity.setEject(add);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(add);
        buffer.writeUtf(type);
    }
}
