package de.unhappycodings.vanillaquarry.common.container;

import com.mojang.blaze3d.vertex.PoseStack;
import de.unhappycodings.vanillaquarry.VanillaQuarry;
import de.unhappycodings.vanillaquarry.client.config.ClientConfig;
import de.unhappycodings.vanillaquarry.client.gui.widgets.ModButton;
import de.unhappycodings.vanillaquarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.vanillaquarry.common.blocks.QuarryBlock;
import de.unhappycodings.vanillaquarry.common.config.CommonConfig;
import de.unhappycodings.vanillaquarry.common.container.base.BaseScreen;
import de.unhappycodings.vanillaquarry.common.container.base.BaseSlot;
import de.unhappycodings.vanillaquarry.common.item.AreaCardItem;
import de.unhappycodings.vanillaquarry.common.network.PacketHandler;
import de.unhappycodings.vanillaquarry.common.network.toserver.QuarryChangedPacket;
import de.unhappycodings.vanillaquarry.common.network.toserver.QuarryModePacket;
import de.unhappycodings.vanillaquarry.common.network.toserver.QuarryOwnerPacket;
import de.unhappycodings.vanillaquarry.common.network.toserver.QuarryPowerPacket;
import de.unhappycodings.vanillaquarry.common.network.toserver.QuarrySpeedPacket;
import de.unhappycodings.vanillaquarry.common.util.CalcUtil;
import de.unhappycodings.vanillaquarry.common.util.NbtUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
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
        drawText(Component.translatable("block.vanillaquarry.quarry_block").getString(), pPoseStack, 71, 7);
        drawText(Component.translatable("gui.vanillaquarry.quarry.text.inventory").getString(), pPoseStack, 8, 110);
        drawText(Component.translatable("gui.vanillaquarry.quarry.text.speed").getString(), pPoseStack, 73, 27);
        drawText(Component.translatable("gui.vanillaquarry.quarry.text.fuel").getString(), pPoseStack, 19, 20);
        drawText(Component.translatable("gui.vanillaquarry.quarry.text.out").getString(), pPoseStack, 138, 20);
        drawText(Component.literal(getMenu().getTile().getSpeed() + 1 + "").getString(), pPoseStack, 85, 41);
        String yCoord = Component.translatable("gui.vanillaquarry.quarry.text.stop").getString();
        ItemStack itemStack = getMenu().getItems().get(getMenu().getItems().size() - 1);
        if (itemStack.getItem() instanceof AreaCardItem && NbtUtil.getNbtTag(itemStack).contains("currentY")) {
            yCoord = String.valueOf(NbtUtil.getNbtTag(itemStack).getInt("currentY"));
        }

        drawText(Component.literal("Y: " + yCoord).getString(), pPoseStack, 73, 95);
        drawText(Component.translatable("gui.vanillaquarry.quarry.power.on").getString(), pPoseStack, 68, 59);
        drawText(Component.translatable("gui.vanillaquarry.quarry.power.off").getString(), pPoseStack, 95, 59);

        switch (getMenu().getTile().getMode()) {
            case 0 ->
                    drawCenteredText(Component.translatable("gui.vanillaquarry.quarry.mode.default").getString(), pPoseStack, 87, 77);
            case 1 ->
                    drawCenteredText(Component.translatable("gui.vanillaquarry.quarry.mode.efficient").getString(), pPoseStack, 87, 77);
            case 2 ->
                    drawCenteredText(Component.translatable("gui.vanillaquarry.quarry.mode.fortune").getString(), pPoseStack, 87, 77);
            case 3 ->
                    drawCenteredText(Component.translatable("gui.vanillaquarry.quarry.mode.silktouch").getString(), pPoseStack, 87, 77);
            case 4 ->
                    drawCenteredText(Component.translatable("gui.vanillaquarry.quarry.mode.void").getString(), pPoseStack, 87, 77);
        }

        if (modeButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            float totalBurnTicks = CalcUtil.getNeededTicks(getMenu().getTile().getMode(), getMenu().getTile().getSpeed());
            switch (getMenu().getTile().getMode()) {
                case 0 -> list.add(Component.translatable("gui.vanillaquarry.quarry.mode.default"));
                case 1 -> list.add(Component.translatable("gui.vanillaquarry.quarry.mode.efficient"));
                case 2 -> list.add(Component.translatable("gui.vanillaquarry.quarry.mode.fortune"));
                case 3 -> list.add(Component.translatable("gui.vanillaquarry.quarry.mode.silktouch"));
                default -> list.add(Component.translatable("gui.vanillaquarry.quarry.mode.void"));
            }
            list.add(Component.translatable("gui.vanillaquarry.quarry.tooltip.consumption").append(" " + totalBurnTicks + " ").append("ticks").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.translatable("gui.vanillaquarry.quarry.tooltip.coal").append(" " + (new DecimalFormat("##.##").format(1600 / totalBurnTicks).replace(",", ".")) + " ").append(Component.translatable("gui.vanillaquarry.quarry.tooltip.blocks")).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            if (getMenu().getTile().getMode() == 1)
                list.add(Component.translatable("gui.vanillaquarry.quarry.tooltip.speed.80").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            this.renderComponentTooltip(pPoseStack, list, pMouseX - leftPos, pMouseY - topPos);
        }

        if (infoButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("gui.vanillaquarry.quarry.tooltip.informations"));
            list.add(Component.literal(""));
            list.add(Component.literal("#" + getBurnTime() + "/" + getTotalBurnTime() + " ticks").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.literal(""));
            list.add(Component.translatable("gui.vanillaquarry.quarry.tooltip.when_turned_off").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.literal(Component.translatable("gui.vanillaquarry.quarry.tooltip.will_consume").getString().replace("#", CommonConfig.quarryIdleConsumption.get().toString())).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.literal("").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.translatable("gui.vanillaquarry.quarry.tooltip.changing_speed").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.translatable("gui.vanillaquarry.quarry.tooltip.affect_fuel").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.literal("").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.translatable("gui.vanillaquarry.quarry.tooltip.use_config").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).withItalic(true)));
            this.renderComponentTooltip(pPoseStack, list, pMouseX - leftPos, pMouseY - topPos);
        }
        if (lockButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            String owner = "undefined";
            if (!Objects.equals(getMenu().getTile().getOwner(), "undefined"))
                owner = UsernameCache.getLastKnownUsername(UUID.fromString(getMenu().getTile().getOwner()));

            if (getMenu().getTile().getLocked()) {
                list.add(Component.translatable("gui.vanillaquarry.quarry.lock.private"));
                list.add(Component.translatable("gui.vanillaquarry.quarry.lock.private.description").withStyle(ChatFormatting.YELLOW));
                list.add(Component.translatable("gui.vanillaquarry.quarry.lock.owner", owner).withStyle(ChatFormatting.YELLOW));
            } else {
                list.add(Component.translatable("gui.vanillaquarry.quarry.lock.public"));
                list.add(Component.translatable("gui.vanillaquarry.quarry.lock.public.description").withStyle(ChatFormatting.YELLOW));
                list.add(Component.translatable("gui.vanillaquarry.quarry.lock.owner", owner).withStyle(ChatFormatting.YELLOW));
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
        MODE_MOUSE_BUTTON = new  ModButton(56, 74, 64, 14, darkmode ? VanillaQuarry.MODE_DARK : VanillaQuarry.MODE, () -> changeMode(false), () -> changeMode(true), tile, this, 64, 28, true);
        INFO_MOUSE_BUTTON = new ModButton(161, 6, 9, 9, VanillaQuarry.INFO, null, null, tile, this, 9, 18, false);
        LOCK_MOUSE_BUTTON = new ModButton(10, 5, 8, 10, darkmode ? (locked ? VanillaQuarry.LOCK_DARK : VanillaQuarry.LOCK_DARK_OPEN) : (locked ? VanillaQuarry.LOCK : VanillaQuarry.LOCK_OPEN), this::cycleLocked, null, tile, this, 8, 20, true);
        if (ClientConfig.enableEnableQuarryDarkmodeButton.get())
            addRenderableWidget(new ModButton(146, 7, 12, 8, darkmode ? VanillaQuarry.DARK_MODE : VanillaQuarry.WHITE_MODE, () -> {
                refreshWidgets();
                setDarkModeConfigValue(!getDarkModeConfigValue());
            }, null, tile, this, 12, 16, true));
        addRenderableWidget(new ModButton(69, 38, 10, 14, darkmode ? VanillaQuarry.COUNTER_DOWN_DARK : VanillaQuarry.COUNTER_DOWN, () -> changeSpeed((byte) -1, tile), null, tile, this, 10, 28, true));
        addRenderableWidget(new ModButton(95, 38, 10, 14, darkmode ? VanillaQuarry.COUNTER_UP_DARK : VanillaQuarry.COUNTER_UP, () -> changeSpeed((byte) 1, tile), null, tile, this, 10, 28, true));
        addRenderableWidget(new ModButton(61, 56, 25, 14, darkmode ? VanillaQuarry.POWER_DARK : VanillaQuarry.POWER, () -> changePower(true, tile), null, tile, this, 25, 28, true));
        addRenderableWidget(new ModButton(90, 56, 25, 14, darkmode ? VanillaQuarry.POWER_DARK : VanillaQuarry.POWER, () -> changePower(false, tile), null, tile, this, 25, 28, true));

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

    public boolean getDarkModeConfigValue() {
        return ClientConfig.enableQuarryDarkmode.get();
    }

    public void setDarkModeConfigValue(boolean state) {
        ClientConfig.enableQuarryDarkmode.set(state);
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
