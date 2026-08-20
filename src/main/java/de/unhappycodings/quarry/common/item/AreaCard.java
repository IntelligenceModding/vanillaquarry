package de.unhappycodings.quarry.common.item;

import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.common.block.QuarryBlock;
import de.unhappycodings.quarry.common.container.AreaCardContainer;
import de.unhappycodings.quarry.common.util.NbtUtil;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class AreaCard extends Item implements ExtendedMenuProvider<BlockPos> {

    public AreaCard(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    public static void writePos(CompoundTag nbt, BlockPos pos) {
        NbtUtil.writePos(nbt, pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (stack.has(Quarry.POS_1)) {
            String pos = stack.get(Quarry.POS_1).toString().replace("{", "").replace("}", "").replace(",", " ");
            tooltipComponents.accept(Component.translatable("gui.areacard.box").setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            if (stack.has(Quarry.LAST_BLOCK)) {
                int blocksMined = stack.get(Quarry.LAST_BLOCK);
                tooltipComponents.accept(Component.translatable("gui.areacard.mined").append(" " + blocksMined).setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            }
            tooltipComponents.accept(Component.translatable("gui.areacard.from").append(" " + pos).setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
        }
        if (stack.has(Quarry.POS_2)) {
            String pos = stack.get(Quarry.POS_2).toString().replace("{", "").replace("}", "").replace(",", " ");
            tooltipComponents.accept(Component.translatable("gui.areacard.to").append(" " + pos).setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
        }
        for (int i = 0; i <= 6; i++) {
            CompoundTag nbt = stack.get(Quarry.FILTERS);
            if (nbt != null && !nbt.isEmpty()) {
                tooltipComponents.accept(Component.translatable("gui.areacard.filters_active", nbt.keySet().size()).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
                tooltipComponents.accept(Component.translatable("gui.areacard.filters_enable").setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
                break;
            }
        }
        super.appendHoverText(stack, context, tooltipDisplay, tooltipComponents, tooltipFlag);
    }

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) return InteractionResult.CONSUME;
        Player player = context.getPlayer();
        ItemStack item = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        Block block = level.getBlockState(pos).getBlock();
        if (block instanceof QuarryBlock || context.getHand() != InteractionHand.MAIN_HAND)
            return InteractionResult.CONSUME;
        if (!item.has(Quarry.POS_1) || item.has(Quarry.POS_2)) {
            item = new ItemStack(item.getItem());
            CompoundTag posTag = new CompoundTag();
            writePos(posTag, pos);
            item.set(Quarry.POS_1, posTag);
            player.sendSystemMessage(Component.translatable("message.quarry.savedfirst").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
        } else {
            if (!item.has(Quarry.POS_2)) {
                CompoundTag posTag = new CompoundTag();
                writePos(posTag, pos);
                item.set(Quarry.POS_2, posTag);
                player.sendSystemMessage(Component.translatable("message.quarry.savedsecond").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            }
        }
        player.setItemSlot(EquipmentSlot.MAINHAND, item);
        return InteractionResult.SUCCESS;
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull Level pLevel, @Nonnull Player pPlayer, @Nonnull InteractionHand pUsedHand) {
        if (pUsedHand == InteractionHand.MAIN_HAND) {
            if (pPlayer.isShiftKeyDown() && !pLevel.isClientSide()) {
                pPlayer.setItemInHand(pUsedHand, new ItemStack(pPlayer.getItemInHand(InteractionHand.MAIN_HAND).getItem()));
                pPlayer.sendSystemMessage(Component.literal("Area card reset to defaults").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            } else if (!pLevel.isClientSide()) {
                pPlayer.openMenu(this);
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Nonnull
    @Override
    public Component getDisplayName() {
        return Component.literal("Area Card");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @Nonnull Inventory pPlayerInventory, Player pPlayer) {
        return new AreaCardContainer(pContainerId, pPlayerInventory, pPlayer.getOnPos(), pPlayer.level());
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayer player) {
        return player.getOnPos();
    }
}
