package de.unhappycodings.quarry.common.container.base;

import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.common.blockentity.QuarryEntity;
import de.unhappycodings.quarry.common.container.AreaCardScreen;
import de.unhappycodings.quarry.common.networking.toServer.QuarryChangedPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public abstract class BaseContainer extends AbstractContainerMenu {
    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_SLOT_COUNT = 14;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private final Container inventory;
    protected QuarryEntity tileEntity;

    protected BaseContainer(@Nullable MenuType<?> type, int id, Inventory inventory, BlockPos pos, Level world) {
        super(type, id);
        this.inventory = inventory;
        if (world != null)
            this.tileEntity = world.getBlockEntity(pos) instanceof QuarryEntity ? (QuarryEntity) world.getBlockEntity(pos) : null;
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(@Nonnull Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem().copy();

        if (playerIn.level().isClientSide() && Minecraft.getInstance().screen instanceof AreaCardScreen screen) {
            if (playerIn.getItemInHand(InteractionHand.MAIN_HAND).is(Quarry.AREA_CARD.get()) && !sourceStack.is(Quarry.AREA_CARD.get()) && playerIn.level().isClientSide()) {
                ItemStack areaCard = playerIn.getItemInHand(InteractionHand.MAIN_HAND);
                Item[] filters = screen.filters;
                for (int i = 0; i < filters.length; i++) {
                    if (filters[i] == Items.AIR) {
                        if (i > 0 && filters[i - 1] == sourceStack.getItem())
                            break;
                        filters[i] = sourceStack.getItem();
                        break;
                    }
                }
                screen.saveFilter(areaCard);
            }

            return ItemStack.EMPTY;
        }

        ItemStack copyOfSourceStack = sourceStack.copy();
        if (playerIn.level().isClientSide() && (sourceStack.is(Quarry.AREA_CARD.get()) || sourceStack.is(Items.AIR))) {

            ClientPlayNetworking.send(new QuarryChangedPacket(sourceStack.is(Quarry.AREA_CARD.get()) ? 1 : 2, tileEntity.getBlockPos(), copyOfSourceStack));
        }

        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX + getTeInventorySlotCount(), false))
                return ItemStack.EMPTY;

        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + getTeInventorySlotCount()) {
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false))
                return ItemStack.EMPTY;

        } else {
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0)
            sourceSlot.set(ItemStack.EMPTY);
        else
            sourceSlot.setChanged();

        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }


    private int addSlotRange(Container handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new Slot(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private void addSlotBox(Container handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int i = 0; i < verAmount; i++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
    }

    public void layoutPlayerInventorySlots(int leftCol, int topRow) {
        addSlotBox(inventory, 9, leftCol, topRow, 9, 18, 3, 18);
        topRow += 58;
        addSlotRange(inventory, 0, leftCol, topRow, 9, 18);
    }

    protected int getTeInventorySlotCount() {
        return TE_INVENTORY_SLOT_COUNT;
    }

}
