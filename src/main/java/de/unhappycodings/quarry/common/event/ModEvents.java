package de.unhappycodings.quarry.common.event;

import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.common.block.QuarryBlock;
import de.unhappycodings.quarry.common.blockentity.EnergyQuarryEntity;
import de.unhappycodings.quarry.common.blockentity.QuarryEntity;
import de.unhappycodings.quarry.common.networking.toClient.QuarryClientBooleanPacket;
import de.unhappycodings.quarry.common.networking.toClient.QuarryClientIntPacket;
import de.unhappycodings.quarry.common.networking.toClient.QuarryClientModePacket;
import de.unhappycodings.quarry.common.networking.toServer.AreaCardItemPacket;
import de.unhappycodings.quarry.common.networking.toServer.QuarryBooleanPacket;
import de.unhappycodings.quarry.common.networking.toServer.QuarryChangedPacket;
import de.unhappycodings.quarry.common.networking.toServer.QuarryIntPacket;
import de.unhappycodings.quarry.common.networking.toServer.QuarryModePacket;
import de.unhappycodings.quarry.common.networking.toServer.QuarryPowerPacket;
import de.unhappycodings.quarry.common.networking.toServer.ServerPayloadHandler;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import team.reborn.energy.api.EnergyStorage;

import java.util.Objects;

public class ModEvents {
    public static void register() {
        registerPayloads();
        registerReceivers();
        registerBlockApis();
        registerBlockBreakProtection();
    }

    private static void registerPayloads() {
        PayloadTypeRegistry.clientboundPlay().register(QuarryClientBooleanPacket.TYPE, QuarryClientBooleanPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(QuarryClientIntPacket.TYPE, QuarryClientIntPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(QuarryClientModePacket.TYPE, QuarryClientModePacket.STREAM_CODEC);

        PayloadTypeRegistry.serverboundPlay().register(QuarryBooleanPacket.TYPE, QuarryBooleanPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(QuarryChangedPacket.TYPE, QuarryChangedPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(QuarryIntPacket.TYPE, QuarryIntPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(QuarryModePacket.TYPE, QuarryModePacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(QuarryPowerPacket.TYPE, QuarryPowerPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(AreaCardItemPacket.TYPE, AreaCardItemPacket.STREAM_CODEC);
    }

    private static void registerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(QuarryBooleanPacket.TYPE, ServerPayloadHandler::handleQuarryBooleanPacketOnMain);
        ServerPlayNetworking.registerGlobalReceiver(QuarryChangedPacket.TYPE, ServerPayloadHandler::handleQuarryChangedPacketOnMain);
        ServerPlayNetworking.registerGlobalReceiver(QuarryIntPacket.TYPE, ServerPayloadHandler::handleQuarryIntPacketOnMain);
        ServerPlayNetworking.registerGlobalReceiver(QuarryModePacket.TYPE, ServerPayloadHandler::handleQuarryModePacketOnMain);
        ServerPlayNetworking.registerGlobalReceiver(QuarryPowerPacket.TYPE, ServerPayloadHandler::handleQuarryPowerPacketOnMain);
        ServerPlayNetworking.registerGlobalReceiver(AreaCardItemPacket.TYPE, ServerPayloadHandler::handleAreaCardItemPacketOnMain);
    }

    private static void registerBlockApis() {
        ItemStorage.SIDED.registerForBlockEntity(QuarryEntity::getSidedStorage, Quarry.QUARRY_ENTITY.get());
        ItemStorage.SIDED.registerForBlockEntity(QuarryEntity::getSidedStorage, Quarry.ENERGY_QUARRY_ENTITY.get());
        EnergyStorage.SIDED.registerForBlockEntity(EnergyQuarryEntity::getEnergyStorage, Quarry.ENERGY_QUARRY_ENTITY.get());
    }

    private static void registerBlockBreakProtection() {
        PlayerBlockBreakEvents.BEFORE.register(ModEvents::canBreakQuarry);
    }

    private static boolean canBreakQuarry(Level level, Player player, BlockPos pos, net.minecraft.world.level.block.state.BlockState state, BlockEntity blockEntity) {
        if (!(state.getBlock() instanceof QuarryBlock)) return true;
        if (!(blockEntity instanceof QuarryEntity quarry)) return true;

        if ((!Objects.equals(quarry.getOwner(), player.getName().getString() + "@" + player.getStringUUID()) && quarry.getLocked()) && !player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) {
            String owner = quarry.getOwner();
            if (owner.isEmpty()) owner = "undefined";
            player.sendSystemMessage(Component.translatable("gui.quarry.message.quarry_from").append(" " + owner + " ").append(Component.translatable("gui.quarry.message.is_locked")).withStyle(ChatFormatting.YELLOW));
            return false;
        }

        return true;
    }
}
