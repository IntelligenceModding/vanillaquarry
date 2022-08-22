package de.unhappycodings.vanillaquarry.common.container;

import com.mojang.blaze3d.vertex.PoseStack;
import de.unhappycodings.vanillaquarry.VanillaQuarry;
import de.unhappycodings.vanillaquarry.client.gui.widgets.ModButton;
import de.unhappycodings.vanillaquarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.vanillaquarry.common.blocks.QuarryBlock;
import de.unhappycodings.vanillaquarry.common.container.base.BaseScreen;
import de.unhappycodings.vanillaquarry.common.container.base.BaseSlot;
import de.unhappycodings.vanillaquarry.common.network.PacketHandler;
import de.unhappycodings.vanillaquarry.common.network.toserver.QuarryChangedPacket;
import de.unhappycodings.vanillaquarry.common.network.toserver.QuarryModePacket;
import de.unhappycodings.vanillaquarry.common.network.toserver.QuarryPowerPacket;
import de.unhappycodings.vanillaquarry.common.network.toserver.QuarrySpeedPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class QuarryScreen extends BaseScreen<QuarryContainer> {
    QuarryContainer container;
    public QuarryScreen(QuarryContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        this.container = screenContainer;
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        drawText(new TranslatableComponent("block.vanillaquarry.quarry_block").getString(), pPoseStack, 71, 7);
        drawText(new TextComponent("Inventory").getString(), pPoseStack, 8, 110);
        drawText(new TextComponent("Speed").getString(), pPoseStack, 73, 27);
        drawText(new TextComponent("Fuel").getString(), pPoseStack, 19, 20);
        drawText(new TextComponent("Out").getString(), pPoseStack, 138, 20);
        drawText(new TextComponent(getMenu().getTile().getSpeed() + 1 + "").getString(), pPoseStack, 85, 41);
        drawText(new TextComponent("Y:" + "stop").getString(), pPoseStack, 73, 95);

        drawText(new TextComponent("On").getString(), pPoseStack, 68, 59);
        drawText(new TextComponent("Off").getString(), pPoseStack, 95, 59);

        switch (getMenu().getTile().getMode()) {
            case 0: drawCenteredText(new TextComponent("Default").getString(), pPoseStack, 87, 77); break;
            case 1: drawCenteredText(new TextComponent("Efficient").getString(), pPoseStack, 87, 77); break;
            case 2: drawCenteredText(new TextComponent("Fortune").getString(), pPoseStack, 87, 77); break;
            case 3: drawCenteredText(new TextComponent("Silktouch").getString(), pPoseStack, 87, 77); break;
            case 4: drawCenteredText(new TextComponent("Void").getString(), pPoseStack, 87, 77);
        }

    }

    @Override
    protected void init() {
        super.init();
        addElements();
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrixStack, float partialTicks, int x, int y) {
        super.renderBg(matrixStack, partialTicks, x, y);
        Level level = this.getMenu().getTile().getLevel();
        BlockPos pos = this.getMenu().getTile().getBlockPos();
        //Render Status Dot and Fire
        if (level.getBlockState(pos).getValue(QuarryBlock.POWERED))
            blit(matrixStack, leftPos + 23, topPos + 86, 176, 0, 14, 14); // fire
        if (level.getBlockState(pos).getValue(QuarryBlock.ACTIVE)) {
            blit(matrixStack, leftPos + 63, topPos + 96, 176, 14, 5, 5); // green
        } else {
            blit(matrixStack, leftPos + 63, topPos + 96, 182, 14, 5, 5); // red
        }
        // Render slots
        for (Slot slot : container.slots) {
            if (slot instanceof BaseSlot baseSlot) {
                baseSlot.renderGhostOverlay(matrixStack, getGuiLeft(), getGuiTop());
            }
        }
    }

    protected void addElements() {
        QuarryBlockEntity tile = this.getMenu().getTile();
        addRenderableWidget(new ModButton(69, 38, 10, 14, VanillaQuarry.COUNTER_DOWN, () -> changeSpeed((byte) -1, tile), tile, this, 10, 28));
        addRenderableWidget(new ModButton(95, 38, 10, 14, VanillaQuarry.COUNTER_UP, () -> changeSpeed((byte) 1, tile), tile, this, 10 ,28));

        addRenderableWidget(new ModButton(61, 56, 25, 14, VanillaQuarry.POWER, () -> changePower(true, tile), tile, this, 25, 28));
        addRenderableWidget(new ModButton(90, 56, 25, 14, VanillaQuarry.POWER, () -> changePower(false, tile), tile, this, 25, 28));

        TextComponent hoverComponent = switch (tile.getMode()) {
            case 0 -> new TextComponent("Default mining. Nothing special. 16B/coal"); // Default
            case 1 -> new TextComponent("Mines with a higher efficiency, but 0.25x slower. 20B/coal"); // Efficient
            case 2 -> new TextComponent("Doubles and tripples all mined block. 10B/coal"); // Fortune
            case 3 -> new TextComponent("Breaks all blocks with fineness. 10B/coal"); // Silktouch
            default -> new TextComponent("Default mining, but voids all mined Blocks. 15B/coal"); // Void
        };
        addRenderableWidget(new ModButton(56, 74, 64, 14, VanillaQuarry.MODE, () -> changeMode(), tile, this, 64, 28).addHoverText(() -> new TextComponent("Hallo")));
    }

    @Override
    protected void slotClicked(@NotNull Slot pSlot, int pSlotId, int pMouseButton, @NotNull ClickType pType) {
        super.slotClicked(pSlot, pSlotId, pMouseButton, pType);
        sendChangedPacket();
    }

    public void sendChangedPacket() {
        PacketHandler.sendToServer(new QuarryChangedPacket(this.getMenu().getTile().getBlockPos()));
    }

    private void changeMode() {
        PacketHandler.sendToServer(new QuarryModePacket(this.getMenu().getTile().getBlockPos(), 1));
        sendChangedPacket();
    }

    private void changeSpeed(byte state, QuarryBlockEntity tile) {
        PacketHandler.sendToServer(new QuarrySpeedPacket(tile.getBlockPos(), state));
        sendChangedPacket();
    }

    private void changePower(boolean state, QuarryBlockEntity tile) {
        PacketHandler.sendToServer(new QuarryPowerPacket(tile.getBlockPos(), state));
        sendChangedPacket();
    }

    @Override
    public int getSizeX() {
        return 176;
    }

    @Override
    public int getSizeY() {
        return 204;
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation(VanillaQuarry.MOD_ID, "textures/gui/quarry_gui.png");
    }

    public void drawCenteredText(String text, PoseStack stack, int x, int y) {
        Minecraft.getInstance().font.draw(stack, text, x - (Minecraft.getInstance().font.width(text) / 2), y, 4210752);
    }

    public void drawText(String text, PoseStack stack, int x, int y) {
        Minecraft.getInstance().font.draw(stack, text, x, y, 4210752);
    }
}
