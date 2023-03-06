package de.unhappycodings.quarry.common.item;

import de.unhappycodings.quarry.common.blocks.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QuarryBlockItem extends BlockItem {

    public QuarryBlockItem() {
        super(ModBlocks.QUARRY.get(), new Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced) {
        CompoundTag tag = stack.getOrCreateTag().getCompound("BlockEntityTag");
        if (tag.contains("Owner")) {
            String owner = "undefined";
            String ownerString = tag.getString("Owner");
            if (!ownerString.isEmpty())
                owner = ownerString.replace("@", " (") + (ownerString.equals("undefined") ? "" : ")");
            tooltipComponents.add(Component.translatable("gui.quarry.owner").withStyle(yellow()).append(" ").append(owner));
            tooltipComponents.add(Component.translatable("gui.quarry.security").withStyle(yellow()).append(" ").append(tag.getBoolean("Locked") ?
                    Component.translatable("gui.quarry.lock.private").withStyle(red()) : Component.translatable("gui.quarry.lock.public").withStyle(green())));
            tooltipComponents.add(Component.translatable("gui.quarry.fueled").withStyle(yellow()).append(" ").append(tag.getInt("BurnTime") > 0 ?
                    Component.translatable("gui.quarry.yes").withStyle(green()) : Component.translatable("gui.quarry.no").withStyle(red())));

        }
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }

    public Style yellow() {
        return Style.EMPTY.withColor(ChatFormatting.YELLOW);
    }

    public Style red() {
        return Style.EMPTY.withColor(ChatFormatting.RED);
    }

    public Style green() {
        return Style.EMPTY.withColor(ChatFormatting.GREEN);
    }

    public Style gray() {
        return Style.EMPTY.withColor(ChatFormatting.GRAY);
    }

}
