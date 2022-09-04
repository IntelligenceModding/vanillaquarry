package de.unhappycodings.vanillaquarry.common.container;

import com.mojang.blaze3d.vertex.PoseStack;
import de.unhappycodings.vanillaquarry.VanillaQuarry;
import de.unhappycodings.vanillaquarry.client.gui.widgets.ModButton;
import de.unhappycodings.vanillaquarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.vanillaquarry.common.blocks.QuarryBlock;
import de.unhappycodings.vanillaquarry.common.container.base.BaseScreen;
import de.unhappycodings.vanillaquarry.common.container.base.BaseSlot;
import de.unhappycodings.vanillaquarry.common.item.AreaCardItem;
import de.unhappycodings.vanillaquarry.common.network.PacketHandler;
import de.unhappycodings.vanillaquarry.common.network.toserver.QuarryChangedPacket;
import de.unhappycodings.vanillaquarry.common.network.toserver.QuarryModePacket;
import de.unhappycodings.vanillaquarry.common.network.toserver.QuarryPowerPacket;
import de.unhappycodings.vanillaquarry.common.network.toserver.QuarrySpeedPacket;
import de.unhappycodings.vanillaquarry.common.util.CalcUtil;
import de.unhappycodings.vanillaquarry.common.util.NbtUtil;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class QuarryScreen extends BaseScreen<QuarryContainer> {
    public static ModButton MODE_MOUSE_BUTTON;
    public static ModButton INFO_MOUSE_BUTTON;
    QuarryContainer container;
    boolean modeButtonIsHovered;
    boolean infoButtonIsHovered;
    boolean darkmode;

    public QuarryScreen(QuarryContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        this.container = screenContainer;
    }

    @Override
    protected void renderLabels(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY) {
        drawText(new TranslatableComponent("block.vanillaquarry.quarry_block").getString(), pPoseStack, 71, 7);
        drawText(new TranslatableComponent("gui.vanillaquarry.quarry.text.inventory").getString(), pPoseStack, 8, 110);
        drawText(new TranslatableComponent("gui.vanillaquarry.quarry.text.speed").getString(), pPoseStack, 73, 27);
        drawText(new TranslatableComponent("gui.vanillaquarry.quarry.text.fuel").getString(), pPoseStack, 19, 20);
        drawText(new TranslatableComponent("gui.vanillaquarry.quarry.text.out").getString(), pPoseStack, 138, 20);
        drawText(new TextComponent(getMenu().getTile().getSpeed() + 1 + "").getString(), pPoseStack, 85, 41);
        String yCoord = "stop";
        ItemStack itemStack = getMenu().getItems().get(getMenu().getItems().size() - 1);
        if (itemStack.getItem() instanceof AreaCardItem && NbtUtil.getNbtTag(itemStack).contains("currentY")) {
            yCoord = String.valueOf(NbtUtil.getNbtTag(itemStack).getInt("currentY"));
        }

        drawText(new TextComponent("Y: " + yCoord).getString(), pPoseStack, 73, 95);
        drawText(new TranslatableComponent("gui.vanillaquarry.quarry.power.on").getString(), pPoseStack, 68, 59);
        drawText(new TranslatableComponent("gui.vanillaquarry.quarry.power.off").getString(), pPoseStack, 95, 59);

        switch (getMenu().getTile().getMode()) {
            case 0 ->
                    drawCenteredText(new TranslatableComponent("gui.vanillaquarry.quarry.mode.default").getString(), pPoseStack, 87, 77);
            case 1 ->
                    drawCenteredText(new TranslatableComponent("gui.vanillaquarry.quarry.mode.efficient").getString(), pPoseStack, 87, 77);
            case 2 ->
                    drawCenteredText(new TranslatableComponent("gui.vanillaquarry.quarry.mode.fortune").getString(), pPoseStack, 87, 77);
            case 3 ->
                    drawCenteredText(new TranslatableComponent("gui.vanillaquarry.quarry.mode.silktouch").getString(), pPoseStack, 87, 77);
            case 4 ->
                    drawCenteredText(new TranslatableComponent("gui.vanillaquarry.quarry.mode.void").getString(), pPoseStack, 87, 77);
        }

        if (modeButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            float totalBurnTicks = CalcUtil.getNeededTicks(getMenu().getTile().getMode(), getMenu().getTile().getSpeed());
            switch (getMenu().getTile().getMode()) {
                case 0 -> list.add(new TranslatableComponent("gui.vanillaquarry.quarry.mode.default"));
                case 1 -> list.add(new TranslatableComponent("gui.vanillaquarry.quarry.mode.efficient"));
                case 2 -> list.add(new TranslatableComponent("gui.vanillaquarry.quarry.mode.fortune"));
                case 3 -> list.add(new TranslatableComponent("gui.vanillaquarry.quarry.mode.silktouch"));
                default -> list.add(new TranslatableComponent("gui.vanillaquarry.quarry.mode.void"));
            }
            list.add(new TranslatableComponent("gui.vanillaquarry.quarry.tooltip.consumption")
                    .append(" " + totalBurnTicks + " ")
                    .append("ticks").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(new TranslatableComponent("gui.vanillaquarry.quarry.tooltip.coal").append(" " + ( new DecimalFormat("##.##").format(1600/totalBurnTicks).replace(",", ".")) + " ")
                    .append(new TranslatableComponent("gui.vanillaquarry.quarry.tooltip.blocks")).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            if (getMenu().getTile().getMode() == 1)
                list.add(new TranslatableComponent("gui.vanillaquarry.quarry.tooltip.speed.80").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            this.renderComponentTooltip(pPoseStack, list, pMouseX - leftPos, pMouseY - topPos);
        }

        if (infoButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            list.add(new TextComponent("Informations"));
            list.add(new TextComponent(""));
            list.add(new TextComponent("#" + getBurnTime() + "/" + getTotalBurnTime() + " ticks").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(new TextComponent(""));
            list.add(new TextComponent("When turned off, the quarry").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(new TextComponent("will consume 1 BurnTick per second.").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(new TextComponent("").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(new TextComponent("Changing the speed does not currently").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(new TextComponent("affect the fuel consumption!").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            this.renderComponentTooltip(pPoseStack, list, pMouseX - leftPos, pMouseY - topPos);
        }
    }

    @Override
    public void onClose() {
        this.getMenu().getTile().setChanged();
        super.onClose();
    }

    @Override
    protected void init() {
        super.init();
        addElements();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void renderBg(@NotNull PoseStack matrixStack, float partialTicks, int x, int y) {
        super.renderBg(matrixStack, partialTicks, x, y);

        Level level = this.getMenu().getTile().getLevel();
        BlockPos pos = this.getMenu().getTile().getBlockPos();

        int height = getLitProgress();
        this.blit(matrixStack, leftPos + 23, topPos + 86 + 13 - height, 176, 13 - height, 14, height + 1);

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
        System.out.println("elements");
        QuarryBlockEntity tile = this.getMenu().getTile();
        MODE_MOUSE_BUTTON = new ModButton(56, 74, 64, 14, darkmode ? VanillaQuarry.MODE_DARK : VanillaQuarry.MODE, this::changeMode, tile, this, 64, 28, true);
        INFO_MOUSE_BUTTON = new ModButton(161, 6, 9, 9, VanillaQuarry.INFO, null, tile, this, 9, 18, false);
        addRenderableWidget(new ModButton(69, 38, 10, 14, darkmode ? VanillaQuarry.COUNTER_DOWN_DARK : VanillaQuarry.COUNTER_DOWN, () -> changeSpeed((byte) -1, tile), tile, this, 10, 28, true));
        addRenderableWidget(new ModButton(95, 38, 10, 14, darkmode ? VanillaQuarry.COUNTER_UP_DARK : VanillaQuarry.COUNTER_UP, () -> changeSpeed((byte) 1, tile), tile, this, 10, 28, true));

        addRenderableWidget(new ModButton(61, 56, 25, 14, darkmode ? VanillaQuarry.POWER_DARK : VanillaQuarry.POWER, () -> changePower(true, tile), tile, this, 25, 28, true));
        addRenderableWidget(new ModButton(90, 56, 25, 14, darkmode ? VanillaQuarry.POWER_DARK : VanillaQuarry.POWER, () -> changePower(false, tile), tile, this, 25, 28, true));

        addRenderableWidget(new ModButton(146, 7, 12, 8, darkmode ? VanillaQuarry.DARK_MODE : VanillaQuarry.WHITE_MODE, () -> {darkmode = !darkmode; refreshWidgets();}, tile, this, 12, 16, true));

        addRenderableWidget(MODE_MOUSE_BUTTON);
        addRenderableWidget(INFO_MOUSE_BUTTON);
    }

    @Override
    protected void slotClicked(@NotNull Slot pSlot, int pSlotId, int pMouseButton, @NotNull ClickType pType) {
        super.slotClicked(pSlot, pSlotId, pMouseButton, pType);
        sendChangedPacket();
        if (pSlotId == 48) {
            PacketHandler.sendToServer(new QuarryChangedPacket(pSlot.getItem(), 1, this.getMenu().getTile().getBlockPos()));
        }
    }

    @Override
    protected boolean isHovering(int pX, int pY, int pWidth, int pHeight, double pMouseX, double pMouseY) {
        if (MODE_MOUSE_BUTTON != null && MODE_MOUSE_BUTTON.isMouseOver(pMouseX, pMouseY)) {
            modeButtonIsHovered = true;
        } else {
            if (modeButtonIsHovered) modeButtonIsHovered = false;
        }
        if (INFO_MOUSE_BUTTON != null && INFO_MOUSE_BUTTON.isMouseOver(pMouseX, pMouseY)) {
            infoButtonIsHovered = true;
        } else {
            if (infoButtonIsHovered) infoButtonIsHovered = false;
        }
        return super.isHovering(pX, pY, pWidth, pHeight, pMouseX, pMouseY);
    }

    public void sendChangedPacket() {
        PacketHandler.sendToServer(new QuarryChangedPacket(null, 0, this.getMenu().getTile().getBlockPos()));
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

    public void refreshWidgets() {
        clearWidgets();
        addElements();
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
        String texture = "textures/gui/quarry_gui.png";
        if (darkmode) texture = "textures/gui/quarry_gui_dark.png";
        return new ResourceLocation(VanillaQuarry.MOD_ID, texture);
    }

    public void drawCenteredText(String text, PoseStack stack, int x, int y) {
        Minecraft.getInstance().font.draw(stack, text, x - (Minecraft.getInstance().font.width(text) / 2f), y, 1315860);
    }

    public void drawText(String text, PoseStack stack, int x, int y) {
        Minecraft.getInstance().font.draw(stack, text, x, y, 1315860);
    }

    public int getBurnTime() {
        return container.getTile().getBurnTime();
    }

    public int getTotalBurnTime() {
        return container.getTile().getTotalBurnTime();
    }

    public int getLitProgress() {
        int total = container.getTile().getTotalBurnTime();
        if (total == 0) {
            total = 1;
        }
        return (container.getTile().getBurnTime() * 13) / total;
    }
}
