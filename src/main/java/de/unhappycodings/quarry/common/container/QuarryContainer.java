package de.unhappycodings.quarry.common.container;

import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.common.blockentity.QuarryEntity;
import de.unhappycodings.quarry.common.container.base.BaseContainer;
import de.unhappycodings.quarry.common.container.base.BaseSlot;
import de.unhappycodings.quarry.common.container.base.SlotCondition;
import de.unhappycodings.quarry.common.container.base.SlotInputHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuarryContainer extends BaseContainer {
    public static List<Item> burnables = new ArrayList<>();
    public static Slot inputSlot;

    public QuarryContainer(int id, Inventory inventory, BlockPos pos, Level level) {
        super(Quarry.QUARRY_CONTAINER.get(), id, inventory, pos, level);
        layoutPlayerInventorySlots(8, 122);
        if (tileEntity != null) {
            Container handler = tileEntity.inventory;
            if (handler != null) {
                for (Map.Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
                    if (level.fuelValues().burnDuration(new ItemStack(entry.getValue(), 1)) > 0) {
                        burnables.add(entry.getValue());
                    }
                }
                boolean acceptsFuelItems = tileEntity.acceptsFuelItems();

                addFuelSlot(handler, inventory, 0, 13, 30, acceptsFuelItems);
                addFuelSlot(handler, inventory, 1, 31, 30, acceptsFuelItems);
                addFuelSlot(handler, inventory, 2, 13, 48, acceptsFuelItems);
                addFuelSlot(handler, inventory, 3, 31, 48, acceptsFuelItems);
                addFuelSlot(handler, inventory, 4, 13, 66, acceptsFuelItems);
                addFuelSlot(handler, inventory, 5, 31, 66, acceptsFuelItems);

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

    private void addFuelSlot(Container handler, Inventory inventory, int index, int x, int y, boolean acceptsFuelItems) {
        BaseSlot slot = new BaseSlot(handler, inventory, index, x, y, BaseSlot.GHOST_OVERLAY, stack -> acceptsFuelItems && burnables.contains(stack.getItem()))
                .addGhostListOverlays(acceptsFuelItems ? burnables : List.of());
        slot.setEnabled(acceptsFuelItems);
        addSlot(slot);
    }

    public QuarryEntity getTile() {
        return this.tileEntity;
    }

    @Override
    public boolean stillValid(@Nonnull Player playerIn) {
        return true;
    }

    @Override
    protected int getTeInventorySlotCount() {
        return 14;
    }
}
