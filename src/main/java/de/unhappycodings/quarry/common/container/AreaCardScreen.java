package de.unhappycodings.quarry.common.container;

import com.mojang.blaze3d.systems.RenderSystem;
import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.client.config.ClientConfig;
import de.unhappycodings.quarry.client.gui.GuiUtil;
import de.unhappycodings.quarry.client.gui.widgets.ModButton;
import de.unhappycodings.quarry.common.container.base.BaseScreen;
import de.unhappycodings.quarry.common.container.base.ModEditBox;
import de.unhappycodings.quarry.common.item.AreaCardItem;
import de.unhappycodings.quarry.common.item.ModItems;
import de.unhappycodings.quarry.common.network.PacketHandler;
import de.unhappycodings.quarry.common.network.toserver.AreaCardItemPacket;
import de.unhappycodings.quarry.common.util.CalcUtil;
import de.unhappycodings.quarry.common.util.NbtUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AreaCardScreen extends BaseScreen<AreaCardContainer> {
    public static final ResourceLocation GHOST_OVERLAY = new ResourceLocation(Quarry.MOD_ID, "textures/gui/slot/filter_overlay.png");
    public static final ResourceLocation GHOST_OVERLAY_DARK = new ResourceLocation(Quarry.MOD_ID, "textures/gui/slot/filter_overlay_dark.png");
    public static final ResourceLocation POS = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/pos.png");
    public static final ResourceLocation RADIUS = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/radius.png");
    public static final ResourceLocation CHUNK = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/chunk.png");
    public static final ResourceLocation FILTER = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/filter.png");
    public static ModButton darkmodeMouseButton;
    public static ModButton posMouseButton;
    public static ModButton radiusMouseButton;
    public static ModButton chunkMouseButton;
    public static ModButton filterMouseButton;
    public ModEditBox pos1x;
    public ModEditBox pos1y;
    public ModEditBox pos1z;
    public ModEditBox pos2x;
    public ModEditBox pos2y;
    public ModEditBox pos2z;
    public ModEditBox top;
    public ModEditBox down;
    public ModEditBox[] positionInputs;
    public ModEditBox[] heightInputs;
    AreaCardContainer container;
    boolean init1 = false;
    boolean init2 = false;
    boolean init3 = false;
    boolean init4 = false;
    int blockRadius = 0;
    int chunkRadius = 0;
    long blockCount = 1;
    private Item[] filters = {Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR};
    byte[][] posList = {
            {8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8},
            {8, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 8},
            {8, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 8},
            {8, 7, 6, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 7, 8},
            {8, 7, 6, 5, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 6, 7, 8},
            {8, 7, 6, 5, 4, 3, 3, 3, 3, 3, 3, 3, 4, 5, 6, 7, 8},
            {8, 7, 6, 5, 4, 3, 2, 2, 2, 2, 2, 3, 4, 5, 6, 7, 8},
            {8, 7, 6, 5, 4, 3, 2, 1, 1, 1, 2, 3, 4, 5, 6, 7, 8},
            {8, 7, 6, 5, 4, 3, 2, 1, 9, 1, 2, 3, 4, 5, 6, 7, 8},
            {8, 7, 6, 5, 4, 3, 2, 1, 1, 1, 2, 3, 4, 5, 6, 7, 8},
            {8, 7, 6, 5, 4, 3, 2, 2, 2, 2, 2, 3, 4, 5, 6, 7, 8},
            {8, 7, 6, 5, 4, 3, 3, 3, 3, 3, 3, 3, 4, 5, 6, 7, 8},
            {8, 7, 6, 5, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 6, 7, 8},
            {8, 7, 6, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 7, 8},
            {8, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 8},
            {8, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 8},
            {8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8}};

    public AreaCardScreen(AreaCardContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        this.container = screenContainer;

        ItemStack stack = Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.getItem() == ModItems.AREA_CARD.get())
            loadFilter(stack);
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, float partialTicks) {
        super.render(graphics, x, y, partialTicks);
        ItemStack stack = Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND);
        for (ModEditBox editBox : positionInputs) {
            if (editBox != null) {
                boolean state = stack.getOrCreateTag().getInt("Selection") == 0;
                if (state)
                    editBox.render(graphics, x, y, partialTicks);
                editBox.active = state;
            }
        }
        for (ModEditBox editBox : heightInputs) {
            if (editBox != null) {
                boolean state = stack.getOrCreateTag().getInt("Selection") == 2;
                if (state)
                    editBox.render(graphics, x, y, partialTicks);
                editBox.active = state;
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int x, int y) {
        super.renderBg(graphics, partialTicks, x, y);
        ItemStack stack = Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND);

        // left side
        graphics.blit(getTexture(), leftPos - 32, topPos + 12, 224, 56, 32, 88); // left
        graphics.blit(getTexture(), leftPos + getSizeX(), topPos + 12, 191, 68, 32, 28); // right

        // indicators
        graphics.blit(getTexture(), leftPos - 27, topPos + 21, 202 - (stack.getOrCreateTag().getInt("Selection") == 0 ? 1 : 0), 57, 1, 10); // pos
        graphics.blit(getTexture(), leftPos - 27, topPos + 41, 202 - (stack.getOrCreateTag().getInt("Selection") == 1 ? 1 : 0), 57, 1, 10); // radius
        graphics.blit(getTexture(), leftPos - 27, topPos + 61, 202 - (stack.getOrCreateTag().getInt("Selection") == 2 ? 1 : 0), 57, 1, 10); // chunk
        graphics.blit(getTexture(), leftPos - 27, topPos + 81, 202 - (stack.getOrCreateTag().getInt("Selection") == 3 ? 1 : 0), 57, 1, 10); // eject

        if (stack.getOrCreateTag().getInt("Selection") == 1) {
            graphics.blit(getTexture(), leftPos + (getSizeX() / 2) - 39, topPos + 67, 177, 150, 78, 14);
        }
        if (stack.getOrCreateTag().getInt("Selection") == 2) {
            graphics.blit(getTexture(), leftPos + 17, topPos + 52, 177, 150, 78, 14); // Count output field
            graphics.blit(getTexture(), leftPos + 115, topPos + 25, 198, 0, 56, 56); // Chunk visualisation
            graphics.blit(getTexture(), leftPos + 15, topPos + 70, 176, 165, 80, 15); // Coordinates field

            for (int i = 0; i < 17; i++) {
                for (int e = 0; e < 17; e++) {
                    if (posList[i][e] <= chunkRadius) {
                        graphics.blit(getTexture(), (int) (leftPos + 118 + (Math.ceil(e * 3))), (int) (topPos + 28 + (Math.ceil(i * 3))), 198, 57, 2, 2);
                    }
                }
            }
        }
        if (stack.getOrCreateTag().getInt("Selection") == 3) {

            for (int i = 0; i < 27; i++) {
                graphics.blit(getTexture(), leftPos + 7 + (i % 9) * 18, (int) (topPos + 27 + Math.floor(i / 9) * 18), 0, 187, 18, 18); // Count output field
                if (isHovering(7 + (i % 9) * 18 + 1, (int) (27 + Math.floor(i / 9) * 18) + 1, 16, 16, x, y)) {
                    renderGhostOverlay(graphics, filters[i].getDefaultInstance(), leftPos + 7 + (i % 9) * 18 + 1, (int) (topPos + 27 + Math.floor(i / 9) * 18 + 1), false);
                } else {
                    renderGhostOverlay(graphics, filters[i].getDefaultInstance(), leftPos + 7 + (i % 9) * 18 + 1, (int) (topPos + 27 + Math.floor(i / 9) * 18 + 1), true);
                }
            }
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY) {
        ItemStack stack = Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.getOrCreateTag().getInt("Selection") == 0) {
            graphics.drawString(Minecraft.getInstance().font, Component.translatable("item.quarry.areacard.text.pos_1").getString(), 30, 25, 1315860, false);
            graphics.drawString(Minecraft.getInstance().font, Component.translatable("item.quarry.areacard.text.pos_2").getString(), 96, 25, 1315860, false);
            graphics.drawString(Minecraft.getInstance().font, Component.literal("X").getString(), 84, 41, 1315860, false);
            graphics.drawString(Minecraft.getInstance().font, Component.literal("Y").getString(), 84, 57, 1315860, false);
            graphics.drawString(Minecraft.getInstance().font, Component.literal("Z").getString(), 84, 74, 1315860, false);
            if (!init1) {
                BlockPos pos = BlockPos.ZERO;
                if (stack.getOrCreateTag().contains("pos1"))
                    pos = NbtUtil.getPos(stack.getOrCreateTag().getCompound("pos1"));
                pos1x.setValue(String.valueOf(pos.getX()));
                pos1y.setValue(String.valueOf(pos.getY()));
                pos1z.setValue(String.valueOf(pos.getZ()));
                init1 = true;
            }
            if (!init2) {
                BlockPos pos = BlockPos.ZERO;
                if (stack.getOrCreateTag().contains("pos2"))
                    pos = NbtUtil.getPos(stack.getOrCreateTag().getCompound("pos2"));
                pos2x.setValue(String.valueOf(pos.getX()));
                pos2y.setValue(String.valueOf(pos.getY()));
                pos2z.setValue(String.valueOf(pos.getZ()));
                init2 = true;
            }
        }
        if (stack.getOrCreateTag().getInt("Selection") == 1) {
            graphics.drawString(Minecraft.getInstance().font, Component.translatable("item.quarry.areacard.text.around"), 51, 34, 1315860, false);
            drawCenteredString(graphics, Minecraft.getInstance().font, Component.literal(String.valueOf(blockRadius)), 88, 52, 1315860, false);
            drawCenteredString(graphics, Minecraft.getInstance().font, Component.literal("#").append(String.valueOf(blockCount)), 88, 71, 1315860, false);
            if (!init4) {
                BlockPos pos1 = BlockPos.ZERO;
                if (stack.getOrCreateTag().contains("pos1"))
                    pos1 = NbtUtil.getPos(stack.getOrCreateTag().getCompound("pos1"));
                BlockPos pos2 = BlockPos.ZERO;
                if (stack.getOrCreateTag().contains("pos2"))
                    pos2 = NbtUtil.getPos(stack.getOrCreateTag().getCompound("pos2"));
                int sizeX = Math.abs(pos1.getX() - pos2.getX()) + 1;
                int sizeZ = Math.abs(pos1.getZ() - pos2.getZ()) + 1;
                int blocksWidth = sizeX == sizeZ ? sizeX : 0;
                blockRadius = (blocksWidth - 1) / 2;
                refreshCount();
                init4 = true;
            }
        }
        if (stack.getOrCreateTag().getInt("Selection") == 2) {
            int multiplicator = 384;
            boolean valid = false;
            if (top.getValue().matches("^-?(\\d+$)") || top.getValue().matches("[0-9-]]")) {
                if (down.getValue().matches("^-?(\\d+$)") || down.getValue().matches("[0-9-]]")) {
                    if (Integer.parseInt(top.getValue()) > Integer.parseInt(down.getValue()))
                        valid = true;
                    multiplicator = Math.abs(Math.abs(Integer.parseInt(top.getValue())) + Math.abs(Integer.parseInt(down.getValue())));
                }
            }
            int count = ((chunkRadius * 2 + 1) * (chunkRadius * 2 + 1)) * (16 * 16 * multiplicator);
            graphics.drawString(Minecraft.getInstance().font, Component.translatable("item.quarry.areacard.text.framing").getString(), 18, 23, 1315860, false);
            drawCenteredString(graphics, Minecraft.getInstance().font, Component.literal(String.valueOf(chunkRadius)).getString(), 56, 37, 1315860, false);
            drawCenteredString(graphics, Minecraft.getInstance().font, Component.literal(valid ? "#" : "").append(String.valueOf(valid ? count : Component.translatable("item.quarry.areacard.text.illegal").getString())).getString(), 55, 56, valid ? 1315860 : 16670302, false);
            if (!init3) {
                BlockPos pos1 = BlockPos.ZERO;
                if (stack.getOrCreateTag().contains("pos1"))
                    pos1 = NbtUtil.getPos(stack.getOrCreateTag().getCompound("pos1"));
                BlockPos pos2 = BlockPos.ZERO;
                if (stack.getOrCreateTag().contains("pos2"))
                    pos2 = NbtUtil.getPos(stack.getOrCreateTag().getCompound("pos2"));
                int sizeX = Math.abs(pos1.getX() - pos2.getX()) + 1;
                int sizeZ = Math.abs(pos1.getZ() - pos2.getZ()) + 1;
                int chunkWidth = sizeX / 16 == sizeZ / 16 ? sizeX / 16 : 0;
                chunkRadius = (chunkWidth - 1) / 2;
                top.setValue(pos1.getY() + "");
                down.setValue(pos2.getY() + "");
                init3 = true;
            }
        }
        if (stack.getOrCreateTag().getInt("Selection") == 3) {
            for (int i = 0; i < 27; i++) {
                if (isHovering(7 + (i % 9) * 18 + 1, (int) (27 + Math.floor(i / 9) * 18) + 1, 16, 16, pMouseX, pMouseY)) {
                    List<Component> list = new ArrayList<>();
                    if (!filters[i].getDefaultInstance().isEmpty()) {
                        list.add(Component.literal("Filter: ").append(filters[i].getDefaultInstance().getDisplayName()));
                        list.add(Component.literal("Click with bare hand to remove filter").withStyle(ChatFormatting.YELLOW));
                    } else {
                        list.add(Component.literal("Filter: Not Set"));
                        list.add(Component.literal("Click with item in mouse to set").withStyle(ChatFormatting.YELLOW));
                    }
                    graphics.renderComponentTooltip(Minecraft.getInstance().font, list, pMouseX - leftPos, pMouseY - topPos);
                }
            }
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

        if (posMouseButton.isMouseOver(pMouseX, pMouseY)) {
            List<Component> list = new ArrayList<>();
            list.add(Component.literal("Position"));
            list.add(Component.literal("Set both positions to your desire").withStyle(ChatFormatting.YELLOW));
            graphics.renderComponentTooltip(Minecraft.getInstance().font, list, pMouseX - leftPos, pMouseY - topPos);
        }

        if (radiusMouseButton.isMouseOver(pMouseX, pMouseY)) {
            List<Component> list = new ArrayList<>();
            list.add(Component.literal("Radius"));
            list.add(Component.literal("Square radius around your current position").withStyle(ChatFormatting.YELLOW));
            graphics.renderComponentTooltip(Minecraft.getInstance().font, list, pMouseX - leftPos, pMouseY - topPos);
        }

        if (chunkMouseButton.isMouseOver(pMouseX, pMouseY)) {
            List<Component> list = new ArrayList<>();
            list.add(Component.literal("Chunk"));
            list.add(Component.literal("Square chunk radius around your current chunk").withStyle(ChatFormatting.YELLOW));
            graphics.renderComponentTooltip(Minecraft.getInstance().font, list, pMouseX - leftPos, pMouseY - topPos);
        }

        if (filterMouseButton.isMouseOver(pMouseX, pMouseY)) {
            List<Component> list = new ArrayList<>();
            list.add(Component.literal("Filter"));
            list.add(Component.literal("Set filters to get rid of useless drops").withStyle(ChatFormatting.YELLOW));
            graphics.renderComponentTooltip(Minecraft.getInstance().font, list, pMouseX - leftPos, pMouseY - topPos);
        }


        // Main Text
        drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable("item.quarry.area_card").getString(), getSizeX() / 2, 6, 1315860, false);
        graphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.quarry.inventory").getString(), 8, 93, 1315860, false);

    }

    public void drawCenteredString(GuiGraphics graphics, Font font, String text, int x, int y, int color, boolean shadow) {
        graphics.drawString(font, text, x - font.width(text) / 2, y, color, shadow);
    }

    public void drawCenteredString(GuiGraphics graphics, Font font, Component text, int x, int y, int color, boolean shadow) {
        graphics.drawString(font, text, x - font.width(text) / 2, y, color, shadow);
    }

    public void renderGhostOverlay(GuiGraphics graphics, ItemStack item, int x, int y, boolean normal) {
        graphics.pose().pushPose();
        graphics.renderItem(item, x, y);
        RenderSystem.setShaderColor(1, 1, 1, 0.65f);
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        //stack.translate(0,0,10);
        graphics.blit(normal ? Quarry.BLANK : (ClientConfig.enableQuarryDarkmode.get() ? GHOST_OVERLAY_DARK : GHOST_OVERLAY), x, y, 0, 0, 16, 16, 16, 16);
        GuiUtil.reset();
        graphics.pose().popPose();
    }

    @Override
    public void onClose() {
        savePositions();
        if (Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.AREA_CARD.get()))
            saveFilter(Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND));
        super.onClose();
    }

    @Override
    protected void init() {
        super.init();
        pos1x = new ModEditBox(font, leftPos + 24, topPos + 40, 51, 10, Component.empty());
        pos1y = new ModEditBox(font, leftPos + 24, topPos + 57, 51, 10, Component.empty());
        pos1z = new ModEditBox(font, leftPos + 24, topPos + 74, 51, 10, Component.empty());
        pos2x = new ModEditBox(font, leftPos + 96, topPos + 40, 51, 10, Component.empty());
        pos2y = new ModEditBox(font, leftPos + 96, topPos + 57, 51, 10, Component.empty());
        pos2z = new ModEditBox(font, leftPos + 96, topPos + 74, 51, 10, Component.empty());
        positionInputs = new ModEditBox[]{pos1x, pos1y, pos1z, pos2x, pos2y, pos2z};

        top = new ModEditBox(font, leftPos + 27, topPos + 73, 25, 10, Component.empty());
        down = new ModEditBox(font, leftPos + 69, topPos + 73, 25, 10, Component.empty());
        heightInputs = new ModEditBox[]{top, down};

        addElements();
    }

    protected void subInit() {
        for (ModEditBox editBox : positionInputs) {
            editBox.setBordered(false);
            editBox.setEditable(true);
            editBox.setMaxLength(9);
            editBox.setFilter(this::isInputValid);
            this.addWidget(editBox);
        }
        for (ModEditBox editBox : heightInputs) {
            editBox.setBordered(false);
            editBox.setEditable(true);
            editBox.setMaxLength(4);
            editBox.setFilter(this::isInputValid);
            this.addWidget(editBox);
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        // focus textboxes on click
        for (ModEditBox editBox : positionInputs) {
            editBox.setFocused(false);
            if (editBox.isHoveredOrFocused()) {
                setInitialFocus(editBox);
            }
        }
        for (ModEditBox editBox : heightInputs) {
            editBox.setFocused(false);
            if (editBox.isHoveredOrFocused()) {
                setInitialFocus(editBox);
            }
        }
        // add and remove filters ability
        for (int i = 0; i < 27; i++) {
            if (isHovering(7 + (i % 9) * 18 + 1, (int) (27 + Math.floor(i / 9) * 18) + 1, 16, 16, pMouseX, pMouseY)) {
                ItemStack mouse = Minecraft.getInstance().player.containerMenu.getCarried();
                if (Minecraft.getInstance().player.containerMenu.getCarried() == ItemStack.EMPTY) {
                    filters[i] = ItemStack.EMPTY.getItem();
                } else {
                    filters[i] = mouse.getItem();
                }
            }
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    protected void addElements() {
        subInit();
        ItemStack stack = Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND);
        int selection = stack.getOrCreateTag().getInt("Selection");

        // Position Fields
        if (selection == 0) {
            addRenderableOnly(new ModButton(21, 37, 60, 14, Quarry.FIELD, null, null, null, this, 60, 28, false));
            addRenderableOnly(new ModButton(21, 54, 60, 14, Quarry.FIELD, null, null, null, this, 60, 28, false));
            addRenderableOnly(new ModButton(21, 71, 60, 14, Quarry.FIELD, null, null, null, this, 60, 28, false));
            addRenderableOnly(new ModButton(93, 37, 60, 14, Quarry.FIELD, null, null, null, this, 60, 28, false));
            addRenderableOnly(new ModButton(93, 54, 60, 14, Quarry.FIELD, null, null, null, this, 60, 28, false));
            addRenderableOnly(new ModButton(93, 71, 60, 14, Quarry.FIELD, null, null, null, this, 60, 28, false));
        }
        if (selection == 1) {
            addRenderableWidget(new ModButton(63, 50, 10, 14, Quarry.COUNTER_DOWN, () -> cycleBlockRadius(-1), null, null, this, 10, 28, true));
            addRenderableWidget(new ModButton(104, 50, 10, 14, Quarry.COUNTER_UP, () -> cycleBlockRadius(1), null, null, this, 10, 28, true));
        }
        if (selection == 2) {
            addRenderableWidget(new ModButton(37, 34, 10, 14, Quarry.COUNTER_DOWN, () -> cycleChunkRadius(-1), null, null, this, 10, 28, true));
            addRenderableWidget(new ModButton(63, 34, 10, 14, Quarry.COUNTER_UP, () -> cycleChunkRadius(1), null, null, this, 10, 28, true));
        }

        darkmodeMouseButton = new ModButton(getSizeX() + 7, 17, 18, 18, Quarry.DARK_MODE, () -> {
            refreshWidgets();
            setDarkModeConfigValue(!getDarkModeConfigValue());
        }, null, null, this, 18, 36, true);
        addRenderableWidget(darkmodeMouseButton);

        posMouseButton = new ModButton(-26, 17, 18, 18, POS, () -> stack.getOrCreateTag().putInt("Selection", 0), null, null, this, 18, 36, true);
        radiusMouseButton = new ModButton(-26, 37, 18, 18, RADIUS, () -> stack.getOrCreateTag().putInt("Selection", 1), null, null, this, 18, 36, true);
        chunkMouseButton = new ModButton(-26, 57, 18, 18, CHUNK, () -> stack.getOrCreateTag().putInt("Selection", 2), null, null, this, 18, 36, true);
        filterMouseButton = new ModButton(-26, 77, 18, 18, FILTER, () -> stack.getOrCreateTag().putInt("Selection", 3), null, null, this, 18, 36, true);

        addRenderableWidget(posMouseButton);
        addRenderableWidget(radiusMouseButton);
        addRenderableWidget(chunkMouseButton);
        addRenderableWidget(filterMouseButton);
    }

    public void cycleChunkRadius(int add) {
        if (add > 0) {
            if (chunkRadius == 8) {
                chunkRadius = 0;
                return;
            }
            chunkRadius += add;
        } else {
            if (chunkRadius == 0) {
                chunkRadius = 8;
                return;
            }
            chunkRadius += add;
        }
    }

    public void cycleBlockRadius(int add) {
        if (add > 0) {
            if (blockRadius == 1024) {
                blockRadius = 0;
                refreshCount();
                return;
            }
            if (!(blockRadius + add > 1024)) blockRadius += add;
        } else {
            if (blockRadius == 0) {
                blockRadius = 1024;
                refreshCount();
                return;
            }
            if (!(blockRadius + add < 0)) blockRadius += add;
        }
        refreshCount();
    }

    public void refreshCount() {
        BlockPos pos1 = getOffsetPos(blockRadius);
        BlockPos pos2 = getOffsetPos(-blockRadius);
        blockCount = CalcUtil.getBlockCount(pos1, pos2);
    }

    public BlockPos getOffsetPos(int modifier) {
        return new BlockPos(Minecraft.getInstance().player.blockPosition().offset(modifier, modifier, modifier));
    }

    @Override
    public void resize(@NotNull Minecraft pMinecraft, int pWidth, int pHeight) {
        ArrayList<String> posValues = new ArrayList<>();
        ArrayList<String> heightValues = new ArrayList<>();
        for (ModEditBox editBox : positionInputs)
            posValues.add(editBox.getValue());
        for (ModEditBox editBox : heightInputs)
            heightValues.add(editBox.getValue());
        this.init(pMinecraft, pWidth, pHeight);
        int i = 0;
        for (ModEditBox editBox : positionInputs) {
            editBox.setValue(posValues.get(i));
            i++;
        }
        i = 0;
        for (ModEditBox editBox : heightInputs) {
            editBox.setValue(heightValues.get(i));
            i++;
        }
    }

    public void savePositions() {
        ItemStack stack = Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND);
        String[] posString = {"x", "y", "z"};
        CompoundTag filters = stack.getOrCreateTag().getCompound("Filters");
        if (stack.getOrCreateTag().getInt("Selection") == 0) {
            for (int e = 1; e <= 2; e++) {
                for (int i = 1; i < 3; i++) {
                    CompoundTag tag = stack.getOrCreateTag().getCompound("pos" + e);
                    if (!tag.contains("x") && !tag.contains("y") && !tag.contains("z")) {
                        tag.putInt("x", 0);
                        tag.putInt("y", 0);
                        tag.putInt("z", 0);
                        stack.getOrCreateTag().put("pos" + e, tag);
                    }
                }
                if (!stack.getOrCreateTag().contains("pos" + e)) return;
                BlockPos pos = NbtUtil.getPos(stack.getOrCreateTag().getCompound("pos" + e));
                ModEditBox[] posList = e == 2 ? new ModEditBox[]{pos2x, pos2y, pos2z} : new ModEditBox[]{pos1x, pos1y, pos1z};
                for (int i = 0; i < posList.length; i++) {
                    if (!posList[i].getValue().isEmpty() && !Objects.equals(posList[i].getValue(), String.valueOf(pos.getX())) && posList[i].getValue().matches("^-?(\\d+$)")) {
                        CompoundTag tag = stack.getOrCreateTag().getCompound("pos" + e);
                        int current = Integer.parseInt(posList[i].getValue());
                        if (posString[i].equals("y")) {
                            if (current > 320)
                                current = 320;
                            if (current < -64)
                                current = -64;
                        }
                        if (posString[i].equals("x") || posString[i].equals("z")) {
                            if (current > 30000000)
                                current = 30000000;
                            if (current < -30000000)
                                current = -30000000;
                        }

                        tag.putInt(posString[i], current);
                        if (!filters.isEmpty())
                            stack.getOrCreateTag().put("Filters", filters);
                        stack.getOrCreateTag().putInt("lastBlock", 0);
                        stack.getOrCreateTag().put("pos" + e, tag);
                        PacketHandler.sendToServer(new AreaCardItemPacket(Minecraft.getInstance().player.getUUID(), stack));
                    }
                }
            }
        }
        if (stack.getOrCreateTag().getInt("Selection") == 1) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("x", getOffsetPos(blockRadius).getX());
            tag.putInt("y", getOffsetPos(blockRadius).getY());
            tag.putInt("z", getOffsetPos(blockRadius).getZ());
            stack.getOrCreateTag().put("pos1", tag);
            tag = new CompoundTag();
            tag.putInt("x", getOffsetPos(-blockRadius).getX());
            tag.putInt("y", getOffsetPos(-blockRadius).getY());
            tag.putInt("z", getOffsetPos(-blockRadius).getZ());
            stack.getOrCreateTag().put("pos2", tag);
            PacketHandler.sendToServer(new AreaCardItemPacket(Minecraft.getInstance().player.getUUID(), stack));
        }
        if (stack.getOrCreateTag().getInt("Selection") == 2) {
            CompoundTag tag = new CompoundTag();
            int offset1 = 7 + (chunkRadius * 16);
            int offset2 = -8 - (chunkRadius * 16);
            ;
            BlockPos chunkMiddle = Minecraft.getInstance().player.chunkPosition().getMiddleBlockPosition(0);
            BlockPos pos1 = chunkMiddle.offset(offset1, 0, offset1);
            BlockPos pos2 = chunkMiddle.offset(offset2, 0, offset2);
            Minecraft.getInstance().level.setBlock(pos1, Blocks.RED_WOOL.defaultBlockState(), 3);
            Minecraft.getInstance().level.setBlock(pos2, Blocks.RED_WOOL.defaultBlockState(), 3);

            tag.putInt("x", pos1.getX());
            tag.putInt("y", Math.min(Integer.parseInt(top.getValue()), 320));
            tag.putInt("z", pos1.getZ());
            stack.getOrCreateTag().put("pos1", tag);
            tag = new CompoundTag();
            tag.putInt("x", pos2.getX());
            tag.putInt("y", Math.max(Integer.parseInt(down.getValue()), -64));
            tag.putInt("z", pos2.getZ());
            stack.getOrCreateTag().put("pos2", tag);
            PacketHandler.sendToServer(new AreaCardItemPacket(Minecraft.getInstance().player.getUUID(), stack));
        }
    }

    public void loadFilter(ItemStack stack) {
        if (stack.getItem() instanceof AreaCardItem) {
            CompoundTag currentTag = stack.getOrCreateTag().getCompound("Filters");

            for (int i = 0; i < 27; i++) {
                if (currentTag.contains(i + "")) {
                    CompoundTag tag = new CompoundTag();
                    tag.putString("id", currentTag.getString(i + ""));
                    tag.putByte("Count", (byte) 1);
                    filters[i] = ItemStack.of(tag).getItem();
                }
            }
        }
    }

    public void saveFilter(ItemStack stack) {
        if (stack.getItem() instanceof AreaCardItem) {
            CompoundTag tag = new CompoundTag();

            for (int i = 0; i < filters.length; i++) {
                Item filter = filters[i];
                if (!filter.getDefaultInstance().is(Items.AIR))
                    tag.putString(i + "", filter.getDefaultInstance().save(new CompoundTag()).getString("id"));
            }

            stack.getOrCreateTag().put("Filters", tag);

            PacketHandler.sendToServer(new AreaCardItemPacket(Minecraft.getInstance().player.getUUID(), stack));
        }
    }

    @Override
    protected void containerTick() {
        ItemStack stack = Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND);
        refreshDarkmode();
        super.containerTick();
        for (ModEditBox editBox : positionInputs) {
            if (stack.getOrCreateTag().getInt("Selection") == 0)
                editBox.tick();
        }
    }

    public boolean isInputValid(String string) {
        if (string.isEmpty()) return true;
        if (string.length() == 1) return string.matches("[0-9-]");
        else return string.split("")[string.split("").length - 1].matches("^-?(\\d+$)");
    }

    public boolean getDarkModeConfigValue() {
        return ClientConfig.enableQuarryDarkmode.get();
    }

    public void setDarkModeConfigValue(boolean state) {
        ClientConfig.enableQuarryDarkmode.set(state);
    }

    public void refreshDarkmode() {
        refreshWidgets();

        for (ModEditBox editBox : positionInputs)
            editBox.setTextColor(ClientConfig.enableQuarryDarkmode.get() ? FastColor.ARGB32.color(0xFF, 0x94, 0x94, 0x94) : 14737632);
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
        return 187;
    }

    @Override
    public ResourceLocation getTexture() {
        if (!getDarkModeConfigValue()) refreshDarkmode();
        String texture = "textures/gui/area_card_gui.png";
        if (getDarkModeConfigValue()) texture = "textures/gui/area_card_gui_dark.png";
        return new ResourceLocation(Quarry.MOD_ID, texture);
    }

}
