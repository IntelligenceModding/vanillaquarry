package de.unhappycodings.quarry.common.item;

import de.unhappycodings.quarry.common.config.ServerConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class FEQuarryItem extends QuarryItem {
    public FEQuarryItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (!ServerConfig.enableFEQuarry.get()) {
            tooltipComponents.accept(Component.translatable("gui.quarry.disabled.tooltip").withStyle(ChatFormatting.RED));
        }
        super.appendHoverText(stack, context, tooltipDisplay, tooltipComponents, tooltipFlag);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!ServerConfig.enableFEQuarry.get()) {
            if (!context.getLevel().isClientSide() && context.getPlayer() != null) {
                context.getPlayer().sendSystemMessage(Component.translatable("gui.quarry.message.disabled"));
            }
            return InteractionResult.FAIL;
        }

        return super.useOn(context);
    }
}
