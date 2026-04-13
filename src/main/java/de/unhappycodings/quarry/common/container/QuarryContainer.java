package de.unhappycodings.quarry.common.container;

import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.common.blockentity.QuarryEntity;
import de.unhappycodings.quarry.common.container.base.BaseContainer;
import de.unhappycodings.quarry.common.container.base.BaseSlot;
import de.unhappycodings.quarry.common.container.base.SlotCondition;
import de.unhappycodings.quarry.common.container.base.SlotInputHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuarryContainer extends BaseContainer {
    public static List<Item> burnables = new ArrayList<>();
    public static SlotItemHandler inputSlot;

    public QuarryContainer(int id, Inventory inventory, BlockPos pos, Level level) {
        super(Quarry.QUARRY_CONTAINER.get(), id, inventory, pos, level);
        layoutPlayerInventorySlots(8, 122);
        if (tileEntity != null) {
            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
            if (handler != null) {
                for (Map.Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
                    if (entry.getValue().getBurnTime(new ItemStack(entry.getValue(), 1), RecipeType.SMELTING) > 0) {
                        burnables.add(entry.getValue());
                    }
                }

                addSlot(new BaseSlot(handler, inventory, 0, 13, 30, BaseSlot.GHOST_OVERLAY, stack -> burnables.contains(stack.getItem())).addGhostListOverlays(burnables));
                addSlot(new BaseSlot(handler, inventory, 1, 31, 30, BaseSlot.GHOST_OVERLAY, stack -> burnables.contains(stack.getItem())).addGhostListOverlays(burnables));
                addSlot(new BaseSlot(handler, inventory, 2, 13, 48, BaseSlot.GHOST_OVERLAY, stack -> burnables.contains(stack.getItem())).addGhostListOverlays(burnables));
                addSlot(new BaseSlot(handler, inventory, 3, 31, 48, BaseSlot.GHOST_OVERLAY, stack -> burnables.contains(stack.getItem())).addGhostListOverlays(burnables));
                addSlot(new BaseSlot(handler, inventory, 4, 13, 66, BaseSlot.GHOST_OVERLAY, stack -> burnables.contains(stack.getItem())).addGhostListOverlays(burnables));
                addSlot(new BaseSlot(handler, inventory, 5, 31, 66, BaseSlot.GHOST_OVERLAY, stack -> burnables.contains(stack.getItem())).addGhostListOverlays(burnables));

                addSlot(new SlotInputHandler(handler, 6, 129, 30, new SlotCondition().setNeededItem(Items.AIR))); //Output
                addSlot(new SlotInputHandler(handler, 7, 147, 30, new SlotCondition().setNeededItem(Items.AIR))); //Output
                addSlot(new SlotInputHandler(handler, 8, 129, 48, new SlotCondition().setNeededItem(Items.AIR))); //Output
                addSlot(new SlotInputHandler(handler, 9, 147, 48, new SlotCondition().setNeededItem(Items.AIR))); //Output
                addSlot(new SlotInputHandler(handler, 10, 129, 66, new SlotCondition().setNeededItem(Items.AIR))); //Output
                addSlot(new SlotInputHandler(handler, 11, 147, 66, new SlotCondition().setNeededItem(Items.AIR))); //Output

                inputSlot = new BaseSlot(handler, inventory, 12, 147, 87, BaseSlot.GHOST_OVERLAY, stack -> stack.is(Quarry.AREA_CARD.get())).addGhostOverlays(Quarry.AREA_CARD.get());
                addSlot(inputSlot); // Card

                addSlot(new SlotInputHandler(handler, 13, 129, 87, new SlotCondition()));
            }
        }
    }

    public QuarryEntity getTile() {
        return this.tileEntity;
    }

    @Override
    public boolean stillValid(@Nonnull Player playerIn) {
        return true;
    }
}
