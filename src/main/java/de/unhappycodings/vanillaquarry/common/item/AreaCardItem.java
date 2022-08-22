package de.unhappycodings.vanillaquarry.common.item;

import de.unhappycodings.vanillaquarry.VanillaQuarry;
import de.unhappycodings.vanillaquarry.common.blocks.QuarryBlock;
import de.unhappycodings.vanillaquarry.common.util.CalcUtil;
import de.unhappycodings.vanillaquarry.common.util.NbtUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AreaCardItem extends Item {

    public AreaCardItem() {
        super(new Item.Properties().stacksTo(1).tab(VanillaQuarry.creativeTab));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced) {
        if (stack.getOrCreateTag().contains("pos1")) {
            String pos = stack.getOrCreateTag().get("pos1").getAsString().replace("{", "").replace("}", "").replace(",", " ");
            tooltipComponents.add(new TextComponent("Box (Solid)").setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            if (stack.getOrCreateTag().contains("pos2"))
                tooltipComponents.add(new TextComponent("#" + CalcUtil.getBlocks(new BlockPos(getPos(stack.getOrCreateTag(), "pos1")), new BlockPos(getPos(stack.getOrCreateTag(), "pos2")), level).size() + " Blocks").setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            tooltipComponents.add(new TextComponent("From ").append("" + pos).setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
        }
        if (stack.getOrCreateTag().contains("pos2")) {
            String pos = stack.getOrCreateTag().get("pos2").getAsString().replace("{", "").replace("}", "").replace(",", " ");
            tooltipComponents.add(new TextComponent("To ").append("" + pos).setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
        }
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }

    public static BlockPos getPos(CompoundTag tag, String key) throws IllegalStateException {
        if (!tag.contains(key))
            throw new IllegalStateException("Tag does not contain position");
        CompoundTag positionTag = (CompoundTag) tag.get(key);
        return NbtUtil.getPos(positionTag);
    }

    @NotNull
    @Override
    public InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) return InteractionResult.CONSUME;
        Player player = context.getPlayer();
        ItemStack item = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        Block block = level.getBlockState(pos).getBlock();
        if (block instanceof QuarryBlock || context.getHand() != InteractionHand.MAIN_HAND) return InteractionResult.CONSUME;
        if (!item.getOrCreateTag().contains("pos1") || item.getOrCreateTag().contains("pos2")) {
            item = new ItemStack(item.getItem());
            CompoundTag posTag = new CompoundTag();
            writePos(posTag, pos);
            item.getOrCreateTag().put("pos1", posTag);
            player.sendMessage(new TranslatableComponent("message.vanillaquarry.savedfirst").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)), Util.NIL_UUID);
        } else {
            if (!item.getOrCreateTag().contains("pos2")) {
                CompoundTag posTag = new CompoundTag();
                writePos(posTag, pos);
                item.getOrCreateTag().put("pos2", posTag);
                player.sendMessage(new TranslatableComponent("message.vanillaquarry.savedsecond").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)), Util.NIL_UUID);
            }
        }
        player.setItemSlot(EquipmentSlot.MAINHAND, item);
        return InteractionResult.SUCCESS;
    }

    public static void writePos(CompoundTag nbt, BlockPos pos) {
        nbt.putInt("x", pos.getX());
        nbt.putInt("y", pos.getY());
        nbt.putInt("z", pos.getZ());
    }

}
