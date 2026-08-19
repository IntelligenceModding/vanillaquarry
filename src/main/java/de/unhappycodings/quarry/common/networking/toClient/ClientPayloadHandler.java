package de.unhappycodings.quarry.common.networking.toClient;

import de.unhappycodings.quarry.common.blockentity.QuarryEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {

    public static void handleQuarryClientIntPacketOnMain(QuarryClientIntPacket data, final IPayloadContext context) {
        Level level = context.player().level();
        BlockEntity machine = level.getBlockEntity(data.pos());
        if (!(machine instanceof QuarryEntity blockEntity)) return;
        String type = data.packetType();
        int add = data.add();
        if (type.contains("speed"))
            blockEntity.setSpeed(add);
        if (type.contains("eject"))
            blockEntity.setEject(add);
    }

    public static void handleQuarryClientModePacketOnMain(QuarryClientModePacket data, final IPayloadContext context) {
        Level level = context.player().level();
        BlockEntity machine = level.getBlockEntity(data.pos());
        if (!(machine instanceof QuarryEntity blockEntity)) return;
        blockEntity.setMode(data.add());
    }

    public static void handleQuarryClientBooleanPacketOnMain(QuarryClientBooleanPacket data, final IPayloadContext context) {
        Level level = context.player().level();
        BlockEntity machine = level.getBlockEntity(data.pos());
        if (!(machine instanceof QuarryEntity blockEntity)) return;
        String type = data.packetType();
        boolean state = data.state();
        if (type.contains("locked")) blockEntity.setLocked(state);
        if (type.contains("loop")) blockEntity.setLoop(state);
        if (type.contains("filter")) blockEntity.setFilter(state);
        if (type.contains("skip")) blockEntity.setSkip(state);
        if (type.contains("replace")) blockEntity.setReplace(state);
    }

}
