package de.unhappycodings.quarry.common.container.base;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

// CREDIT GOES TO: Sr_endi  | https://github.com/Seniorendi
public abstract class BaseScreen<T extends BaseContainer> extends AbstractContainerScreen<T> {

    public BaseScreen(T screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        imageWidth = getSizeX();
        imageHeight = getSizeY();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int x, int y, float partialTicks) {
        renderBackground(graphics);
        super.render(graphics, x, y, partialTicks);
        renderTooltip(graphics, x, y);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int x, int y) {
        graphics.blit(getTexture(), getGuiLeft(), getGuiTop(), 0, 0, imageWidth, imageHeight);
    }

    public abstract int getSizeX();

    public abstract int getSizeY();

    public abstract ResourceLocation getTexture();
}
