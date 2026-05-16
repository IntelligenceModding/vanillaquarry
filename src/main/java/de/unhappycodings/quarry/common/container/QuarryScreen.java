package de.unhappycodings.quarry.common.container;

import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.client.config.ClientConfig;
import de.unhappycodings.quarry.client.gui.widgets.ModButton;
import de.unhappycodings.quarry.common.block.QuarryBlock;
import de.unhappycodings.quarry.common.blockentity.QuarryEntity;
import de.unhappycodings.quarry.common.config.CommonConfig;
import de.unhappycodings.quarry.common.container.base.BaseScreen;
import de.unhappycodings.quarry.common.container.base.BaseSlot;
import de.unhappycodings.quarry.common.container.base.SlotInputHandler;
import de.unhappycodings.quarry.common.item.AreaCard;
import de.unhappycodings.quarry.common.networking.toServer.*;
import de.unhappycodings.quarry.common.util.CalcUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;
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
    public static ModButton replaceMouseButton;
    QuarryContainer container;

    public QuarryScreen(QuarryContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        this.container = screenContainer;
    }

    @Override
    protected void renderLabels(@Nonnull GuiGraphics graphics, int pMouseX, int pMouseY) {
        QuarryEntity blockEntity = this.getMenu().getTile();
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
        if (!Objects.equals(blockEntity.getOwner(), this.getMinecraft().player.getName().getString() + "@" + this.getMinecraft().player.getStringUUID()) && blockEntity.getLocked()) {
            drawCenteredString(graphics, Minecraft.getInstance().font, ChatFormatting.BOLD + Component.translatable("gui.quarry.admin").getString(), getSizeX() / 2, -30, 11141120, false);
            drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable("gui.quarry.others").getString(), getSizeX() / 2, -20, 11141120, false);
        }
        graphics.drawString(Minecraft.getInstance().font, Component.translatable("block.quarry.quarry_block").getString(), 71, 7, 1315860, false);
        graphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.quarry.inventory").getString(), 8, 110, 1315860, false);
        graphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.quarry.speed").getString(), 73, 27, 1315860, false);
        graphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.quarry.fuel").getString(), 19, 20, 1315860, false);
        graphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.quarry.out").getString(), 138, 20, 1315860, false);
        graphics.drawString(Minecraft.getInstance().font, Component.literal(blockEntity.getSpeed() + 1 + "").getString(), 85, 41, 1315860, false);
        String yCoord = Component.translatable("gui.quarry.stop").getString();
        ItemStack itemStack = getMenu().getItems().get(getMenu().getItems().size() - 2);
        if (itemStack.getItem() instanceof AreaCard && itemStack.has(Quarry.CURRENT_Y) && blockEntity.getLevel().getBlockState(blockEntity.getBlockPos()).getValue(QuarryBlock.ACTIVE)) {
            yCoord = String.valueOf(itemStack.get(Quarry.CURRENT_Y));
        }

        graphics.drawString(Minecraft.getInstance().font, Component.literal("Y: " + yCoord).getString(), 73, 95, 1315860, false);
        graphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.quarry.power.on").getString(), 68, 59, 1315860, false);
        graphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.quarry.power.off").getString(), 95, 59, 1315860, false);

        switch (blockEntity.getMode()) {
            case 0 -> drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable("gui.quarry.mode.default").getString(), 87, 77, 1315860, false);
            case 1 -> drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable("gui.quarry.mode.efficient").getString(), 87, 77, 1315860, false);
            case 2 -> drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable("gui.quarry.mode.fortune").getString(), 87, 77, 1315860, false);
            case 3 -> drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable("gui.quarry.mode.silktouch").getString(), 87, 77, 1315860, false);
            case 4 -> drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable("gui.quarry.mode.void").getString(), 87, 77, 1315860, false);
        }
        if (modeMouseButton.isMouseOver(pMouseX, pMouseY)) {
            List<Component> list = new ArrayList<>();
            float totalBurnTicks = CalcUtil.getNeededTicks(blockEntity.getMode(), blockEntity.getSpeed());
            switch (blockEntity.getMode()) {
                case 0 -> list.add(Component.translatable("gui.quarry.mode.default"));
                case 1 -> list.add(Component.translatable("gui.quarry.mode.efficient"));
                case 2 -> list.add(Component.translatable("gui.quarry.mode.fortune"));
                case 3 -> list.add(Component.translatable("gui.quarry.mode.silktouch"));
                default -> list.add(Component.translatable("gui.quarry.mode.void"));
            }
            list.add(Component.translatable("gui.quarry.consumption").append(" " + totalBurnTicks + " ").append("ticks").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.translatable("gui.quarry.coal").append(" " + (new DecimalFormat("##.##").format(1600 / totalBurnTicks).replace(",", ".")) + " ").append(Component.translatable("gui.quarry.blocks")).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            if (blockEntity.getMode() == 1)
                list.add(Component.translatable("gui.quarry.speed.80").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            graphics.renderComponentTooltip(Minecraft.getInstance().font, list, pMouseX - leftPos, pMouseY - topPos);
        }
        if (infoMouseButton.isMouseOver(pMouseX, pMouseY)) {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("gui.quarry.informations"));
            list.add(Component.literal(""));
            list.add(Component.literal("#" + getBurnTime() + "/" + getTotalBurnTime() + " ticks").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.literal(""));
            list.add(Component.translatable("gui.quarry.when_turned_off").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.translatable("gui.quarry.will_consume", CommonConfig.quarryIdleConsumption.get().toString()).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.literal("").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.translatable("gui.quarry.changing_speed").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.translatable("gui.quarry.affect_fuel").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.literal("").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.translatable("gui.quarry.replacing").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.literal("").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.translatable("gui.quarry.use_config").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).withItalic(true)));
            graphics.renderComponentTooltip(Minecraft.getInstance().font, list, pMouseX - leftPos, pMouseY - topPos);
        }
        if (lockMouseButton.isMouseOver(pMouseX, pMouseY)) {
            List<Component> list = new ArrayList<>();
            String owner = "undefined";
            String ownerString = blockEntity.getOwner();
            if (!ownerString.isEmpty())
                owner = ownerString.replace("@", " (") + (ownerString.equals("undefined") ? "" : ")");

            if (blockEntity.getLocked()) {
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
        if (loopMouseButton.isMouseOver(pMouseX, pMouseY)) {
            List<Component> list = new ArrayList<>();
            if (blockEntity.getLoop()) {
                list.add(Component.translatable("gui.quarry.loop.always"));
                list.add(Component.translatable("gui.quarry.loop.restart").withStyle(ChatFormatting.YELLOW));
            } else {
                list.add(Component.translatable("gui.quarry.loop.never"));
                list.add(Component.translatable("gui.quarry.loop.stop").withStyle(ChatFormatting.YELLOW));
            }
            graphics.renderComponentTooltip(Minecraft.getInstance().font, list, pMouseX - leftPos, pMouseY - topPos);
        }
        if (filterMouseButton.isMouseOver(pMouseX, pMouseY)) {
            List<Component> list = new ArrayList<>();
            if (blockEntity.getFilter()) {
                list.add(Component.translatable("gui.quarry.filter.always"));
                list.add(Component.translatable("gui.quarry.filter.filters").withStyle(ChatFormatting.YELLOW));
            } else {
                list.add(Component.translatable("gui.quarry.filter.never"));
                list.add(Component.translatable("gui.quarry.filter.all").withStyle(ChatFormatting.YELLOW));
            }
            graphics.renderComponentTooltip(Minecraft.getInstance().font, list, pMouseX - leftPos, pMouseY - topPos);
        }
        if (ejectMouseButton.isMouseOver(pMouseX, pMouseY)) {
            List<Component> list = new ArrayList<>();
            if (blockEntity.getEject() == 0) {
                list.add(Component.translatable("gui.quarry.output.dont"));
                list.add(Component.translatable("gui.quarry.output.in_out_hoppers").withStyle(ChatFormatting.YELLOW));
            } else if (blockEntity.getEject() == 1) {
                list.add(Component.translatable("gui.quarry.output.pull"));
                list.add(Component.translatable("gui.quarry.output.pulls_above").withStyle(ChatFormatting.YELLOW));
                list.add(Component.translatable("gui.quarry.output.out_hoppers").withStyle(ChatFormatting.YELLOW));
            } else if (blockEntity.getEject() == 2) {
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
        if (darkmodeMouseButton.isMouseOver(pMouseX, pMouseY)) {
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
        if (skipMouseButton.isMouseOver(pMouseX, pMouseY)) {
            List<Component> list = new ArrayList<>();
            if (blockEntity.getSkip()) {
                list.add(Component.translatable("gui.quarry.skip.always"));
                list.add(Component.translatable("gui.quarry.skip.skipped").withStyle(ChatFormatting.YELLOW));
                list.add(Component.translatable("gui.quarry.skip.skips").withStyle(ChatFormatting.YELLOW));
            } else {
                list.add(Component.translatable("gui.quarry.skip.never"));
                list.add(Component.translatable("gui.quarry.skip.iterate").withStyle(ChatFormatting.YELLOW));
            }
            graphics.renderComponentTooltip(Minecraft.getInstance().font, list, pMouseX - leftPos, pMouseY - topPos);
        }
        if (replaceMouseButton.isMouseOver(pMouseX, pMouseY)) {
            List<Component> list = new ArrayList<>();
            if (blockEntity.getReplace()) {
                list.add(Component.translatable("gui.quarry.replace.always"));
                list.add(Component.translatable("gui.quarry.replace.always.description").withStyle(ChatFormatting.YELLOW));
            } else {
                list.add(Component.translatable("gui.quarry.replace.never"));
                list.add(Component.translatable("gui.quarry.replace.never.description").withStyle(ChatFormatting.YELLOW));
            }
            graphics.renderComponentTooltip(Minecraft.getInstance().font, list, pMouseX - leftPos, pMouseY - topPos);
        }
    }

    public void drawCenteredString(GuiGraphics graphics, Font font, String text, int x, int y, int color, boolean shadow) {
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

    @Override
    public int getSlotColor(int index) {
        return ClientConfig.enableQuarryDarkmode.get() ? 0x806B6B6B : super.getSlotColor(index);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float partialTicks, int x, int y) {
        super.renderBg(graphics, partialTicks, x, y);
        QuarryEntity blockEntity = this.getMenu().getTile();
        Level level = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();

        // render sides
        graphics.blit(getTexture(), leftPos - 32, topPos + 12, 191, 0, 32, 88); // left
        graphics.blit(getTexture(), leftPos + getSizeX(), topPos + 12, 224, 0, 32, 88); // right

        // render button indicator
        // 0 = green | 2 = yellow | 4 = red | 6 = white
        graphics.blit(getTexture(), leftPos - 27, topPos + 21, 177 + (blockEntity.getLocked() ? 2 : 4), 20, 1, 10); // lock
        graphics.blit(getTexture(), leftPos - 27, topPos + 41, 177 + (blockEntity.getLoop() ? 4 : 0), 20, 1, 10); // loop
        graphics.blit(getTexture(), leftPos - 27, topPos + 61, 177 + (blockEntity.getFilter() ? 4 : 0), 20, 1, 10); // filter
        graphics.blit(getTexture(), leftPos - 27, topPos + 81, 177 + (blockEntity.getEject() == 2 || blockEntity.getEject() == 3 ? 4 : 0), 20, 1, 4); // eject
        graphics.blit(getTexture(), leftPos - 27, topPos + 87, 177 + (blockEntity.getEject() == 1 || blockEntity.getEject() == 3 ? 4 : 0), 20, 1, 4); // pull

        graphics.blit(getTexture(), leftPos + getSizeX() + 25, topPos + 61, 177 + (blockEntity.getSkip() ? 4 : 0), 20, 1, 10); // skip air
        graphics.blit(getTexture(), leftPos + getSizeX() + 25, topPos + 81, 177 + (blockEntity.getReplace() ? 4 : 0), 20, 1, 10); // replace fluids

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
        QuarryEntity blockEntity = this.getMenu().getTile();
        // right side
        infoMouseButton = new ModButton(getSizeX() + 7, 17, 18, 18, Quarry.INFO, null, null, blockEntity, this, 18, 36, false);
        darkmodeMouseButton = new ModButton(getSizeX() + 7, 37, 18, 18, Quarry.DARK_MODE, () -> {
            refreshWidgets();
            setDarkModeConfigValue(!getDarkModeConfigValue());
        }, null, blockEntity, this, 18, 36, true);
        skipMouseButton = new ModButton(getSizeX() + 7, 57, 18, 18, Quarry.SKIP, () -> cycleBoolean("skip"), null, blockEntity, this, 18, 36, true);
        replaceMouseButton = new ModButton(getSizeX() + 7, 77, 18, 18, Quarry.REPLACE, () -> cycleBoolean("replace"), null, blockEntity, this, 18, 36, true);

        // left side
        lockMouseButton = new ModButton(-26, 17, 18, 18, Quarry.LOCK, this::cycleLocked, null, blockEntity, this, 18, 36, true);
        loopMouseButton = new ModButton(-26, 37, 18, 18, Quarry.LOOP, () -> cycleBoolean("loop"), null, blockEntity, this, 18, 36, true);
        filterMouseButton = new ModButton(-26, 57, 18, 18, Quarry.FILTER, () -> cycleBoolean("filter"), null, blockEntity, this, 18, 36, true);
        ejectMouseButton = new ModButton(-26, 77, 18, 18, Quarry.EJECT, () -> changeEject((byte) 1), null, blockEntity, this, 18, 36, true);

        modeMouseButton = new ModButton(56, 74, 64, 14, Quarry.MODE, () -> changeMode(false), () -> changeMode(true), blockEntity, this, 64, 28, true);
        addRenderableWidget(new ModButton(69, 38, 10, 14, Quarry.COUNTER_DOWN, () -> changeSpeed((byte) -1), null, blockEntity, this, 10, 28, true));
        addRenderableWidget(new ModButton(95, 38, 10, 14, Quarry.COUNTER_UP, () -> changeSpeed((byte) 1), null, blockEntity, this, 10, 28, true));
        addRenderableWidget(new ModButton(61, 56, 25, 14, Quarry.POWER, () -> changePower(true), null, blockEntity, this, 25, 28, true));
        addRenderableWidget(new ModButton(90, 56, 25, 14, Quarry.POWER, () -> changePower(false), null, blockEntity, this, 25, 28, true));

        addRenderableWidget(infoMouseButton);
        addRenderableWidget(lockMouseButton);
        addRenderableWidget(darkmodeMouseButton);
        addRenderableWidget(loopMouseButton);
        addRenderableWidget(filterMouseButton);
        addRenderableWidget(ejectMouseButton);
        addRenderableWidget(skipMouseButton);
        addRenderableWidget(replaceMouseButton);
        addRenderableWidget(modeMouseButton);
    }

    @Override
    protected void slotClicked(@Nonnull Slot pSlot, int pSlotId, int pMouseButton, @Nonnull ClickType pType) {
        super.slotClicked(pSlot, pSlotId, pMouseButton, pType);
        sendChangedPacket();
        if (pSlotId == 48) {
            ItemStack stack = pSlot.getItem().isEmpty() ? Items.STONE.getDefaultInstance() : pSlot.getItem();

            PacketDistributor.sendToServer(new QuarryChangedPacket(stack.is(Quarry.AREA_CARD.get()) ? 1 : 2, getMenu().getTile().getBlockPos(), stack));
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

    public void sendChangedPacket() {
        PacketDistributor.sendToServer(new QuarryChangedPacket(0, this.getMenu().getTile().getBlockPos(), new ItemStack(Items.STONE)));
    }

    public void cycleLocked() {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) return;

        LocalPlayer player = Minecraft.getInstance().player;
        QuarryEntity entity = this.getMenu().getTile();
        if ((Objects.equals(entity.getOwner(), player.getName().getString() + "@" + player.getStringUUID())) || player.hasPermissions(2))
            PacketDistributor.sendToServer(new QuarryBooleanPacket(entity.getBlockPos(), false, "locked"));
        sendChangedPacket();
    }

    private void changeMode(boolean reverse) {
        PacketDistributor.sendToServer(new QuarryModePacket(this.getMenu().getTile().getBlockPos(), reverse ? 10 : 1));
        sendChangedPacket();
    }

    private void changeSpeed(byte state) {
        PacketDistributor.sendToServer(new QuarryIntPacket(this.getMenu().getTile().getBlockPos(), state, "speed"));
        sendChangedPacket();
    }

    private void changeEject(byte state) {
        PacketDistributor.sendToServer(new QuarryIntPacket(this.getMenu().getTile().getBlockPos(), state, "eject"));
        sendChangedPacket();
    }

    private void changePower(boolean state) {
        PacketDistributor.sendToServer(new QuarryPowerPacket(this.getMenu().getTile().getBlockPos(), state));
        sendChangedPacket();
    }

    public void cycleBoolean(String type) {
        PacketDistributor.sendToServer(new QuarryBooleanPacket(this.getMenu().getTile().getBlockPos(), false, type));
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
        return ResourceLocation.fromNamespaceAndPath(Quarry.MOD_ID, texture);
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
