package de.unhappycodings.quarry.common.container.base;

import com.mojang.blaze3d.systems.RenderSystem;
import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.client.config.ClientConfig;
import de.unhappycodings.quarry.client.gui.GuiUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

// CREDIT GOES TO: Sr_endi  | https://github.com/Seniorendi
public class BaseSlot extends SlotItemHandler {
    public static final ResourceLocation GHOST_OVERLAY = ResourceLocation.fromNamespaceAndPath(Quarry.MOD_ID, "textures/gui/slot/ghost_overlay.png");
    public static final ResourceLocation GHOST_OVERLAY_DARK = ResourceLocation.fromNamespaceAndPath(Quarry.MOD_ID, "textures/gui/slot/ghost_overlay_dark.png");

    private final Inventory inventory;
    private final Predicate<ItemStack> canPlace;
    public boolean isEnabled = true;
    int size;
    private ItemStack[] ghostOverlays;
    private int nextGhostItemTick = 0;
    private ItemStack currentGhostItem;

    public BaseSlot(IItemHandler itemHandler, Inventory inventory, int index, int xPosition, int yPosition, ResourceLocation texture, Predicate<ItemStack> canPlace, ItemStack... ghostOverlays) {
        super(itemHandler, index, xPosition, yPosition);
        this.inventory = inventory;
        this.size = 18;
        this.canPlace = canPlace;
        this.ghostOverlays = ghostOverlays;
    }

    public BaseSlot addGhostOverlays(Item... ghostOverlays) {
        ItemStack[] items = new ItemStack[ghostOverlays.length];
        for (int i = 0; i < ghostOverlays.length; i++)
            items[i] = new ItemStack(ghostOverlays[i], 1);

        this.ghostOverlays = ArrayUtils.addAll(this.ghostOverlays, items);
        return this;
    }

    public BaseSlot addGhostListOverlays(List<Item> ghostOverlays) {
        ItemStack[] items = new ItemStack[ghostOverlays.size()];
        for (int i = 0; i < ghostOverlays.size(); i++) {
            items[i] = new ItemStack(ghostOverlays.get(i), 1);
        }
        this.ghostOverlays = ArrayUtils.addAll(this.ghostOverlays, items);
        return this;
    }

    public int getTextX() {
        return this.x - (getSize() - 16) / 2;
    }

    public int getTextY() {
        return this.y - (getSize() - 16) / 2;
    }

    public int getSize() {
        return size;
    }

    public BaseSlot setSize(int size) {
        this.size = size;
        return this;
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        this.setChanged();
        return canPlace.test(stack);
    }

    public void setEnabled(boolean enable) {
        this.isEnabled = enable;
    }

    @Override
    public boolean isActive() {
        return isEnabled;
    }

    @Override
    public void setChanged() {
        if (inventory != null) inventory.setChanged();
    }

    public ItemStack[] getGhostOverlayItem() {
        return ghostOverlays;
    }

    @OnlyIn(Dist.CLIENT)
    public void renderGhostOverlay(GuiGraphics graphics, int x, int y) {
        if (getGhostOverlayItem() != null && getGhostOverlayItem().length > 0) {
            nextGhostItemTick++;

            if (!getItem().isEmpty()) return;
            if (this.currentGhostItem == null || this.currentGhostItem.isEmpty())
                this.currentGhostItem = getGhostOverlayItem()[new Random().nextInt(getGhostOverlayItem().length)];
            if (nextGhostItemTick % 500 == 0) {
                this.currentGhostItem = getGhostOverlayItem()[new Random().nextInt(getGhostOverlayItem().length)];
                nextGhostItemTick = 0;
            }

            graphics.pose().pushPose();

            graphics.renderItem(this.currentGhostItem, x + this.x, y + this.y);

            RenderSystem.setShaderColor(1, 1, 1, 0.45f);
            graphics.pose().translate(0, 0, 200);

            graphics.fill(x + this.x, y + this.y, x + this.x + 16, y + this.y + 16, ClientConfig.enableQuarryDarkmode.get() ? 0xA0434343 : 0xA08B8B8B);

            GuiUtil.reset();
            graphics.pose().popPose();

        }
    }
}
