package de.unhappycodings.quarry.common.container;

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
import de.unhappycodings.quarry.common.item.ModItems;
import de.unhappycodings.quarry.common.network.PacketHandler;
import de.unhappycodings.quarry.common.network.toserver.*;
import de.unhappycodings.quarry.common.util.CalcUtil;
import de.unhappycodings.quarry.common.util.NbtUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
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
    protected void renderLabels(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY) {
        if (getSlotUnderMouse() != null && getSlotUnderMouse() instanceof SlotInputHandler) {
            if (getSlotUnderMouse().getContainerSlot() == 13 && !getSlotUnderMouse().hasItem()) {
                List<Component> list = new ArrayList<>();
                list.add(Component.translatable("gui.quarry.replace"));
                list.add(Component.translatable("gui.quarry.replace_1").withStyle(ChatFormatting.YELLOW));
                list.add(Component.translatable("gui.quarry.replace_2").withStyle(ChatFormatting.YELLOW));
                list.add(Component.literal(""));
                list.add(Component.translatable("gui.quarry.replace_3").withStyle(ChatFormatting.YELLOW));

                graphics.renderComponentTooltip(Minecraft.getInstance().font, list, pMouseX - leftPos, pMouseY - topPos);
            }
        }
        if (!Objects.equals(getMenu().getTile().getOwner(), this.getMinecraft().player.getName().getString() + "@" + this.getMinecraft().player.getStringUUID()) && this.getMenu().getTile().getLocked()) {
            drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable("gui.quarry.admin").getString(), getSizeY(), getSizeX() / 2, 11141120, false);
            drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable("gui.quarry.others").getString(), getSizeY(), getSizeX() / 2, 11141120, false);
        }
        graphics.drawString(Minecraft.getInstance().font, Component.translatable("block.quarry.quarry_block").getString(), 71, 7, 1315860, false);
        graphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.quarry.inventory").getString(), 8, 110, 1315860, false);
        graphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.quarry.speed").getString(), 73, 27, 1315860, false);
        graphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.quarry.fuel").getString(), 19, 20, 1315860, false);
        graphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.quarry.out").getString(), 138, 20, 1315860, false);
        graphics.drawString(Minecraft.getInstance().font, Component.literal(getMenu().getTile().getSpeed() + 1 + "").getString(), 85, 41, 1315860, false);
        String yCoord = Component.translatable("gui.quarry.stop").getString();
        ItemStack itemStack = getMenu().getItems().get(getMenu().getItems().size() - 2);
        if (itemStack.getItem() instanceof AreaCardItem && NbtUtil.getNbtTag(itemStack).contains("currentY") && getMenu().getTile().getLevel().getBlockState(getMenu().getTile().getBlockPos()).getValue(QuarryBlock.ACTIVE)) {
            yCoord = String.valueOf(NbtUtil.getNbtTag(itemStack).getInt("currentY"));
        }

        graphics.drawString(Minecraft.getInstance().font, Component.literal("Y: " + yCoord).getString(), 73, 95, 1315860, false);
        graphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.quarry.power.on").getString(), 68, 59, 1315860, false);
        graphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.quarry.power.off").getString(), 95, 59, 1315860, false);

        switch (getMenu().getTile().getMode()) {
            case 0 ->
                    drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable("gui.quarry.mode.default").getString(), 87, 77, 1315860, false);
            case 1 ->
                    drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable("gui.quarry.mode.efficient").getString(), 87, 77, 1315860, false);
            case 2 ->
                    drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable("gui.quarry.mode.fortune").getString(), 87, 77, 1315860, false);
            case 3 ->
                    drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable("gui.quarry.mode.silktouch").getString(), 87, 77, 1315860, false);
            case 4 -> drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable("gui.quarry.mode.void").getString(), 87, 77, 1315860, false);
        }
        if (modeButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            float totalBurnTicks = CalcUtil.getNeededTicks(getMenu().getTile().getMode(), getMenu().getTile().getSpeed());
            switch (getMenu().getTile().getMode()) {
                case 0 -> list.add(Component.translatable("gui.quarry.mode.default"));
                case 1 -> list.add(Component.translatable("gui.quarry.mode.efficient"));
                case 2 -> list.add(Component.translatable("gui.quarry.mode.fortune"));
                case 3 -> list.add(Component.translatable("gui.quarry.mode.silktouch"));
                default -> list.add(Component.translatable("gui.quarry.mode.void"));
            }
            list.add(Component.translatable("gui.quarry.consumption").append(" " + totalBurnTicks + " ").append("ticks").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.translatable("gui.quarry.coal").append(" " + (new DecimalFormat("##.##").format(1600 / totalBurnTicks).replace(",", ".")) + " ").append(Component.translatable("gui.quarry.blocks")).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            if (getMenu().getTile().getMode() == 1)
                list.add(Component.translatable("gui.quarry.speed.80").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            graphics.renderComponentTooltip(Minecraft.getInstance().font, list, pMouseX - leftPos, pMouseY - topPos);
        }
        if (infoButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("gui.quarry.informations"));
            list.add(Component.literal(""));
            list.add(Component.literal("#" + getBurnTime() + "/" + getTotalBurnTime() + " ticks").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.literal(""));
            list.add(Component.translatable("gui.quarry.when_turned_off").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.literal(Component.translatable("gui.quarry.will_consume").getString().replace("#", CommonConfig.quarryIdleConsumption.get().toString())).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.literal("").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.translatable("gui.quarry.changing_speed").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.translatable("gui.quarry.affect_fuel").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.literal("").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.translatable("gui.quarry.replacing").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.literal("").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.translatable("gui.quarry.use_config").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).withItalic(true)));
            graphics.renderComponentTooltip(Minecraft.getInstance().font, list, pMouseX - leftPos, pMouseY - topPos);
        }
        if (lockButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            String owner = "undefined";
            String ownerString = getMenu().getTile().getOwner();
            if (!ownerString.isEmpty())
                owner = ownerString.replace("@", " (") + (ownerString.equals("undefined") ? "" : ")");

            if (getMenu().getTile().getLocked()) {
                list.add(Component.translatable("gui.quarry.lock.private"));
                list.add(Component.translatable("gui.quarry.lock.private.description").withStyle(ChatFormatting.YELLOW));
                list.add(Component.translatable("gui.quarry.lock.owner", owner).withStyle(ChatFormatting.YELLOW));
            } else {
                list.add(Component.translatable("gui.quarry.lock.public"));
                list.add(Component.translatable("gui.quarry.lock.public.description").withStyle(ChatFormatting.YELLOW));
                list.add(Component.translatable("gui.quarry.lock.owner", owner).withStyle(ChatFormatting.YELLOW));
            }
            graphics.renderComponentTooltip(Minecraft.getInstance().font, list, pMouseX - leftPos, pMouseY - topPos);
        }
        if (loopButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            if (getMenu().getTile().getLoop()) {
                list.add(Component.translatable("gui.quarry.loop.always"));
                list.add(Component.translatable("gui.quarry.loop.restart").withStyle(ChatFormatting.YELLOW));
            } else {
                list.add(Component.translatable("gui.quarry.loop.never"));
                list.add(Component.translatable("gui.quarry.loop.stop").withStyle(ChatFormatting.YELLOW));
            }
            graphics.renderComponentTooltip(Minecraft.getInstance().font, list, pMouseX - leftPos, pMouseY - topPos);
        }
        if (filterButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            if (getMenu().getTile().getFilter()) {
                list.add(Component.translatable("gui.quarry.filter.always"));
                list.add(Component.translatable("gui.quarry.filter.filters").withStyle(ChatFormatting.YELLOW));
            } else {
                list.add(Component.translatable("gui.quarry.filter.never"));
                list.add(Component.translatable("gui.quarry.filter.all").withStyle(ChatFormatting.YELLOW));
            }
            graphics.renderComponentTooltip(Minecraft.getInstance().font, list, pMouseX - leftPos, pMouseY - topPos);
        }
        if (ejectButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            if (getMenu().getTile().getEject() == 0) {
                list.add(Component.translatable("gui.quarry.output.dont"));
                list.add(Component.translatable("gui.quarry.output.in_out_hoppers").withStyle(ChatFormatting.YELLOW));
            } else if (getMenu().getTile().getEject() == 1) {
                list.add(Component.translatable("gui.quarry.output.pull"));
                list.add(Component.translatable("gui.quarry.output.pulls_above").withStyle(ChatFormatting.YELLOW));
                list.add(Component.translatable("gui.quarry.output.out_hoppers").withStyle(ChatFormatting.YELLOW));
            } else if (getMenu().getTile().getEject() == 2) {
                list.add(Component.translatable("gui.quarry.output.eject"));
                list.add(Component.translatable("gui.quarry.output.eject_below").withStyle(ChatFormatting.YELLOW));
                list.add(Component.translatable("gui.quarry.output.in_hoppers").withStyle(ChatFormatting.YELLOW));
            } else {
                list.add(Component.translatable("gui.quarry.output.both"));
                list.add(Component.translatable("gui.quarry.output.pulls_above").withStyle(ChatFormatting.YELLOW));
                list.add(Component.translatable("gui.quarry.output.eject_below").withStyle(ChatFormatting.YELLOW));
            }
            graphics.renderComponentTooltip(Minecraft.getInstance().font, list, pMouseX - leftPos, pMouseY - topPos);
        }
        if (skipButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            if (getMenu().getTile().getSkip()) {
                list.add(Component.translatable("gui.quarry.skip.always"));
                list.add(Component.translatable("gui.quarry.skip.skipped").withStyle(ChatFormatting.YELLOW));
            } else {
                list.add(Component.translatable("gui.quarry.skip.never"));
                list.add(Component.translatable("gui.quarry.skip.iterate").withStyle(ChatFormatting.YELLOW));
            }
            graphics.renderComponentTooltip(Minecraft.getInstance().font, list, pMouseX - leftPos, pMouseY - topPos);
        }
        if (darkmodeButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            if (getDarkModeConfigValue()) {
                list.add(Component.translatable("gui.quarry.darkmode.dark"));
                list.add(Component.translatable("gui.quarry.darkmode.dark.switch").withStyle(ChatFormatting.YELLOW));
            } else {
                list.add(Component.translatable("gui.quarry.darkmode.white"));
                list.add(Component.translatable("gui.quarry.darkmode.white.switch").withStyle(ChatFormatting.YELLOW));
            }
            graphics.renderComponentTooltip(Minecraft.getInstance().font, list, pMouseX - leftPos, pMouseY - topPos);
        }
    }

    public void drawCenteredString(GuiGraphics graphics, Font font, String text, int x, int y, int color, boolean shadow) {
        graphics.drawString(font, text, x - font.width(text) / 2, y, color, shadow);
    }

    public void drawCenteredString(GuiGraphics graphics, Font font, Component text, int x, int y, int color, boolean shadow) {
        graphics.drawString(font, text, x - font.width(text) / 2, y, color, shadow);
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
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int x, int y) {
        super.renderBg(graphics, partialTicks, x, y);
        Level level = this.getMenu().getTile().getLevel();
        BlockPos pos = this.getMenu().getTile().getBlockPos();

        // render burn tick process
        int height = getLitProgress();
        graphics.blit(getTexture(), leftPos + 23, topPos + 86 + 13 - height, 176, 13 - height, 14, height + 1);

        // render power dot indicators
        if (level.getBlockState(pos).getValue(QuarryBlock.ACTIVE)) {
            graphics.blit(getTexture(), leftPos + 63, topPos + 96, 176, 14, 5, 5); // green
        } else {
            graphics.blit(getTexture(), leftPos + 63, topPos + 96, 182, 14, 5, 5); // red
        }

        // Render slots
        for (Slot slot : container.slots) {
            if (slot instanceof BaseSlot baseSlot) {
                baseSlot.renderGhostOverlay(graphics, getGuiLeft(), getGuiTop());
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
            PacketHandler.sendToServer(new QuarryChangedPacket(pSlot.getItem(), pSlot.getItem().is(ModItems.AREA_CARD.get()) ? 1 : 2, this.getMenu().getTile().getBlockPos()));
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
