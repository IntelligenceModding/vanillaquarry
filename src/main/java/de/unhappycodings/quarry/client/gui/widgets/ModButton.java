package de.unhappycodings.quarry.client.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.client.gui.GuiUtil;
import de.unhappycodings.quarry.client.gui.widgets.base.BaseWidget;
import de.unhappycodings.quarry.common.container.QuarryScreen;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.function.Supplier;

public class ModButton extends BaseWidget {
    public static final ResourceLocation COUNTER_UP = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/counter_plus.png");
    public static final ResourceLocation COUNTER_DOWN = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/counter_minus.png");

    private final Runnable onClick;
    private final Runnable onClickReverse;
    private final Supplier<Boolean> isValid;
    private final ResourceLocation texture;
    private Supplier<Component> hoverText;
    boolean playSound;
    int tX = 0;
    int tY = 0;

    public ModButton(int x, int y, int width, int height, ResourceLocation texture, Runnable onClick, Runnable onClickReverse, BlockEntity tile, QuarryScreen screen, int tX, int tY, boolean playSound) {
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
    public void renderToolTip(@NotNull PoseStack matrixStack, int mouseX, int mouseY) {
        super.renderToolTip(matrixStack, mouseX, mouseY);
        if (hoverText != null && isMouseOver(mouseX, mouseY))
            renderComponentTooltip(matrixStack, Collections.singletonList(hoverText.get()), mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (QuarryScreen.modeMouseButton.isMouseOver(pMouseX, pMouseY) && pButton == 1) {
            if (isValid != null && isValid.get() && onClickReverse != null) {
                onClickReverse.run();
                playDownSound(minecraft.getSoundManager());
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        if (isMouseOver(pMouseX, pMouseY)) {
            if (isValid != null && isValid.get() && onClick != null) {
                onClick.run();
                playDownSound(minecraft.getSoundManager());
            }
        }
        super.onClick(pMouseX, pMouseY);
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int x, int y, float partialTicks) {
        super.render(matrixStack, x, y, partialTicks);
        GuiUtil.bind(texture);

        if (!isMouseOver(x, y) || (isValid != null && !isValid.get()))
            blit(matrixStack, this.x, this.y, 0, 0, width, height, tX, tY);
        if (isMouseOver(x, y) && (isValid != null && isValid.get()))
            blit(matrixStack, this.x, this.y, 0, tY / 2f, width, height, tX, tY);
    }

    @Override
    public void updateNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {
        // overridden by purpose
    }

    @Override
    public void playDownSound(@NotNull SoundManager pHandler) {
        if (playSound)
            super.playDownSound(pHandler);
    }
}
