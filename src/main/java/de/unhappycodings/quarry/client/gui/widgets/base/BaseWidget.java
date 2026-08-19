package de.unhappycodings.quarry.client.gui.widgets.base;

import de.unhappycodings.quarry.client.gui.GuiUtil;
import de.unhappycodings.quarry.client.gui.widgets.ModButton;
import de.unhappycodings.quarry.common.container.base.BaseScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseWidget extends AbstractWidget {

    public final BaseScreen<?> screen;
    protected final List<BaseWidget> children = new ArrayList<>();
    protected BlockEntity tile;
    protected Minecraft minecraft = Minecraft.getInstance();
    protected int leftPos;
    protected int topPos;

    public BaseWidget(int x, int y, int width, int height, BlockEntity tile, BaseScreen<?> screen) {
        super(screen.getLeftPos() + x, screen.getTopPos() + y, width, height, Component.empty());
        this.tile = tile;
        this.screen = screen;
        this.leftPos = screen.getLeftPos();
        this.topPos = screen.getTopPos();
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int x, int y, float partialTick) {
        GuiUtil.reset();

        for (BaseWidget child : children) {
            child.extractRenderState(graphics, x, y, partialTick);
        }
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        for (BaseWidget child : children) {
            if (child instanceof ModButton) child.onClick(event, doubleClick);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        for (BaseWidget child : children) {
            if (child instanceof ModButton) child.mouseClicked(event, doubleClick);
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void playDownSound(@Nonnull SoundManager pHandler) {
        if (this instanceof ModButton)
            super.playDownSound(pHandler);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY) || children.stream().anyMatch(child -> child.isMouseOver(mouseX, mouseY));
    }

}
