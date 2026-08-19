package de.unhappycodings.quarry.common.container.base;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

// CREDIT GOES TO: Sr_endi  | https://github.com/Seniorendi
public abstract class BaseScreen<T extends BaseContainer> extends AbstractContainerScreen<T> {

    public BaseScreen(T screenContainer, Inventory inv, Component titleIn, int imageWidth, int imageHeight) {
        super(screenContainer, inv, titleIn, imageWidth, imageHeight);
    }

    @Override
    public void extractRenderState(@Nonnull GuiGraphicsExtractor graphics, int x, int y, float partialTicks) {
        super.extractRenderState(graphics, x, y, partialTicks);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int x, int y, float partialTicks) {
        super.extractBackground(graphics, x, y, partialTicks);
        graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight, 256, 256);
    }

    public abstract int getSizeX();

    public abstract int getSizeY();

    public abstract Identifier getTexture();
}
