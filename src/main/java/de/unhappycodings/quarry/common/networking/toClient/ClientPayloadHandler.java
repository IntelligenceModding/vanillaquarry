package de.unhappycodings.quarry.common.networking.toClient;

import de.unhappycodings.quarry.common.blockentity.QuarryEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ClientPayloadHandler {

    public static void handleQuarryClientIntPacketOnMain(QuarryClientIntPacket data, final ClientPlayNetworking.Context context) {
        context.client().execute(() -> handleQuarryClientIntPacket(data, context));
    }

    private static void handleQuarryClientIntPacket(QuarryClientIntPacket data, final ClientPlayNetworking.Context context) {
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

    public static void handleQuarryClientModePacketOnMain(QuarryClientModePacket data, final ClientPlayNetworking.Context context) {
        context.client().execute(() -> handleQuarryClientModePacket(data, context));
    }

    private static void handleQuarryClientModePacket(QuarryClientModePacket data, final ClientPlayNetworking.Context context) {
        Level level = context.player().level();
        BlockEntity machine = level.getBlockEntity(data.pos());
        if (!(machine instanceof QuarryEntity blockEntity)) return;
        blockEntity.setMode(data.add());
    }

    public static void handleQuarryClientBooleanPacketOnMain(QuarryClientBooleanPacket data, final ClientPlayNetworking.Context context) {
        context.client().execute(() -> handleQuarryClientBooleanPacket(data, context));
    }

    private static void handleQuarryClientBooleanPacket(QuarryClientBooleanPacket data, final ClientPlayNetworking.Context context) {
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
