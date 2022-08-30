package de.unhappycodings.vanillaquarry.client.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import de.unhappycodings.vanillaquarry.VanillaQuarry;
import de.unhappycodings.vanillaquarry.client.gui.GuiUtil;
import de.unhappycodings.vanillaquarry.client.gui.widgets.base.BaseWidget;
import de.unhappycodings.vanillaquarry.common.container.QuarryScreen;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.function.Supplier;

public class ModButton extends BaseWidget {
    public static final ResourceLocation COUNTER_UP = new ResourceLocation(VanillaQuarry.MOD_ID, "textures/gui/button/counter_plus.png");
    public static final ResourceLocation COUNTER_DOWN = new ResourceLocation(VanillaQuarry.MOD_ID, "textures/gui/button/counter_minus.png");

    private final Runnable onClick;
    private final Supplier<Boolean> isValid;
    private final ResourceLocation texture;
    private Supplier<Component> hoverText;

    int tX = 0;
    int tY = 0;

    public ModButton(int x, int y, int width, int height, ResourceLocation texture, Runnable onClick, BlockEntity tile, QuarryScreen screen, int tX, int tY) {
        super(x, y, width, height, tile, screen);
        this.onClick = onClick;
        this.isValid = () -> true;
        this.texture = texture;
        this.tX = tX;
        this.tY = tY;
    }

    public ModButton addHoverText(Supplier<Component> message) {
        this.hoverText = message;
        return this;
    }

    @Override
    public void renderToolTip(@NotNull PoseStack matrixStack, int mouseX, int mouseY) {
        super.renderToolTip(matrixStack, mouseX, mouseY);
        if (hoverText != null && isMouseOver(mouseX, mouseY))
            renderComponentTooltip(matrixStack, Collections.singletonList(hoverText.get()), mouseX, mouseY);
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        if (isMouseOver(pMouseX, pMouseY)) {
            if (isValid != null && isValid.get()) {
                if (onClick != null) {
                    onClick.run();
                    playDownSound(minecraft.getSoundManager());
                }
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

    }

}
