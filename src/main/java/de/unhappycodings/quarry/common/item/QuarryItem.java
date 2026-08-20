package de.unhappycodings.quarry.common.item;

import de.unhappycodings.quarry.Quarry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class QuarryItem extends BlockItem {

    public QuarryItem(Block block, Item.Properties properties) {
        super(block, properties.stacksTo(1));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (stack.getComponents().isEmpty()) return;
        CompoundTag tag = (CompoundTag) DataComponentMap.CODEC.encodeStart(NbtOps.INSTANCE, stack.getComponents()).result().get();
        if (tag.isEmpty()) return;

        CompoundTag quarryTag = tag.getCompoundOrEmpty("minecraft:block_entity_data");
        if (quarryTag.contains("Owner")) {
            String owner = "undefined";
            String ownerString = quarryTag.getStringOr("Owner", "");
            if (!ownerString.isEmpty())
                owner = ownerString.replace("@", " (") + (ownerString.equals("undefined") ? "" : ")");
            tooltipComponents.accept(Component.translatable("gui.quarry.owner").withStyle(yellow()).append(" ").append(owner));
            tooltipComponents.accept(Component.translatable("gui.quarry.security").withStyle(yellow()).append(" ").append(quarryTag.getBooleanOr("Locked", false) ?
                    Component.translatable("gui.quarry.lock.private").withStyle(red()) : Component.translatable("gui.quarry.lock.public").withStyle(green())));
            long storedPower = quarryTag.contains("Energy") ? quarryTag.getLongOr("Energy", 0L) : quarryTag.getIntOr("BurnTime", 0);
            tooltipComponents.accept(Component.translatable("gui.quarry.fueled").withStyle(yellow()).append(" ").append(storedPower > 0 ?
                    Component.translatable("gui.quarry.yes").withStyle(green()) : Component.translatable("gui.quarry.no").withStyle(red())));

        }
        super.appendHoverText(stack, context, tooltipDisplay, tooltipComponents, tooltipFlag);
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

}
