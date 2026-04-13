package de.unhappycodings.quarry.common.container.base;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

// CREDIT GOES TO: Sr_endi  | https://github.com/Seniorendi
public class SlotInputHandler extends SlotItemHandler {
    SlotCondition condition;

    public SlotInputHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition, SlotCondition condition) {
        super(itemHandler, index, xPosition, yPosition);
        this.condition = condition;
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return condition.isValid(stack);
    }
}
