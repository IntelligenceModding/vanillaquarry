package de.unhappycodings.quarry.common.container;

import com.mojang.blaze3d.vertex.PoseStack;
import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.client.config.ClientConfig;
import de.unhappycodings.quarry.client.gui.widgets.ModButton;
import de.unhappycodings.quarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.quarry.common.blocks.QuarryBlock;
import de.unhappycodings.quarry.common.config.CommonConfig;
import de.unhappycodings.quarry.common.container.base.BaseScreen;
import de.unhappycodings.quarry.common.container.base.BaseSlot;
import de.unhappycodings.quarry.common.item.AreaCardItem;
import de.unhappycodings.quarry.common.network.PacketHandler;
import de.unhappycodings.quarry.common.network.toserver.*;
import de.unhappycodings.quarry.common.util.CalcUtil;
import de.unhappycodings.quarry.common.util.NbtUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.UsernameCache;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AreaCardScreen extends BaseScreen<AreaCardContainer> {
    public static ModButton darkmodeMouseButton;
    AreaCardContainer container;
    boolean darkmodeButtonIsHovered;

    public AreaCardScreen(AreaCardContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        this.container = screenContainer;
    }

    @Override
    protected void renderLabels(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY) {
        drawText(new TextComponent("Position 1").getString(), pPoseStack, 34, 6);
        drawText(new TextComponent("Position 2").getString(), pPoseStack, 118, 6);
        drawText(new TextComponent("X").getString(), pPoseStack, 9, 19);
        drawText(new TextComponent("Y").getString(), pPoseStack, 9, 36);
        drawText(new TextComponent("Z").getString(), pPoseStack, 9, 53);
        drawText(new TextComponent("X").getString(), pPoseStack, 184, 19);
        drawText(new TextComponent("Y").getString(), pPoseStack, 184, 36);
        drawText(new TextComponent("Z").getString(), pPoseStack, 184, 53);
        drawText(new TextComponent("Filter").getString(), pPoseStack, 87, 72);
        if (darkmodeButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            if (getDarkModeConfigValue()) {
                list.add(new TranslatableComponent("gui.quarry.quarry.tooltip.darkmode.dark"));
                list.add(new TranslatableComponent("gui.quarry.quarry.tooltip.darkmode.dark.switch").withStyle(ChatFormatting.YELLOW));
            } else {
                list.add(new TranslatableComponent("gui.quarry.quarry.tooltip.darkmode.white"));
                list.add(new TranslatableComponent("gui.quarry.quarry.tooltip.darkmode.white.switch").withStyle(ChatFormatting.YELLOW));
            }
            this.renderComponentTooltip(pPoseStack, list, pMouseX - leftPos, pMouseY - topPos);
        }
        LocalPlayer player = Minecraft.getInstance().player;
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.getOrCreateTag().contains("pos1")) {
            BlockPos pos = NbtUtil.getPos(stack.getOrCreateTag().getCompound("pos1"));
            drawCenteredText(new TextComponent(String.valueOf(pos.getX())).getText(), pPoseStack, 55, 19);
            drawCenteredText(new TextComponent(String.valueOf(pos.getY())).getText(), pPoseStack, 55, 36);
            drawCenteredText(new TextComponent(String.valueOf(pos.getZ())).getText(), pPoseStack, 55, 53);
        }
        if (stack.getOrCreateTag().contains("pos2")) {
            BlockPos pos = NbtUtil.getPos(stack.getOrCreateTag().getCompound("pos2"));
            drawCenteredText(new TextComponent(String.valueOf(pos.getX())).getText(), pPoseStack, 142, 19);
            drawCenteredText(new TextComponent(String.valueOf(pos.getY())).getText(), pPoseStack, 142, 36);
            drawCenteredText(new TextComponent(String.valueOf(pos.getZ())).getText(), pPoseStack, 142, 53);
        }
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    protected void init() {
        super.init();
        addElements();
    }

    @Override
    protected boolean isHovering(int pX, int pY, int pWidth, int pHeight, double pMouseX, double pMouseY) {
        if (darkmodeMouseButton != null && darkmodeMouseButton.isMouseOver(pMouseX, pMouseY)) {
            darkmodeButtonIsHovered = true;
        } else {
            if (darkmodeButtonIsHovered) darkmodeButtonIsHovered = false;
        }
        return super.isHovering(pX, pY, pWidth, pHeight, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrixStack, float partialTicks, int x, int y) {
        super.renderBg(matrixStack, partialTicks, x, y);
        Level level = this.getMenu().getLevel();
        BlockPos pos = this.getMenu().getPos();
    }

    protected void addElements() {
        boolean darkmode = getDarkModeConfigValue();
        darkmodeMouseButton = new ModButton(182, 5, 9, 9, darkmode ? Quarry.DARK_MODE : Quarry.WHITE_MODE, () -> {
            refreshWidgets();
            setDarkModeConfigValue(!getDarkModeConfigValue());
        }, null, null, this, 9, 18, true);
        addRenderableOnly(darkmodeMouseButton);
    }

    @Override
    protected void containerTick() {
        refreshDarkmode();
        super.containerTick();
    }

    public boolean getDarkModeConfigValue() {
        return ClientConfig.enableQuarryDarkmode.get();
    }

    public void setDarkModeConfigValue(boolean state) {
        ClientConfig.enableQuarryDarkmode.set(state);
    }

    public void refreshDarkmode() {
        refreshWidgets();
    }

    public void refreshWidgets() {
        clearWidgets();
        addElements();
    }

    @Override
    public int getSizeX() {
        return 197;
    }

    @Override
    public int getSizeY() {
        return 113;
    }

    @Override
    public ResourceLocation getTexture() {
        if (!getDarkModeConfigValue()) refreshDarkmode();
        String texture = "textures/gui/area_card_gui.png";
        if (getDarkModeConfigValue()) texture = "textures/gui/area_card_gui_dark.png";
        return new ResourceLocation(Quarry.MOD_ID, texture);
    }

    public void drawCenteredText(String text, PoseStack stack, int x, int y) {
        Minecraft.getInstance().font.draw(stack, text, x - (Minecraft.getInstance().font.width(text) / 2f), y, 1315860);
    }

    public void drawText(String text, PoseStack stack, int x, int y) {
        Minecraft.getInstance().font.draw(stack, text, x, y, 1315860);
    }
}
