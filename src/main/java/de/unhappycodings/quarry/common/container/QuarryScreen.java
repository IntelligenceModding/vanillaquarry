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
import de.unhappycodings.quarry.common.network.toserver.QuarryChangedPacket;
import de.unhappycodings.quarry.common.network.toserver.QuarryModePacket;
import de.unhappycodings.quarry.common.network.toserver.QuarryOwnerPacket;
import de.unhappycodings.quarry.common.network.toserver.QuarryPowerPacket;
import de.unhappycodings.quarry.common.network.toserver.QuarrySpeedPacket;
import de.unhappycodings.quarry.common.util.CalcUtil;
import de.unhappycodings.quarry.common.util.NbtUtil;
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
import net.minecraftforge.common.UsernameCache;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class QuarryScreen extends BaseScreen<QuarryContainer> {
    public static ModButton MODE_MOUSE_BUTTON;
    public static ModButton INFO_MOUSE_BUTTON;
    public static ModButton LOCK_MOUSE_BUTTON;
    QuarryContainer container;
    boolean modeButtonIsHovered;
    boolean infoButtonIsHovered;
    boolean lockButtonIsHovered;

    public QuarryScreen(QuarryContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        this.container = screenContainer;
    }

    @Override
    protected void renderLabels(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY) {
        drawText(new TranslatableComponent("block.quarry.quarry_block").getString(), pPoseStack, 71, 7);
        drawText(new TranslatableComponent("gui.quarry.quarry.text.inventory").getString(), pPoseStack, 8, 110);
        drawText(new TranslatableComponent("gui.quarry.quarry.text.speed").getString(), pPoseStack, 73, 27);
        drawText(new TranslatableComponent("gui.quarry.quarry.text.fuel").getString(), pPoseStack, 19, 20);
        drawText(new TranslatableComponent("gui.quarry.quarry.text.out").getString(), pPoseStack, 138, 20);
        drawText(new TextComponent(getMenu().getTile().getSpeed() + 1 + "").getString(), pPoseStack, 85, 41);
        String yCoord = new TranslatableComponent("gui.quarry.quarry.text.stop").getString();
        ItemStack itemStack = getMenu().getItems().get(getMenu().getItems().size() - 1);
        if (itemStack.getItem() instanceof AreaCardItem && NbtUtil.getNbtTag(itemStack).contains("currentY")) {
            yCoord = String.valueOf(NbtUtil.getNbtTag(itemStack).getInt("currentY"));
        }

        drawText(new TextComponent("Y: " + yCoord).getString(), pPoseStack, 73, 95);
        drawText(new TranslatableComponent("gui.quarry.quarry.power.on").getString(), pPoseStack, 68, 59);
        drawText(new TranslatableComponent("gui.quarry.quarry.power.off").getString(), pPoseStack, 95, 59);

        switch (getMenu().getTile().getMode()) {
            case 0 -> drawCenteredText(new TranslatableComponent("gui.quarry.quarry.mode.default").getString(), pPoseStack, 87, 77);
            case 1 -> drawCenteredText(new TranslatableComponent("gui.quarry.quarry.mode.efficient").getString(), pPoseStack, 87, 77);
            case 2 -> drawCenteredText(new TranslatableComponent("gui.quarry.quarry.mode.fortune").getString(), pPoseStack, 87, 77);
            case 3 -> drawCenteredText(new TranslatableComponent("gui.quarry.quarry.mode.silktouch").getString(), pPoseStack, 87, 77);
            case 4 -> drawCenteredText(new TranslatableComponent("gui.quarry.quarry.mode.void").getString(), pPoseStack, 87, 77);
        }

        if (modeButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            float totalBurnTicks = CalcUtil.getNeededTicks(getMenu().getTile().getMode(), getMenu().getTile().getSpeed());
            switch (getMenu().getTile().getMode()) {
                case 0 -> list.add(new TranslatableComponent("gui.quarry.quarry.mode.default"));
                case 1 -> list.add(new TranslatableComponent("gui.quarry.quarry.mode.efficient"));
                case 2 -> list.add(new TranslatableComponent("gui.quarry.quarry.mode.fortune"));
                case 3 -> list.add(new TranslatableComponent("gui.quarry.quarry.mode.silktouch"));
                default -> list.add(new TranslatableComponent("gui.quarry.quarry.mode.void"));
            }
            list.add(new TranslatableComponent("gui.quarry.quarry.tooltip.consumption")
                    .append(" " + totalBurnTicks + " ")
                    .append("ticks").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(new TranslatableComponent("gui.quarry.quarry.tooltip.coal").append(" " + ( new DecimalFormat("##.##").format(1600/totalBurnTicks).replace(",", ".")) + " ")
                    .append(new TranslatableComponent("gui.quarry.quarry.tooltip.blocks")).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            if (getMenu().getTile().getMode() == 1)
                list.add(new TranslatableComponent("gui.quarry.quarry.tooltip.speed.80").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            this.renderComponentTooltip(pPoseStack, list, pMouseX - leftPos, pMouseY - topPos);
        }

        if (infoButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            list.add(new TranslatableComponent("gui.quarry.quarry.tooltip.informations"));
            list.add(new TextComponent(""));
            list.add(new TextComponent("#" + getBurnTime() + "/" + getTotalBurnTime() + " ticks").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(new TextComponent(""));
            list.add(new TranslatableComponent("gui.quarry.quarry.tooltip.when_turned_off").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(new TextComponent(new TranslatableComponent("gui.quarry.quarry.tooltip.will_consume").getString().replace("#", CommonConfig.quarryIdleConsumption.get().toString())).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(new TextComponent("").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(new TranslatableComponent("gui.quarry.quarry.tooltip.changing_speed").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(new TranslatableComponent("gui.quarry.quarry.tooltip.affect_fuel").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(new TextComponent("").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(new TranslatableComponent("gui.quarry.quarry.tooltip.use_config").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).withItalic(true)));
            this.renderComponentTooltip(pPoseStack, list, pMouseX - leftPos, pMouseY - topPos);
        }
        if (lockButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            String owner = "undefined";
            if (!Objects.equals(getMenu().getTile().getOwner(), "undefined")) owner = UsernameCache.getLastKnownUsername(UUID.fromString(getMenu().getTile().getOwner()));

            if (getMenu().getTile().getLocked()) {
                list.add(new TranslatableComponent("gui.quarry.quarry.lock.private"));
                list.add(new TranslatableComponent("gui.quarry.quarry.lock.private.description").withStyle(ChatFormatting.YELLOW));
                list.add(new TranslatableComponent("gui.quarry.quarry.lock.owner", owner) .withStyle(ChatFormatting.YELLOW));
            } else {
                list.add(new TranslatableComponent("gui.quarry.quarry.lock.public"));
                list.add(new TranslatableComponent("gui.quarry.quarry.lock.public.description").withStyle(ChatFormatting.YELLOW));
                list.add(new TranslatableComponent("gui.quarry.quarry.lock.owner", owner) .withStyle(ChatFormatting.YELLOW));
            }
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

        // render burn tick process
        int height = getLitProgress();
        this.blit(matrixStack, leftPos + 23, topPos + 86 + 13 - height, 176, 13 - height, 14, height + 1);

        // render power dot indicators
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
        boolean darkmode = getDarkModeConfigValue();
        boolean locked = tile.getLocked();
        INFO_MOUSE_BUTTON = new ModButton(161, 6, 9, 9, Quarry.INFO, null, null, tile, this, 9, 18, false);
        LOCK_MOUSE_BUTTON = new ModButton(6, 6, 9, 9, locked ? Quarry.LOCK : Quarry.LOCK_OPEN, this::cycleLocked, null, tile, this, 9, 18, true);
        addRenderableWidget(new ModButton(150, 6, 9, 9, darkmode ? Quarry.DARK_MODE : Quarry.WHITE_MODE, () -> {refreshWidgets(); setDarkModeConfigValue(!getDarkModeConfigValue());}, null, tile, this, 9, 18, true));

        MODE_MOUSE_BUTTON = new ModButton(56, 74, 64, 14, darkmode ? Quarry.MODE_DARK : Quarry.MODE, () -> changeMode(false), () -> changeMode(true), tile, this, 64, 28, true);
        addRenderableWidget(new ModButton(69, 38, 10, 14, darkmode ? Quarry.COUNTER_DOWN_DARK : Quarry.COUNTER_DOWN, () -> changeSpeed((byte) -1, tile), null, tile, this, 10, 28, true));
        addRenderableWidget(new ModButton(95, 38, 10, 14, darkmode ? Quarry.COUNTER_UP_DARK : Quarry.COUNTER_UP, () -> changeSpeed((byte) 1, tile), null, tile, this, 10, 28, true));
        addRenderableWidget(new ModButton(61, 56, 25, 14, darkmode ? Quarry.POWER_DARK : Quarry.POWER, () -> changePower(true, tile), null, tile, this, 25, 28, true));
        addRenderableWidget(new ModButton(90, 56, 25, 14, darkmode ? Quarry.POWER_DARK : Quarry.POWER, () -> changePower(false, tile), null, tile, this, 25, 28, true));

        addRenderableWidget(MODE_MOUSE_BUTTON);
        addRenderableWidget(INFO_MOUSE_BUTTON);
        addRenderableWidget(LOCK_MOUSE_BUTTON);
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
        if (LOCK_MOUSE_BUTTON != null && LOCK_MOUSE_BUTTON.isMouseOver(pMouseX, pMouseY)) {
            lockButtonIsHovered = true;
        } else {
            if (lockButtonIsHovered) lockButtonIsHovered = false;
        }
        return super.isHovering(pX, pY, pWidth, pHeight, pMouseX, pMouseY);
    }

    @Override
    protected void containerTick() {
        refreshDarkmode();
        super.containerTick();
    }

    public void setDarkModeConfigValue(boolean state) {
        ClientConfig.enableQuarryDarkmode.set(state);
    }

    public boolean getDarkModeConfigValue() {
        return ClientConfig.enableQuarryDarkmode.get();
    }

    public void refreshDarkmode() {
        refreshWidgets();
    }

    public void sendChangedPacket() {
        PacketHandler.sendToServer(new QuarryChangedPacket(null, 0, this.getMenu().getTile().getBlockPos()));
    }

    public void cycleLocked() {
        QuarryBlockEntity entity = this.getMenu().getTile();
        if (Objects.equals(((QuarryBlockEntity) entity.getLevel().getBlockEntity(entity.getBlockPos())).getOwner(), this.getMinecraft().player.getStringUUID()))
            PacketHandler.sendToServer(new QuarryOwnerPacket(this.getMenu().getTile().getBlockPos(), false));
        sendChangedPacket();
    }

    private void changeMode(boolean reverse) {
        PacketHandler.sendToServer(new QuarryModePacket(this.getMenu().getTile().getBlockPos(), reverse ? 10 : 1));
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
        if (!getDarkModeConfigValue()) refreshDarkmode();
        String texture = "textures/gui/quarry_gui.png";
        if (getDarkModeConfigValue()) texture = "textures/gui/quarry_gui_dark.png";
        return new ResourceLocation(Quarry.MOD_ID, texture);
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
