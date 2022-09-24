package de.unhappycodings.quarry.common.container;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.client.config.ClientConfig;
import de.unhappycodings.quarry.client.gui.GuiUtil;
import de.unhappycodings.quarry.client.gui.widgets.ModButton;
import de.unhappycodings.quarry.common.container.base.BaseScreen;
import de.unhappycodings.quarry.common.item.AreaCardItem;
import de.unhappycodings.quarry.common.network.PacketHandler;
import de.unhappycodings.quarry.common.network.toserver.AreaCardItemPacket;
import de.unhappycodings.quarry.common.util.NbtUtil;
import de.unhappycodings.quarry.common.util.RenderUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AreaCardScreen extends BaseScreen<AreaCardContainer> {
    public static final ResourceLocation GHOST_OVERLAY = new ResourceLocation(Quarry.MOD_ID, "textures/gui/slot/filter_overlay.png");
    public static final ResourceLocation GHOST_OVERLAY_DARK = new ResourceLocation(Quarry.MOD_ID, "textures/gui/slot/filter_overlay_dark.png");

    public static ModButton darkmodeMouseButton;
    AreaCardContainer container;
    boolean darkmodeButtonIsHovered;

    public AreaCardScreen(AreaCardContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        this.container = screenContainer;
    }

    @Override
    protected void renderLabels(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY) {
        drawText(Component.translatable("item.quarry.areacard.text.pos_1").getString(), pPoseStack, 34, 7);
        drawText(Component.translatable("item.quarry.areacard.text.pos_2").getString(), pPoseStack, 118, 7);
        drawText(Component.literal("X").getString(), pPoseStack, 9, 22);
        drawText(Component.literal("Y").getString(), pPoseStack, 9, 39);
        drawText(Component.literal("Z").getString(), pPoseStack, 9, 56);
        drawText(Component.literal("X").getString(), pPoseStack, 184, 22);
        drawText(Component.literal("Y").getString(), pPoseStack, 184, 39);
        drawText(Component.literal("Z").getString(), pPoseStack, 184, 56);
        drawText(Component.translatable("item.quarry.areacard.text.filter").getString(), pPoseStack, 87, 72);
        if (darkmodeButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            if (getDarkModeConfigValue()) {
                list.add(Component.translatable("gui.quarry.quarry.tooltip.darkmode.dark"));
                list.add(Component.translatable("gui.quarry.quarry.tooltip.darkmode.dark.switch").withStyle(ChatFormatting.YELLOW));
            } else {
                list.add(Component.translatable("gui.quarry.quarry.tooltip.darkmode.white"));
                list.add(Component.translatable("gui.quarry.quarry.tooltip.darkmode.white.switch").withStyle(ChatFormatting.YELLOW));
            }
            this.renderComponentTooltip(pPoseStack, list, pMouseX - leftPos, pMouseY - topPos);
        }
        LocalPlayer player = Minecraft.getInstance().player;
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.getOrCreateTag().contains("pos1")) {
            BlockPos pos = NbtUtil.getPos(stack.getOrCreateTag().getCompound("pos1"));
            drawCenteredText(Component.literal(String.valueOf(pos.getX())).getString(), pPoseStack, 55, 22);
            drawCenteredText(Component.literal(String.valueOf(pos.getY())).getString(), pPoseStack, 55, 39);
            drawCenteredText(Component.literal(String.valueOf(pos.getZ())).getString(), pPoseStack, 55, 56);
        }
        if (stack.getOrCreateTag().contains("pos2")) {
            BlockPos pos = NbtUtil.getPos(stack.getOrCreateTag().getCompound("pos2"));
            drawCenteredText(Component.literal(String.valueOf(pos.getX())).getString(), pPoseStack, 142, 22);
            drawCenteredText(Component.literal(String.valueOf(pos.getY())).getString(), pPoseStack, 142, 39);
            drawCenteredText(Component.literal(String.valueOf(pos.getZ())).getString(), pPoseStack, 142, 56);
        }
        CompoundTag tag = stack.getOrCreateTag().getCompound("Filters");
        renderGhostOverlay(pPoseStack, new ItemStack(Blocks.COBBLESTONE), 40, 84, tag.getBoolean("0"));
        renderGhostOverlay(pPoseStack, new ItemStack(Blocks.STONE), 58, 84, tag.getBoolean("1"));
        renderGhostOverlay(pPoseStack, new ItemStack(Blocks.GRAVEL), 76, 84, tag.getBoolean("2"));
        renderGhostOverlay(pPoseStack, new ItemStack(Blocks.DIRT), 94, 84, tag.getBoolean("3"));
        renderGhostOverlay(pPoseStack, new ItemStack(Blocks.SAND), 112, 84, tag.getBoolean("4"));
        renderGhostOverlay(pPoseStack, new ItemStack(Blocks.RED_SAND), 130, 84, tag.getBoolean("5"));
        renderGhostOverlay(pPoseStack, new ItemStack(Blocks.NETHERRACK), 148, 84, tag.getBoolean("6"));
    }

    public void renderGhostOverlay(PoseStack stack, ItemStack item, int x, int y, boolean normal) {
        stack.pushPose();
        RenderUtil.renderGuiItem(item, x, y);
        RenderSystem.setShaderTexture(0, normal ? Quarry.BLANK : (ClientConfig.enableQuarryDarkmode.get() ? GHOST_OVERLAY_DARK : GHOST_OVERLAY));
        RenderSystem.setShaderColor(1, 1, 1, 0.65f);
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        //stack.translate(0,0,10);
        GuiComponent.blit(stack, x, y, 0, 0, 16, 16, 16, 16);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        GuiUtil.reset();
        stack.popPose();
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

    protected void addElements() {
        boolean darkmode = getDarkModeConfigValue();
        darkmodeMouseButton = new ModButton(182, 5, 9, 9, darkmode ? Quarry.DARK_MODE : Quarry.WHITE_MODE, () -> {
            refreshWidgets();
            setDarkModeConfigValue(!getDarkModeConfigValue());
        }, null, null, this, 9, 18, true);
        addRenderableWidget(darkmodeMouseButton);
        addRenderableWidget(new ModButton(40, 84, 16, 16, Quarry.BLANK, () -> changeFilter(0), null, null, this, 16, 16, true));
        addRenderableWidget(new ModButton(58, 84, 16, 16, Quarry.BLANK, () -> changeFilter(1), null, null, this, 16, 16, true));
        addRenderableWidget(new ModButton(76, 84, 16, 16, Quarry.BLANK, () -> changeFilter(2), null, null, this, 16, 16, true));
        addRenderableWidget(new ModButton(94, 84, 16, 16, Quarry.BLANK, () -> changeFilter(3), null, null, this, 16, 16, true));
        addRenderableWidget(new ModButton(112, 84, 16, 16, Quarry.BLANK, () -> changeFilter(4), null, null, this, 16, 16, true));
        addRenderableWidget(new ModButton(130, 84, 16, 16, Quarry.BLANK, () -> changeFilter(5), null, null, this, 16, 16, true));
        addRenderableWidget(new ModButton(148, 84, 16, 16, Quarry.BLANK, () -> changeFilter(6), null, null, this, 16, 16, true));
    }

    public void changeFilter(int index) {
        ItemStack stack = Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.getItem() instanceof AreaCardItem) {
            CompoundTag tag = new CompoundTag();
            CompoundTag currentTag = stack.getOrCreateTag().getCompound("Filters");
            for (int i = 0; i < 9; i++) {
                if (currentTag.contains(String.valueOf(i)) && i != index) {
                    if (currentTag.getBoolean(String.valueOf(i)))
                        tag.putBoolean(String.valueOf(i), true);
                }
            }
            if (!currentTag.getBoolean(String.valueOf(index)))
                tag.putBoolean(String.valueOf(index), true);
            stack.getOrCreateTag().put("Filters", tag);
            PacketHandler.sendToServer(new AreaCardItemPacket(Minecraft.getInstance().player.getUUID(), stack));
        }
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
