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
import de.unhappycodings.quarry.common.container.base.SlotInputHandler;
import de.unhappycodings.quarry.common.item.AreaCardItem;
import de.unhappycodings.quarry.common.network.PacketHandler;
import de.unhappycodings.quarry.common.network.toserver.*;
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
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuarryScreen extends BaseScreen<QuarryContainer> {
    public static ModButton modeMouseButton;
    public static ModButton infoMouseButton;
    public static ModButton darkmodeMouseButton;
    public static ModButton lockMouseButton;
    public static ModButton loopMouseButton;
    public static ModButton filterMouseButton;
    public static ModButton ejectMouseButton;
    public static ModButton skipMouseButton;
    QuarryContainer container;
    boolean modeButtonIsHovered;
    boolean infoButtonIsHovered;
    boolean lockButtonIsHovered;
    boolean loopButtonIsHovered;
    boolean filterButtonIsHovered;
    boolean ejectButtonIsHovered;
    boolean darkmodeButtonIsHovered;
    boolean skipButtonIsHovered;

    public QuarryScreen(QuarryContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        this.container = screenContainer;
    }

    @Override
    protected void renderLabels(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY) {
        if (getSlotUnderMouse() != null && getSlotUnderMouse() instanceof SlotInputHandler) {
            if (getSlotUnderMouse().getContainerSlot() == 13 && !getSlotUnderMouse().hasItem()) {
                List<Component> list = new ArrayList<>();
                list.add(new TranslatableComponent("gui.quarry.replace"));
                list.add(new TranslatableComponent("gui.quarry.replace_1").withStyle(ChatFormatting.YELLOW));
                list.add(new TranslatableComponent("gui.quarry.replace_2").withStyle(ChatFormatting.YELLOW));
                list.add(new TextComponent(""));
                list.add(new TranslatableComponent("gui.quarry.replace_3").withStyle(ChatFormatting.YELLOW));

                this.renderComponentTooltip(pPoseStack, list, pMouseX - leftPos, pMouseY - topPos);
            }
        }
        if (!Objects.equals(getMenu().getTile().getOwner(), this.getMinecraft().player.getName().getString() + "@" + this.getMinecraft().player.getStringUUID()) && this.getMenu().getTile().getLocked()) {
            drawCenteredText(new TranslatableComponent("gui.quarry.admin").getString(), pPoseStack, getSizeX() / 2, -19, 11141120);
            drawCenteredText(new TranslatableComponent("gui.quarry.others").getString(), pPoseStack, getSizeX() / 2, -10, 11141120);
        }
        drawText(new TranslatableComponent("block.quarry.quarry_block").getString(), pPoseStack, 71, 7);
        drawText(new TranslatableComponent("gui.quarry.text.inventory").getString(), pPoseStack, 8, 110);
        drawText(new TranslatableComponent("gui.quarry.text.speed").getString(), pPoseStack, 73, 27);
        drawText(new TranslatableComponent("gui.quarry.text.fuel").getString(), pPoseStack, 19, 20);
        drawText(new TranslatableComponent("gui.quarry.text.out").getString(), pPoseStack, 138, 20);
        drawText(new TextComponent(getMenu().getTile().getSpeed() + 1 + "").getString(), pPoseStack, 85, 41);
        String yCoord = new TranslatableComponent("gui.quarry.text.stop").getString();
        ItemStack itemStack = getMenu().getItems().get(getMenu().getItems().size() - 1);
        if (itemStack.getItem() instanceof AreaCardItem && NbtUtil.getNbtTag(itemStack).contains("currentY") && getMenu().getTile().getLevel().getBlockState(getMenu().getTile().getBlockPos()).getValue(QuarryBlock.ACTIVE)) {
            yCoord = String.valueOf(NbtUtil.getNbtTag(itemStack).getInt("currentY"));
        }

        drawText(new TextComponent("Y: " + yCoord).getString(), pPoseStack, 73, 95);
        drawText(new TranslatableComponent("gui.quarry.power.on").getString(), pPoseStack, 68, 59);
        drawText(new TranslatableComponent("gui.quarry.power.off").getString(), pPoseStack, 95, 59);

        switch (getMenu().getTile().getMode()) {
            case 0 ->
                    drawCenteredText(new TranslatableComponent("gui.quarry.mode.default").getString(), pPoseStack, 87, 77);
            case 1 ->
                    drawCenteredText(new TranslatableComponent("gui.quarry.mode.efficient").getString(), pPoseStack, 87, 77);
            case 2 ->
                    drawCenteredText(new TranslatableComponent("gui.quarry.mode.fortune").getString(), pPoseStack, 87, 77);
            case 3 ->
                    drawCenteredText(new TranslatableComponent("gui.quarry.mode.silktouch").getString(), pPoseStack, 87, 77);
            case 4 ->
                    drawCenteredText(new TranslatableComponent("gui.quarry.mode.void").getString(), pPoseStack, 87, 77);
        }

        if (modeButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            float totalBurnTicks = CalcUtil.getNeededTicks(getMenu().getTile().getMode(), getMenu().getTile().getSpeed());
            switch (getMenu().getTile().getMode()) {
                case 0 -> list.add(new TranslatableComponent("gui.quarry.mode.default"));
                case 1 -> list.add(new TranslatableComponent("gui.quarry.mode.efficient"));
                case 2 -> list.add(new TranslatableComponent("gui.quarry.mode.fortune"));
                case 3 -> list.add(new TranslatableComponent("gui.quarry.mode.silktouch"));
                default -> list.add(new TranslatableComponent("gui.quarry.mode.void"));
            }
            list.add(new TranslatableComponent("gui.quarry.tooltip.consumption").append(" " + totalBurnTicks + " ").append("ticks").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(new TranslatableComponent("gui.quarry.tooltip.coal").append(" " + (new DecimalFormat("##.##").format(1600 / totalBurnTicks).replace(",", ".")) + " ").append(new TranslatableComponent("gui.quarry.tooltip.blocks")).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            if (getMenu().getTile().getMode() == 1)
                list.add(new TranslatableComponent("gui.quarry.tooltip.speed.80").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            this.renderComponentTooltip(pPoseStack, list, pMouseX - leftPos, pMouseY - topPos);
        }

        if (infoButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            list.add(new TranslatableComponent("gui.quarry.tooltip.informations"));
            list.add(new TextComponent(""));
            list.add(new TextComponent("#" + getBurnTime() + "/" + getTotalBurnTime() + " ticks").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(new TextComponent(""));
            list.add(new TranslatableComponent("gui.quarry.tooltip.when_turned_off").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(new TextComponent(new TranslatableComponent("gui.quarry.tooltip.will_consume").getString().replace("#", CommonConfig.quarryIdleConsumption.get().toString())).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(new TextComponent("").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(new TranslatableComponent("gui.quarry.tooltip.changing_speed").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(new TranslatableComponent("gui.quarry.tooltip.affect_fuel").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(new TextComponent("").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(new TranslatableComponent("gui.quarry.replacing").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(new TextComponent("").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(new TranslatableComponent("gui.quarry.tooltip.use_config").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).withItalic(true)));
            this.renderComponentTooltip(pPoseStack, list, pMouseX - leftPos, pMouseY - topPos);
        }
        if (lockButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            String owner = "undefined";
            String ownerString = getMenu().getTile().getOwner();
            if (!ownerString.isEmpty())
                owner = ownerString.replace("@", " (") + (ownerString.equals("undefined") ? "" : ")");

            if (getMenu().getTile().getLocked()) {
                list.add(new TranslatableComponent("gui.quarry.lock.private"));
                list.add(new TranslatableComponent("gui.quarry.lock.private.description").withStyle(ChatFormatting.YELLOW));
                list.add(new TranslatableComponent("gui.quarry.lock.owner", owner).withStyle(ChatFormatting.YELLOW));
            } else {
                list.add(new TranslatableComponent("gui.quarry.lock.public"));
                list.add(new TranslatableComponent("gui.quarry.lock.public.description").withStyle(ChatFormatting.YELLOW));
                list.add(new TranslatableComponent("gui.quarry.lock.owner", owner).withStyle(ChatFormatting.YELLOW));
            }
            this.renderComponentTooltip(pPoseStack, list, pMouseX - leftPos, pMouseY - topPos);
        }
        if (loopButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            if (getMenu().getTile().getLoop()) {
                list.add(new TranslatableComponent("gui.quarry.tooltip.loop.always"));
                list.add(new TranslatableComponent("gui.quarry.tooltip.loop.restart").withStyle(ChatFormatting.YELLOW));
            } else {
                list.add(new TranslatableComponent("gui.quarry.tooltip.loop.never"));
                list.add(new TranslatableComponent("gui.quarry.tooltip.loop.stop").withStyle(ChatFormatting.YELLOW));
            }
            this.renderComponentTooltip(pPoseStack, list, pMouseX - leftPos, pMouseY - topPos);
        }
        if (filterButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            if (getMenu().getTile().getFilter()) {
                list.add(new TranslatableComponent("gui.quarry.tooltip.filter.always"));
                list.add(new TranslatableComponent("gui.quarry.tooltip.filter.filters").withStyle(ChatFormatting.YELLOW));
            } else {
                list.add(new TranslatableComponent("gui.quarry.tooltip.filter.never"));
                list.add(new TranslatableComponent("gui.quarry.tooltip.filter.all").withStyle(ChatFormatting.YELLOW));
            }
            this.renderComponentTooltip(pPoseStack, list, pMouseX - leftPos, pMouseY - topPos);
        }
        if (ejectButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            if (getMenu().getTile().getEject() == 0) {
                list.add(new TranslatableComponent("gui.quarry.tooltip.output.dont"));
                list.add(new TranslatableComponent("gui.quarry.tooltip.output.in_out_hoppers").withStyle(ChatFormatting.YELLOW));
            } else if (getMenu().getTile().getEject() == 1) {
                list.add(new TranslatableComponent("gui.quarry.tooltip.output.pull"));
                list.add(new TranslatableComponent("gui.quarry.tooltip.output.pulls_above").withStyle(ChatFormatting.YELLOW));
                list.add(new TranslatableComponent("gui.quarry.tooltip.output.out_hoppers").withStyle(ChatFormatting.YELLOW));
            } else if (getMenu().getTile().getEject() == 2) {
                list.add(new TranslatableComponent("gui.quarry.tooltip.output.eject"));
                list.add(new TranslatableComponent("gui.quarry.tooltip.output.eject_below").withStyle(ChatFormatting.YELLOW));
                list.add(new TranslatableComponent("gui.quarry.tooltip.output.in_hoppers").withStyle(ChatFormatting.YELLOW));
            } else {
                list.add(new TranslatableComponent("gui.quarry.tooltip.output.both"));
                list.add(new TranslatableComponent("gui.quarry.tooltip.output.pulls_above").withStyle(ChatFormatting.YELLOW));
                list.add(new TranslatableComponent("gui.quarry.tooltip.output.eject_below").withStyle(ChatFormatting.YELLOW));
            }
            this.renderComponentTooltip(pPoseStack, list, pMouseX - leftPos, pMouseY - topPos);
        }
        if (skipButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            if (getMenu().getTile().getSkip()) {
                list.add(new TranslatableComponent("gui.quarry.skip.always"));
                list.add(new TranslatableComponent("gui.quarry.skip.skipped").withStyle(ChatFormatting.YELLOW));
            } else {
                list.add(new TranslatableComponent("gui.quarry.skip.never"));
                list.add(new TranslatableComponent("gui.quarry.skip.iterate").withStyle(ChatFormatting.YELLOW));
            }
            this.renderComponentTooltip(pPoseStack, list, pMouseX - leftPos, pMouseY - topPos);
        }
        if (darkmodeButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            if (getDarkModeConfigValue()) {
                list.add(new TranslatableComponent("gui.quarry.tooltip.darkmode.dark"));
                list.add(new TranslatableComponent("gui.quarry.tooltip.darkmode.dark.switch").withStyle(ChatFormatting.YELLOW));
            } else {
                list.add(new TranslatableComponent("gui.quarry.tooltip.darkmode.white"));
                list.add(new TranslatableComponent("gui.quarry.tooltip.darkmode.white.switch").withStyle(ChatFormatting.YELLOW));
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
        boolean loop = tile.getLoop();
        boolean filter = tile.getFilter();
        boolean skip = tile.getSkip();
        int eject = tile.getEject();
        infoMouseButton = new ModButton(161, 6, 9, 9, Quarry.INFO, null, null, tile, this, 9, 18, false);
        lockMouseButton = new ModButton(6, 6, 9, 9, locked ? Quarry.LOCK : Quarry.LOCK_OPEN, this::cycleLocked, null, tile, this, 9, 18, true);
        darkmodeMouseButton = new ModButton(150, 6, 9, 9, darkmode ? Quarry.DARK_MODE : Quarry.WHITE_MODE, () -> {
            refreshWidgets();
            setDarkModeConfigValue(!getDarkModeConfigValue());
        }, null, tile, this, 9, 18, true);
        loopMouseButton = new ModButton(17, 6, 9, 9, loop ? Quarry.LOOP : Quarry.LOOP_OFF, () -> cycleLoop(), null, tile, this, 9, 18, true);
        filterMouseButton = new ModButton(28, 6, 9, 9, filter ? Quarry.FILTER : Quarry.FILTER_OFF, () -> cycleFilter(true), null, tile, this, 9, 18, true);
        ejectMouseButton = new ModButton(39, 6, 9, 9, eject <= 1 ? (eject == 0 ? Quarry.EJECT_OFF : Quarry.EJECT_IN) : (eject == 2 ? Quarry.EJECT_OUT : Quarry.EJECT_ALL), () -> changeEject((byte) 1), null, tile, this, 9, 18, true);
        skipMouseButton = new ModButton(50, 6, 9, 9, skip ? Quarry.SKIP : Quarry.SKIP_OFF, () -> cycleSkip(), null, tile, this, 9, 18, true);

        modeMouseButton = new ModButton(56, 74, 64, 14, darkmode ? Quarry.MODE_DARK : Quarry.MODE, () -> changeMode(false), () -> changeMode(true), tile, this, 64, 28, true);
        addRenderableWidget(new ModButton(69, 38, 10, 14, darkmode ? Quarry.COUNTER_DOWN_DARK : Quarry.COUNTER_DOWN, () -> changeSpeed((byte) -1), null, tile, this, 10, 28, true));
        addRenderableWidget(new ModButton(95, 38, 10, 14, darkmode ? Quarry.COUNTER_UP_DARK : Quarry.COUNTER_UP, () -> changeSpeed((byte) 1), null, tile, this, 10, 28, true));
        addRenderableWidget(new ModButton(61, 56, 25, 14, darkmode ? Quarry.POWER_DARK : Quarry.POWER, () -> changePower(true), null, tile, this, 25, 28, true));
        addRenderableWidget(new ModButton(90, 56, 25, 14, darkmode ? Quarry.POWER_DARK : Quarry.POWER, () -> changePower(false), null, tile, this, 25, 28, true));

        addRenderableWidget(infoMouseButton);
        addRenderableWidget(lockMouseButton);
        addRenderableWidget(darkmodeMouseButton);
        addRenderableWidget(loopMouseButton);
        addRenderableWidget(filterMouseButton);
        addRenderableWidget(ejectMouseButton);
        addRenderableWidget(skipMouseButton);
        addRenderableWidget(modeMouseButton);
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
        if (modeMouseButton != null && modeMouseButton.isMouseOver(pMouseX, pMouseY)) {
            modeButtonIsHovered = true;
        } else {
            if (modeButtonIsHovered) modeButtonIsHovered = false;
        }
        if (infoMouseButton != null && infoMouseButton.isMouseOver(pMouseX, pMouseY)) {
            infoButtonIsHovered = true;
        } else {
            if (infoButtonIsHovered) infoButtonIsHovered = false;
        }
        if (lockMouseButton != null && lockMouseButton.isMouseOver(pMouseX, pMouseY)) {
            lockButtonIsHovered = true;
        } else {
            if (lockButtonIsHovered) lockButtonIsHovered = false;
        }
        if (loopMouseButton != null && loopMouseButton.isMouseOver(pMouseX, pMouseY)) {
            loopButtonIsHovered = true;
        } else {
            if (loopButtonIsHovered) loopButtonIsHovered = false;
        }
        if (ejectMouseButton != null && ejectMouseButton.isMouseOver(pMouseX, pMouseY)) {
            ejectButtonIsHovered = true;
        } else {
            if (ejectButtonIsHovered) ejectButtonIsHovered = false;
        }
        if (filterMouseButton != null && filterMouseButton.isMouseOver(pMouseX, pMouseY)) {
            filterButtonIsHovered = true;
        } else {
            if (filterButtonIsHovered) filterButtonIsHovered = false;
        }
        if (darkmodeMouseButton != null && darkmodeMouseButton.isMouseOver(pMouseX, pMouseY)) {
            darkmodeButtonIsHovered = true;
        } else {
            if (darkmodeButtonIsHovered) darkmodeButtonIsHovered = false;
        }
        if (skipMouseButton != null && skipMouseButton.isMouseOver(pMouseX, pMouseY)) {
            skipButtonIsHovered = true;
        } else {
            if (skipButtonIsHovered) skipButtonIsHovered = false;
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
        if ((Objects.equals(((QuarryBlockEntity) entity.getLevel().getBlockEntity(entity.getBlockPos())).getOwner(), this.getMinecraft().player.getName().getString() + "@" + this.getMinecraft().player.getStringUUID())) || this.getMinecraft().player.hasPermissions(2))
            PacketHandler.sendToServer(new QuarryBooleanPacket(this.getMenu().getTile().getBlockPos(), false, "locked"));
        sendChangedPacket();
    }

    private void changeMode(boolean reverse) {
        PacketHandler.sendToServer(new QuarryModePacket(this.getMenu().getTile().getBlockPos(), reverse ? 10 : 1));
        sendChangedPacket();
    }

    private void changeSpeed(byte state) {
        PacketHandler.sendToServer(new QuarryIntPacket(this.getMenu().getTile().getBlockPos(), state, "speed"));
        sendChangedPacket();
    }

    private void changeEject(byte state) {
        PacketHandler.sendToServer(new QuarryIntPacket(this.getMenu().getTile().getBlockPos(), state, "eject"));
        sendChangedPacket();
    }

    private void changePower(boolean state) {
        PacketHandler.sendToServer(new QuarryPowerPacket(this.getMenu().getTile().getBlockPos(), state));
        sendChangedPacket();
    }

    public void cycleLoop() {
        PacketHandler.sendToServer(new QuarryBooleanPacket(this.getMenu().getTile().getBlockPos(), false, "loop"));
        sendChangedPacket();
    }

    public void cycleSkip() {
        PacketHandler.sendToServer(new QuarryBooleanPacket(this.getMenu().getTile().getBlockPos(), false, "skip"));
        sendChangedPacket();
    }

    private void cycleFilter(boolean state) {
        PacketHandler.sendToServer(new QuarryBooleanPacket(this.getMenu().getTile().getBlockPos(), false, "filter"));
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

    public void drawCenteredText(String text, PoseStack stack, int x, int y, int color) {
        Minecraft.getInstance().font.draw(stack, text, x - (Minecraft.getInstance().font.width(text) / 2f), y, color);
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
