package de.unhappycodings.quarry.common.networking.toServer;

import de.unhappycodings.quarry.common.block.QuarryBlock;
import de.unhappycodings.quarry.common.blockentity.QuarryEntity;
import de.unhappycodings.quarry.common.item.AreaCard;
import de.unhappycodings.quarry.common.networking.toClient.QuarryClientBooleanPacket;
import de.unhappycodings.quarry.common.networking.toClient.QuarryClientIntPacket;
import de.unhappycodings.quarry.common.networking.toClient.QuarryClientModePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {

    public static void handleQuarryBooleanPacketOnMain(QuarryBooleanPacket data, final IPayloadContext context) {
        boolean clientRefresh = data.clientRefresh();
        String type = data.packetType();
        BlockPos pos = data.pos();

        Player player = context.player();
        BlockEntity machine = player.level().getBlockEntity(pos);
        if (!(machine instanceof QuarryEntity blockEntity)) return;

        if (type.contains("locked")) {
            if (!clientRefresh) blockEntity.setLocked(!blockEntity.getLocked());

            PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new QuarryClientBooleanPacket(machine.getBlockPos(), blockEntity.getLocked(), "locked"));
        } else if (type.contains("loop")) {
            if (!clientRefresh) blockEntity.setLoop(!blockEntity.getLoop());

            PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new QuarryClientBooleanPacket(machine.getBlockPos(), blockEntity.getLoop(), "loop"));
        } else if (type.contains("filter")) {
            if (!clientRefresh) blockEntity.setFilter(!blockEntity.getFilter());

            PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new QuarryClientBooleanPacket(machine.getBlockPos(), blockEntity.getFilter(), "filter"));
        } else if (type.contains("skip")) {
            if (!clientRefresh) blockEntity.setSkip(!blockEntity.getSkip());

            PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new QuarryClientBooleanPacket(machine.getBlockPos(), blockEntity.getSkip(), "skip"));
        } else if (type.contains("replace")) {
            if (!clientRefresh) blockEntity.setReplace(!blockEntity.getReplace());

            PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new QuarryClientBooleanPacket(machine.getBlockPos(), blockEntity.getReplace(), "replace"));
        }

    }

    public static void handleQuarryChangedPacketOnMain(QuarryChangedPacket data, final IPayloadContext context) {
        Player player = context.player();
        Level level = player.level();
        QuarryEntity blockEntity = (QuarryEntity) level.getBlockEntity(data.pos());
        blockEntity.setChanged();

        if (data.packetType() == 1) blockEntity.refreshPositions(data.stack(), level); // refresh
        if (data.packetType() == 2) blockEntity.resetPositions(); // reset
    }

    public static void handleQuarryIntPacketOnMain(QuarryIntPacket data, final IPayloadContext context) {
        String type = data.packetType();
        BlockPos pos = data.pos();
        int add = data.add();

        Player player = context.player();
        BlockEntity machine = player.level().getBlockEntity(pos);
        if (!(machine instanceof QuarryEntity blockEntity)) return;
        if (type.contains("speed")) {
            if (add != 0) {
                int newSpeed = blockEntity.getSpeed() + add;
                if (newSpeed >= 0 && newSpeed <= 6) {
                    blockEntity.setSpeed(newSpeed);
                    PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new QuarryClientIntPacket(machine.getBlockPos(), newSpeed, "speed"));
                }
            } else {
                PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new QuarryClientIntPacket(machine.getBlockPos(), blockEntity.getSpeed(), "speed"));
            }
        } else if (type.contains("eject")) {
            if (add != 0) {
                int newEject = blockEntity.getEject() + add;
                if (newEject >= 0 && newEject <= 3) {
                    blockEntity.setEject(newEject);
                    PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new QuarryClientIntPacket(machine.getBlockPos(), newEject, "eject"));
                } else {
                    blockEntity.setEject(0);
                    PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new QuarryClientIntPacket(machine.getBlockPos(), 0, "eject"));
                }
            } else {
                PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new QuarryClientIntPacket(machine.getBlockPos(), blockEntity.getEject(), "eject"));
            }
        }
    }

    public static void handleQuarryModePacketOnMain(QuarryModePacket data, final IPayloadContext context) {
        BlockPos pos = data.pos();
        int add = data.add();

        Player player = context.player();
        BlockEntity machine = player.level().getBlockEntity(pos);
        if (!(machine instanceof QuarryEntity blockEntity)) return;
        if (add != -1) {
            int newMode = blockEntity.getMode() + add;
            if (add == 10) {
                newMode = blockEntity.getMode() - 1;
                if (newMode < 0) newMode = 4;
                blockEntity.setMode(newMode);
                PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new QuarryClientModePacket(machine.getBlockPos(), newMode));
                return;
            }
            if (newMode >= 0 && newMode <= 4) {
                blockEntity.setMode(newMode);
                PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new QuarryClientModePacket(machine.getBlockPos(), newMode));
            } else {
                blockEntity.setMode(0);
                PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new QuarryClientModePacket(machine.getBlockPos(), 0));
            }
        } else {
            PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new QuarryClientModePacket(machine.getBlockPos(), blockEntity.getMode()));
        }
    }

    public static void handleQuarryPowerPacketOnMain(QuarryPowerPacket data, final IPayloadContext context) {
        BlockPos pos = data.pos();
        boolean add = data.add();

        Player player = context.player();
        Level level = player.level();
        BlockEntity machine = player.level().getBlockEntity(pos);
        if (!(machine instanceof QuarryEntity)) return;
        if (level.getBlockState(pos).getValue(QuarryBlock.POWERED)) {
            level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(QuarryBlock.ACTIVE, add));
            if (!add) {
                level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(QuarryBlock.WORKING, false));
            }
        }
    }

    public static void handleAreaCardItemPacketOnMain(AreaCardItemPacket data, final IPayloadContext context) {
        Player player = context.player();
        ItemStack oldStack = player.getItemBySlot(EquipmentSlot.MAINHAND);
        if (oldStack.getItem() instanceof AreaCard) {
            player.setItemInHand(InteractionHand.MAIN_HAND, data.stack());
        }
    }

}