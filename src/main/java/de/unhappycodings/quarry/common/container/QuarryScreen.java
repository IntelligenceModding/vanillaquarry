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
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

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
    private static final int TEXT_COLOR = 0xFF141414;
    private static final int ALERT_COLOR = 0xFFAA0000;
    QuarryContainer container;

    public QuarryScreen(QuarryContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn, 176, 204);
        this.container = screenContainer;
    }

    @Override
    protected void extractLabels(@Nonnull GuiGraphicsExtractor graphics, int pMouseX, int pMouseY) {
        QuarryEntity blockEntity = this.getMenu().getTile();
        boolean energyPowered = blockEntity.isEnergyPowered();
        Slot hoveredSlot = getHoveredSlot();
        if (hoveredSlot instanceof SlotInputHandler) {
            if (hoveredSlot.getContainerSlot() == 13 && !hoveredSlot.hasItem()) {
                List<Component> list = new ArrayList<>();
                list.add(Component.translatable("gui.quarry.replace"));
                list.add(Component.translatable("gui.quarry.replace_1").withStyle(ChatFormatting.YELLOW));
                list.add(Component.translatable("gui.quarry.replace_2").withStyle(ChatFormatting.YELLOW));
                list.add(Component.literal(""));
                list.add(Component.translatable("gui.quarry.replace_3").withStyle(ChatFormatting.YELLOW));

                graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, list, pMouseX, pMouseY);
            }
        }
        if (!Objects.equals(blockEntity.getOwner(), this.getMinecraft().player.getName().getString() + "@" + this.getMinecraft().player.getStringUUID()) && blockEntity.getLocked()) {
            drawCenteredString(graphics, Minecraft.getInstance().font, ChatFormatting.BOLD + Component.translatable("gui.quarry.admin").getString(), getSizeX() / 2, -30, ALERT_COLOR, false);
            drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable("gui.quarry.others").getString(), getSizeX() / 2, -20, ALERT_COLOR, false);
        }

        drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable(blockEntity.getBlockState().getBlock().getDescriptionId()).getString(), getSizeX() / 2, 7, TEXT_COLOR, false);
        graphics.text(Minecraft.getInstance().font, Component.translatable("gui.quarry.inventory").getString(), 8, 110, TEXT_COLOR, false);
        drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable("gui.quarry.speed").getString(), getSizeX() / 2, 27, TEXT_COLOR, false);
        drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable(energyPowered ? "gui.quarry.energy" : "gui.quarry.fuel").getString(), 29, 20, TEXT_COLOR, false);
        drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable("gui.quarry.out").getString(), 148, 20, TEXT_COLOR, false);
        drawCenteredString(graphics, Minecraft.getInstance().font, Component.literal(blockEntity.getSpeed() + 1 + "").getString(), getSizeX() / 2, 41, TEXT_COLOR, false);
        String yCoord = Component.translatable("gui.quarry.stop").getString();
        ItemStack itemStack = getMenu().getItems().get(getMenu().getItems().size() - 2);
        if (itemStack.getItem() instanceof AreaCard && itemStack.has(Quarry.CURRENT_Y) && blockEntity.getLevel().getBlockState(blockEntity.getBlockPos()).getValue(QuarryBlock.ACTIVE)) {
            yCoord = String.valueOf(itemStack.get(Quarry.CURRENT_Y));
        }

        graphics.text(Minecraft.getInstance().font, Component.literal("Y: " + yCoord).getString(), 73, 95, TEXT_COLOR, false);
        graphics.text(Minecraft.getInstance().font, Component.translatable("gui.quarry.power.on").getString(), 68, 59, TEXT_COLOR, false);
        graphics.text(Minecraft.getInstance().font, Component.translatable("gui.quarry.power.off").getString(), 95, 59, TEXT_COLOR, false);

        switch (blockEntity.getMode()) {
            case 0 -> drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable("gui.quarry.mode.default").getString(), 87, 77, TEXT_COLOR, false);
            case 1 -> drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable("gui.quarry.mode.efficient").getString(), 87, 77, TEXT_COLOR, false);
            case 2 -> drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable("gui.quarry.mode.fortune").getString(), 87, 77, TEXT_COLOR, false);
            case 3 -> drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable("gui.quarry.mode.silktouch").getString(), 87, 77, TEXT_COLOR, false);
            case 4 -> drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable("gui.quarry.mode.void").getString(), 87, 77, TEXT_COLOR, false);
        }
        if (modeMouseButton.isMouseOver(pMouseX, pMouseY)) {
            List<Component> list = new ArrayList<>();
            float totalBurnTicks = CalcUtil.getNeededTicks(blockEntity.getMode(), blockEntity.getSpeed(), energyPowered);
            switch (blockEntity.getMode()) {
                case 0 -> list.add(Component.translatable("gui.quarry.mode.default"));
                case 1 -> list.add(Component.translatable("gui.quarry.mode.efficient"));
                case 2 -> list.add(Component.translatable("gui.quarry.mode.fortune"));
                case 3 -> list.add(Component.translatable("gui.quarry.mode.silktouch"));
                default -> list.add(Component.translatable("gui.quarry.mode.void"));
            }
            list.add(Component.translatable("gui.quarry.consumption").append(" " + totalBurnTicks + " ").append(energyPowered ? "FE" : "ticks").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            if (energyPowered) {
                list.add(Component.literal(blockEntity.getEnergyCapacity() + " FE: " + (new DecimalFormat("##.##").format(blockEntity.getEnergyCapacity() / totalBurnTicks).replace(",", ".")) + " ").append(Component.translatable("gui.quarry.blocks")).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            } else {
                list.add(Component.translatable("gui.quarry.coal").append(" " + (new DecimalFormat("##.##").format(1600 / totalBurnTicks).replace(",", ".")) + " ").append(Component.translatable("gui.quarry.blocks")).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            }
            if (blockEntity.getMode() == 1)
                list.add(Component.translatable("gui.quarry.speed.80").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, list, pMouseX, pMouseY);
        }
        if (energyPowered && isMouseOverEnergyBar(pMouseX, pMouseY)) {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("gui.quarry.energy"));
            list.add(Component.literal(blockEntity.getEnergyStored() + "/" + blockEntity.getEnergyCapacity() + " FE").withStyle(ChatFormatting.YELLOW));
            graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, list, pMouseX, pMouseY);
        }
        if (infoMouseButton.isMouseOver(pMouseX, pMouseY)) {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("gui.quarry.informations"));
            list.add(Component.translatable("gui.quarry.toggleholo").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.literal(""));
            list.add(Component.literal("#" + getBurnTime() + "/" + getTotalBurnTime() + (energyPowered ? " FE" : " ticks")).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.literal(""));
            list.add(Component.translatable("gui.quarry.when_turned_off").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            int idleConsumption = energyPowered ? CommonConfig.feQuarryIdleConsumption.get() : CommonConfig.quarryIdleConsumption.get();
            list.add(Component.translatable(energyPowered ? "gui.quarry.will_consume_fe" : "gui.quarry.will_consume", String.valueOf(idleConsumption)).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.literal("").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.translatable("gui.quarry.changing_speed").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.translatable("gui.quarry.affect_fuel").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.literal("").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.translatable("gui.quarry.replacing").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.translatable("gui.quarry.maxrange", CommonConfig.quarryMineRadius.get()).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.literal("").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
            list.add(Component.translatable("gui.quarry.use_config").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).withItalic(true)));
            graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, list, pMouseX, pMouseY);
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
            graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, list, pMouseX, pMouseY);
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
            graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, list, pMouseX, pMouseY);
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
            graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, list, pMouseX, pMouseY);
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
            graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, list, pMouseX, pMouseY);
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
            graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, list, pMouseX, pMouseY);
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
            graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, list, pMouseX, pMouseY);
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
            graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, list, pMouseX, pMouseY);
        }
    }

    public void drawCenteredString(GuiGraphicsExtractor graphics, Font font, String text, int x, int y, int color, boolean shadow) {
        graphics.text(font, text, x - font.width(text) / 2, y, color, shadow);
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
    public void extractBackground(@Nonnull GuiGraphicsExtractor graphics, int x, int y, float partialTicks) {
        super.extractBackground(graphics, x, y, partialTicks);
        QuarryEntity blockEntity = this.getMenu().getTile();
        Level level = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();

        // render sides
        graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos - 32, topPos + 12, 191, 0, 32, 88, 256, 256); // left
        graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos + getSizeX(), topPos + 12, 224, 0, 32, 88, 256, 256); // right

        // render button indicator
        // 0 = green | 2 = yellow | 4 = red | 6 = white
        graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos - 27, topPos + 21, 177 + (blockEntity.getLocked() ? 2 : 4), 20, 1, 10, 256, 256); // lock
        graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos - 27, topPos + 41, 177 + (blockEntity.getLoop() ? 4 : 0), 20, 1, 10, 256, 256); // loop
        graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos - 27, topPos + 61, 177 + (blockEntity.getFilter() ? 4 : 0), 20, 1, 10, 256, 256); // filter
        graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos - 27, topPos + 81, 177 + (blockEntity.getEject() == 2 || blockEntity.getEject() == 3 ? 4 : 0), 20, 1, 4, 256, 256); // eject
        graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos - 27, topPos + 87, 177 + (blockEntity.getEject() == 1 || blockEntity.getEject() == 3 ? 4 : 0), 20, 1, 4, 256, 256); // pull

        graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos + getSizeX() + 25, topPos + 21, 177 + (ClientConfig.enableQuarryHolograph.get() ? 4 : 0), 20, 1, 10, 256, 256); // skip air
        graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos + getSizeX() + 25, topPos + 61, 177 + (blockEntity.getSkip() ? 4 : 0), 20, 1, 10, 256, 256); // skip air
        graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos + getSizeX() + 25, topPos + 81, 177 + (blockEntity.getReplace() ? 4 : 0), 20, 1, 10, 256, 256); // replace fluids

        // render power storage
        if (blockEntity.isEnergyPowered()) {
            renderEnergyBar(graphics, blockEntity);
        } else {
            int height = getLitProgress();
            graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos + 23, topPos + 86 + 13 - height, 176, 13 - height, 14, height + 1, 256, 256);
        }

        // render power dot indicators
        graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos + 63, topPos + 96, !level.getBlockState(pos).getValue(QuarryBlock.ACTIVE) ? 181 : blockEntity.inventoryFull || blockEntity.skippingAir || blockEntity.outOfRange ? 186 : 176, 14, 5, 5, 256, 256); // red


        // Render slots
        for (Slot slot : container.slots) {
            if (slot instanceof BaseSlot baseSlot) {
                baseSlot.renderGhostOverlay(graphics, getLeftPos(), getTopPos());
            }
        }
    }

    protected void addElements() {
        QuarryEntity blockEntity = this.getMenu().getTile();
        // right side
        infoMouseButton = new ModButton(getSizeX() + 7, 17, 18, 18, Quarry.INFO, () -> setHolographConfigValue(!getHolographConfigValue()), null, blockEntity, this, 18, 36, true);
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
    protected void slotClicked(Slot pSlot, int pSlotId, int pMouseButton, @Nonnull ContainerInput pType) {
        super.slotClicked(pSlot, pSlotId, pMouseButton, pType);
        sendChangedPacket();
        if (pSlot == null) return;

        if (pSlot.getContainerSlot() == 12) {
            ItemStack stack = pSlot.getItem().isEmpty() ? Items.STONE.getDefaultInstance() : pSlot.getItem();

            ClientPacketDistributor.sendToServer(new QuarryChangedPacket(stack.is(Quarry.AREA_CARD.get()) ? 1 : 2, getMenu().getTile().getBlockPos(), stack));
        }
    }

    @Override
    protected void containerTick() {
        refreshDarkmode();
        super.containerTick();
    }

    public boolean getHolographConfigValue() {
        return ClientConfig.enableQuarryHolograph.get();
    }

    public void setHolographConfigValue(boolean state) {
        ClientConfig.setQuarryHolograph(state);
    }

    public boolean getDarkModeConfigValue() {
        return ClientConfig.enableQuarryDarkmode.get();
    }

    public void setDarkModeConfigValue(boolean state) {
        ClientConfig.setQuarryDarkmode(state);
    }

    public void refreshDarkmode() {
        refreshWidgets();
    }

    public void sendChangedPacket() {
        ClientPacketDistributor.sendToServer(new QuarryChangedPacket(0, this.getMenu().getTile().getBlockPos(), new ItemStack(Items.STONE)));
    }

    public void cycleLocked() {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) return;

        LocalPlayer player = Minecraft.getInstance().player;
        QuarryEntity entity = this.getMenu().getTile();
        if ((Objects.equals(entity.getOwner(), player.getName().getString() + "@" + player.getStringUUID())) || player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
            ClientPacketDistributor.sendToServer(new QuarryBooleanPacket(entity.getBlockPos(), false, "locked"));
        sendChangedPacket();
    }

    private void changeMode(boolean reverse) {
        ClientPacketDistributor.sendToServer(new QuarryModePacket(this.getMenu().getTile().getBlockPos(), reverse ? 10 : 1));
        sendChangedPacket();
    }

    private void changeSpeed(byte state) {
        ClientPacketDistributor.sendToServer(new QuarryIntPacket(this.getMenu().getTile().getBlockPos(), state, "speed"));
        sendChangedPacket();
    }

    private void changeEject(byte state) {
        ClientPacketDistributor.sendToServer(new QuarryIntPacket(this.getMenu().getTile().getBlockPos(), state, "eject"));
        sendChangedPacket();
    }

    private void changePower(boolean state) {
        ClientPacketDistributor.sendToServer(new QuarryPowerPacket(this.getMenu().getTile().getBlockPos(), state));
        sendChangedPacket();
    }

    public void cycleBoolean(String type) {
        ClientPacketDistributor.sendToServer(new QuarryBooleanPacket(this.getMenu().getTile().getBlockPos(), false, type));
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
    public Identifier getTexture() {
        if (!getDarkModeConfigValue()) refreshDarkmode();
        String texture = "textures/gui/quarry_gui.png";
        if (getDarkModeConfigValue()) texture = "textures/gui/quarry_gui_dark.png";
        return Identifier.fromNamespaceAndPath(Quarry.MOD_ID, texture);
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

    private boolean isMouseOverEnergyBar(int mouseX, int mouseY) {
        return mouseX >= leftPos + 13 && mouseX <= leftPos + 49 && mouseY >= topPos + 30 && mouseY <= topPos + 84;
    }

    private void renderEnergyBar(GuiGraphicsExtractor graphics, QuarryEntity blockEntity) {
        int x = leftPos + 12;
        int y = topPos + 29;
        int width = 36;
        int height = 54;
        int stored = blockEntity.getEnergyStored();
        int capacity = Math.max(1, blockEntity.getEnergyCapacity());
        int innerX = x + 5;
        int innerY = y + 5;
        int innerWidth = width - 10;
        int innerHeight = height - 10;
        int filled = (int) Math.min(innerHeight, ((long) stored * innerHeight) / capacity);
        int fillTop = innerY + innerHeight - filled;

        graphics.fill(x, y, x + width, y + height + 17, getDarkModeConfigValue() ? 0xFF535353 : 0xFFC6C6C6);
        graphics.fill(x, y, x + width, y + height, getDarkModeConfigValue() ? 0xFF1D1D1D : 0xFFE6E0DE);
        graphics.outline(x, y, width, height, 0xFF111111);
        graphics.outline(x + 1, y + 1, width - 2, height - 2, 0xFF8B1E18);
        graphics.fill(x + 2, y + 2, x + width - 2, y + 4, 0xFFE17B6F);
        graphics.fill(x + 2, y + height - 4, x + width - 2, y + height - 2, 0xFF4A0E0A);
        graphics.fill(x + 2, y + 4, x + 4, y + height - 4, 0xFFB8483C);
        graphics.fill(x + width - 4, y + 4, x + width - 2, y + height - 4, 0xFF3B0D0A);
        graphics.fill(innerX, innerY, innerX + innerWidth, innerY + innerHeight, 0xFF151111);

        if (filled > 0) {
            graphics.fill(innerX, fillTop, innerX + innerWidth, innerY + innerHeight, 0xFFB41518);
            graphics.fill(innerX + 2, fillTop, innerX + innerWidth - 2, innerY + innerHeight, 0xFFE42528);
            graphics.fill(innerX + 5, fillTop, innerX + innerWidth - 5, innerY + innerHeight, 0xFFE23D40);
        }

        for (int markerY = innerY + 1; markerY < innerY + innerHeight; markerY += 2) {
            graphics.fill(innerX, markerY, innerX + innerWidth, markerY + 1, 0xFF2B1414);
            if (markerY >= fillTop) {
                graphics.fill(innerX, markerY, innerX + innerWidth, markerY + 1, 0xFFA51318);
            }
        }
    }
}
