package de.unhappycodings.quarry.common.event;

import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.common.block.QuarryBlock;
import de.unhappycodings.quarry.common.blockentity.QuarryEntity;
import de.unhappycodings.quarry.common.networking.toServer.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.Objects;

@EventBusSubscriber(modid = Quarry.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void onQuarryBlockDestroy(BlockEvent.BreakEvent event) {
        Level level = event.getPlayer().level();
        BlockPos pos = event.getPos();
        if (!level.getBlockState(pos).is(Quarry.QUARRY_BLOCK.get())) return;
        QuarryEntity quarry = (QuarryEntity) level.getBlockEntity(pos);
        Player player = event.getPlayer();
        if ((!Objects.equals(quarry.getOwner(), player.getName().getString() + "@" + player.getStringUUID()) && quarry.getLocked()) && !player.hasPermissions(2)) {
            String owner = quarry.getOwner();
            if (owner.isEmpty()) owner = "undefined";
            event.setCanceled(true);
            player.sendSystemMessage(Component.translatable("gui.quarry.message.quarry_from").append(" " + owner + " ").append(Component.translatable("gui.quarry.message.is_locked")).withStyle(ChatFormatting.YELLOW));
        }
    }

    @SubscribeEvent // on the mod event bus
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                QuarryBooleanPacket.TYPE,
                QuarryBooleanPacket.STREAM_CODEC,
                ServerPayloadHandler::handleQuarryBooleanPacketOnMain
        );
        registrar.playToServer(
                QuarryChangedPacket.TYPE,
                QuarryChangedPacket.STREAM_CODEC,
                ServerPayloadHandler::handleQuarryChangedPacketOnMain
        );
        registrar.playToServer(
                QuarryIntPacket.TYPE,
                QuarryIntPacket.STREAM_CODEC,
                ServerPayloadHandler::handleQuarryIntPacketOnMain
        );
        registrar.playToServer(
                QuarryModePacket.TYPE,
                QuarryModePacket.STREAM_CODEC,
                ServerPayloadHandler::handleQuarryModePacketOnMain
        );
        registrar.playToServer(
                QuarryPowerPacket.TYPE,
                QuarryPowerPacket.STREAM_CODEC,
                ServerPayloadHandler::handleQuarryPowerPacketOnMain
        );
        registrar.playToServer(
                AreaCardItemPacket.TYPE,
                AreaCardItemPacket.STREAM_CODEC,
                ServerPayloadHandler::handleAreaCardItemPacketOnMain
        );
    }

}
