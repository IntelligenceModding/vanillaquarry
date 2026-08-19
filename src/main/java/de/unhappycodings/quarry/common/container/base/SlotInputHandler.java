package de.unhappycodings.quarry.common.container.base;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

// CREDIT GOES TO: Sr_endi  | https://github.com/Seniorendi
public class SlotInputHandler extends Slot {
    SlotCondition condition;

    public SlotInputHandler(Container container, int index, int xPosition, int yPosition, SlotCondition condition) {
        super(container, index, xPosition, yPosition);
        this.condition = condition;
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return condition.isValid(stack);
    }
}
