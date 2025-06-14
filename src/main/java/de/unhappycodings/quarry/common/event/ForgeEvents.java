package de.unhappycodings.quarry.common.event;

import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.quarry.common.blocks.ModBlocks;
import de.unhappycodings.quarry.common.blocks.QuarryBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = Quarry.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {

    @SubscribeEvent
    public static void onQuarryBlockDestroy(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        Level level = event.getPlayer().level();
        BlockPos pos = event.getPos();
        if (!(level.getBlockState(pos).getBlock() instanceof QuarryBlock)) return;
        event.setCanceled(true);
        QuarryBlockEntity quarry = (QuarryBlockEntity) level.getBlockEntity(pos);
        if ((!Objects.equals(quarry.getOwner(), player.getName().getString() + "@" + player.getStringUUID()) && quarry.getLocked()) && !player.hasPermissions(2)) {
            String owner = quarry.getOwner();
            if (owner.isEmpty()) owner = "undefined";
            player.sendSystemMessage(Component.translatable("gui.quarry.message.quarry_from").append(" " + owner + " ").append(Component.translatable("gui.quarry.message.is_locked")).withStyle(ChatFormatting.YELLOW));
        } else {
            QuarryBlockEntity machine = (QuarryBlockEntity) level.getBlockEntity(pos);
            ItemStack machineStack = new ItemStack(ModBlocks.QUARRY.get(), 1);
            machine.saveToItem(machineStack);
            if (machine.hasCustomName()) machineStack.setHoverName(machine.getCustomName());
            ItemEntity itementity = new ItemEntity(level, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, machineStack);
            itementity.setDefaultPickUpDelay();
            level.addFreshEntity(itementity);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

}
