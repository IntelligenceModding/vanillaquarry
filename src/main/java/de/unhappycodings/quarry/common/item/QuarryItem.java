package de.unhappycodings.quarry.common.item;

import de.unhappycodings.quarry.Quarry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QuarryItem extends BlockItem {

    public QuarryItem() {
        super(Quarry.QUARRY_BLOCK.get(), new Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (stack.getComponents().isEmpty()) return;
        CompoundTag tag = (CompoundTag) DataComponentMap.CODEC.encodeStart(NbtOps.INSTANCE, stack.getComponents()).result().get();
        if (tag.isEmpty()) return;

        CompoundTag quarryTag = tag.getCompound("minecraft:block_entity_data");
        if (quarryTag.contains("Owner")) {
            String owner = "undefined";
            String ownerString = quarryTag.getString("Owner");
            if (!ownerString.isEmpty())
                owner = ownerString.replace("@", " (") + (ownerString.equals("undefined") ? "" : ")");
            tooltipComponents.add(Component.translatable("gui.quarry.owner").withStyle(yellow()).append(" ").append(owner));
            tooltipComponents.add(Component.translatable("gui.quarry.security").withStyle(yellow()).append(" ").append(quarryTag.getBoolean("Locked") ?
                    Component.translatable("gui.quarry.lock.private").withStyle(red()) : Component.translatable("gui.quarry.lock.public").withStyle(green())));
            tooltipComponents.add(Component.translatable("gui.quarry.fueled").withStyle(yellow()).append(" ").append(quarryTag.getInt("BurnTime") > 0 ?
                    Component.translatable("gui.quarry.yes").withStyle(green()) : Component.translatable("gui.quarry.no").withStyle(red())));

        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
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
