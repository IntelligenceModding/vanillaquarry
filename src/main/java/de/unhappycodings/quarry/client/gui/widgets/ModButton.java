package de.unhappycodings.quarry.client.gui.widgets;

import de.unhappycodings.quarry.client.config.ClientConfig;
import de.unhappycodings.quarry.client.gui.GuiUtil;
import de.unhappycodings.quarry.client.gui.widgets.base.BaseWidget;
import de.unhappycodings.quarry.common.container.QuarryScreen;
import de.unhappycodings.quarry.common.container.base.BaseScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class ModButton extends BaseWidget {
    private final Runnable onClick;
    private final Runnable onClickReverse;
    private final Supplier<Boolean> isValid;
    private final Identifier texture;
    boolean playSound;
    int tX;
    int tY;

    public ModButton(int x, int y, int width, int height, Identifier texture, Runnable onClick, Runnable onClickReverse, BlockEntity tile, BaseScreen<?> screen, int tX, int tY, boolean playSound) {
        super(x, y, width, height, tile, screen);
        this.onClick = onClick;
        this.onClickReverse = onClickReverse;
        this.isValid = () -> true;
        this.texture = texture;
        this.tX = tX;
        this.tY = tY;
        this.playSound = playSound;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (QuarryScreen.modeMouseButton != null && QuarryScreen.modeMouseButton.isMouseOver(event.x(), event.y()) && event.button() == 1) {
            if (isValid != null && isValid.get() && onClickReverse != null) {
                onClickReverse.run();
                playDownSound(minecraft.getSoundManager());
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        if (isMouseOver(event.x(), event.y())) {
            if (isValid != null && isValid.get() && onClick != null) {
                onClick.run();
                playDownSound(minecraft.getSoundManager());
            }
        }
        super.onClick(event, doubleClick);
    }

    @Override
    protected void extractWidgetRenderState(@Nonnull GuiGraphicsExtractor graphics, int x, int y, float partialTicks) {
        GuiUtil.bind(texture);
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, this.getX(), this.getY(), 0, ClientConfig.enableQuarryDarkmode.get() ? tY / 2f : 0, width, height, tX, tY);

        if (isMouseOver(x, y))
            graphics.outline(getX(), getY(), getWidth(), getHeight(), ARGB.color(255, 255, 255, 255));
    }

    @Override
    protected void updateWidgetNarration(@Nonnull NarrationElementOutput pNarrationElementOutput) {
        // Overriden by purpose
    }

    @Override
    public void playDownSound(@Nonnull SoundManager pHandler) {
        if (playSound) super.playDownSound(pHandler);
    }
}
