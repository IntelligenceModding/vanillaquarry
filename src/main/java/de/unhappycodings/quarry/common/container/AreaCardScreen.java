package de.unhappycodings.quarry.common.container;

import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.client.config.ClientConfig;
import de.unhappycodings.quarry.client.gui.GuiUtil;
import de.unhappycodings.quarry.client.gui.widgets.ModButton;
import de.unhappycodings.quarry.common.container.base.BaseScreen;
import de.unhappycodings.quarry.common.container.base.ModEditBox;
import de.unhappycodings.quarry.common.item.AreaCard;
import de.unhappycodings.quarry.common.networking.toServer.AreaCardItemPacket;
import de.unhappycodings.quarry.common.util.CalcUtil;
import de.unhappycodings.quarry.common.util.NbtUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AreaCardScreen extends BaseScreen<AreaCardContainer> {
    private static final int TEXT_COLOR = 0xFF141414;
    private static final int LIGHT_TEXT_COLOR = 0xFFE0E0E0;
    private static final int ERROR_COLOR = 0xFFFE67DE;
    public final Identifier GHOST_OVERLAY = Identifier.fromNamespaceAndPath(Quarry.MOD_ID, "textures/gui/slot/filter_overlay.png");
    public final Identifier GHOST_OVERLAY_DARK = Identifier.fromNamespaceAndPath(Quarry.MOD_ID, "textures/gui/slot/filter_overlay_dark.png");
    public final Identifier POS = Identifier.fromNamespaceAndPath(Quarry.MOD_ID, "textures/gui/button/pos.png");
    public final Identifier RADIUS = Identifier.fromNamespaceAndPath(Quarry.MOD_ID, "textures/gui/button/radius.png");
    public final Identifier CHUNK = Identifier.fromNamespaceAndPath(Quarry.MOD_ID, "textures/gui/button/chunk.png");
    public final Identifier FILTER = Identifier.fromNamespaceAndPath(Quarry.MOD_ID, "textures/gui/button/filter.png");
    public ModButton darkmodeMouseButton;
    public ModButton resetMouseButton;
    public ModButton posMouseButton;
    public ModButton radiusMouseButton;
    public ModButton chunkMouseButton;
    public ModButton filterMouseButton;
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
    boolean changesMade = false;
    int blockRadius = 0;
    int chunkRadius = 0;
    long blockCount = 1;
    public Item[] filters = {Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR};
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
        super(screenContainer, inv, titleIn, 176, 187);
        this.container = screenContainer;

        ItemStack stack = Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.getItem() == Quarry.AREA_CARD.get())
            loadFilter(stack);
    }

    @Override
    public void extractRenderState(@Nonnull GuiGraphicsExtractor graphics, int x, int y, float partialTicks) {
        super.extractRenderState(graphics, x, y, partialTicks);
        if (Minecraft.getInstance().player == null) return;

        ItemStack stack = Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND);
        for (ModEditBox editBox : positionInputs) {
            if (editBox != null) {
                boolean state = stack.getOrDefault(Quarry.SELECTION, 0) == 0;
                if (state)
                    editBox.extractRenderState(graphics, x, y, partialTicks);
                editBox.active = state;
            }
        }
        for (ModEditBox editBox : heightInputs) {
            if (editBox != null) {
                boolean state = stack.getOrDefault(Quarry.SELECTION, 0) == 2;
                if (state)
                    editBox.extractRenderState(graphics, x, y, partialTicks);
                editBox.active = state;
            }
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int x, int y, float partialTicks) {
        super.extractBackground(graphics, x, y, partialTicks);
        if (Minecraft.getInstance().player == null) return;

        ItemStack stack = Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND);

        // left side
        graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos - 32, topPos + 12, 224, 56, 32, 88, 256, 256); // left
        graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos + getSizeX(), topPos + 12, 191, 68, 32, 48, 256, 256); // right

        // indicators
        graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos - 27, topPos + 21, 202 - (stack.getOrDefault(Quarry.SELECTION, 0) == 0 ? 1 : 0), 57, 1, 10, 256, 256); // pos
        graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos - 27, topPos + 41, 202 - (stack.getOrDefault(Quarry.SELECTION, 0) == 1 ? 1 : 0), 57, 1, 10, 256, 256); // radius
        graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos - 27, topPos + 61, 202 - (stack.getOrDefault(Quarry.SELECTION, 0) == 2 ? 1 : 0), 57, 1, 10, 256, 256); // chunk
        graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos - 27, topPos + 81, 202 - (stack.getOrDefault(Quarry.SELECTION, 0) == 3 ? 1 : 0), 57, 1, 10, 256, 256); // eject

        if (stack.getOrDefault(Quarry.SELECTION, 0) == 1) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos + (getSizeX() / 2) - 39, topPos + 67, 177, 150, 78, 14, 256, 256);
        }
        if (stack.getOrDefault(Quarry.SELECTION, 0) == 2) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos + 17, topPos + 52, 177, 150, 78, 14, 256, 256); // Count output field
            graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos + 115, topPos + 25, 198, 0, 56, 56, 256, 256); // Chunk visualisation
            graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos + 15, topPos + 70, 176, 165, 80, 15, 256, 256); // Coordinates field

            for (int i = 0; i < 17; i++) {
                for (int e = 0; e < 17; e++) {
                    if (posList[i][e] <= chunkRadius) {
                        graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), (int) (leftPos + 118 + ((double) e * 3)), (int) (topPos + 28 + ((double) i * 3)), 198, 57, 2, 2, 256, 256);
                    }
                }
            }
        }
        if (stack.getOrDefault(Quarry.SELECTION, 0) == 3) {

            for (int i = 0; i < 27; i++) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos + 7 + (i % 9) * 18, (int) (topPos + 27 + Math.floor(i / 9D) * 18), 0, 187, 18, 18, 256, 256); // Count output field
                renderGhostOverlay(graphics, filters[i].getDefaultInstance(), leftPos + 7 + (i % 9) * 18 + 1, (int) (topPos + 27 + Math.floor((double) i / 9) * 18 + 1), !isHovering(7 + (i % 9) * 18 + 1, (int) (27 + Math.floor(i / 9D) * 18) + 1, 16, 16, x, y));
            }
        }
    }

    @Override
    protected void extractLabels(@Nonnull GuiGraphicsExtractor graphics, int pMouseX, int pMouseY) {
        if (Minecraft.getInstance().player == null) return;

        ItemStack stack = Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.getOrDefault(Quarry.SELECTION, 0) == 0) {
            graphics.text(Minecraft.getInstance().font, Component.translatable("gui.areacard.pos_1").getString(), 30, 25, TEXT_COLOR, false);
            graphics.text(Minecraft.getInstance().font, Component.translatable("gui.areacard.pos_2").getString(), 96, 25, TEXT_COLOR, false);
            graphics.text(Minecraft.getInstance().font, Component.literal("X").getString(), 84, 41, TEXT_COLOR, false);
            graphics.text(Minecraft.getInstance().font, Component.literal("Y").getString(), 84, 57, TEXT_COLOR, false);
            graphics.text(Minecraft.getInstance().font, Component.literal("Z").getString(), 84, 74, TEXT_COLOR, false);
            if (!init1) {
                BlockPos pos = BlockPos.ZERO;
                if (stack.has(Quarry.POS_1))
                    pos = NbtUtil.getPos(stack.get(Quarry.POS_1));
                pos1x.setValue(String.valueOf(pos.getX()));
                pos1y.setValue(String.valueOf(pos.getY()));
                pos1z.setValue(String.valueOf(pos.getZ()));
                init1 = true;
            }
            if (!init2) {
                BlockPos pos = BlockPos.ZERO;
                if (stack.has(Quarry.POS_2))
                    pos = NbtUtil.getPos(stack.get(Quarry.POS_2));
                pos2x.setValue(String.valueOf(pos.getX()));
                pos2y.setValue(String.valueOf(pos.getY()));
                pos2z.setValue(String.valueOf(pos.getZ()));
                init2 = true;
            }
        }
        if (stack.getOrDefault(Quarry.SELECTION, 0) == 1) {
            drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable("gui.areacard.around"), getSizeX() / 2, 34, TEXT_COLOR, false);
            drawCenteredString(graphics, Minecraft.getInstance().font, Component.literal(String.valueOf(blockRadius)), 88, 52, TEXT_COLOR, false);
            drawCenteredString(graphics, Minecraft.getInstance().font, Component.literal("#").append(String.valueOf(blockCount)), 88, 70, ClientConfig.enableQuarryDarkmode.get() ? ARGB.color(0xFF, 0x94, 0x94, 0x94) : LIGHT_TEXT_COLOR, true);
            if (!init4) {
                BlockPos pos1 = BlockPos.ZERO;
                if (stack.has(Quarry.POS_1))
                    pos1 = NbtUtil.getPos(stack.get(Quarry.POS_1));
                BlockPos pos2 = BlockPos.ZERO;
                if (stack.has(Quarry.POS_2))
                    pos2 = NbtUtil.getPos(stack.get(Quarry.POS_2));
                int sizeX = Math.abs(pos1.getX() - pos2.getX()) + 1;
                int sizeZ = Math.abs(pos1.getZ() - pos2.getZ()) + 1;
                int blocksWidth = sizeX == sizeZ ? sizeX : 0;
                blockRadius = (blocksWidth - 1) / 2;
                refreshCount();
                init4 = true;
            }
        }
        if (stack.getOrDefault(Quarry.SELECTION, 0) == 2) {
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
            drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable("gui.areacard.framing").getString(), getSizeX() / 3, 23, TEXT_COLOR, false);
            drawCenteredString(graphics, Minecraft.getInstance().font, Component.literal(String.valueOf(chunkRadius)).getString(), 56, 37, TEXT_COLOR, false);
            drawCenteredString(graphics, Minecraft.getInstance().font, Component.literal(valid ? "#" : "").append(String.valueOf(valid ? count : Component.translatable("gui.areacard.illegal").getString())).getString(), 55, 55, valid ? ClientConfig.enableQuarryDarkmode.get() ? ARGB.color(0xFF, 0x94, 0x94, 0x94) : LIGHT_TEXT_COLOR : ERROR_COLOR, true);
            if (!init3) {
                BlockPos pos1 = BlockPos.ZERO;
                if (stack.has(Quarry.POS_1))
                    pos1 = NbtUtil.getPos(stack.get(Quarry.POS_1));
                BlockPos pos2 = BlockPos.ZERO;
                if (stack.has(Quarry.POS_2))
                    pos2 = NbtUtil.getPos(stack.get(Quarry.POS_2));
                int sizeX = Math.abs(pos1.getX() - pos2.getX()) + 1;
                int sizeZ = Math.abs(pos1.getZ() - pos2.getZ()) + 1;
                int chunkWidth = sizeX / 16 == sizeZ / 16 ? sizeX / 16 : 0;
                chunkRadius = (chunkWidth - 1) / 2;
                top.setValue(pos1.getY() + "");
                down.setValue(pos2.getY() + "");
                init3 = true;
            }
        }
        if (stack.getOrDefault(Quarry.SELECTION, 0) == 3) {
            for (int i = 0; i < 27; i++) {
                if (isHovering(7 + (i % 9) * 18 + 1, (int) (27 + Math.floor(i / 9D) * 18) + 1, 16, 16, pMouseX, pMouseY)) {
                    List<Component> list = new ArrayList<>();
                    if (!filters[i].getDefaultInstance().isEmpty()) {
                        list.add(Component.translatable("gui.areacard.selection.filter").append(filters[i].getDefaultInstance().getDisplayName()));
                        list.add(Component.translatable("gui.areacard.selection.filter.reset").withStyle(ChatFormatting.YELLOW));
                    } else {
                        list.add(Component.translatable("gui.areacard.selection.filter.unset"));
                        list.add(Component.translatable("gui.areacard.selection.filter.set").withStyle(ChatFormatting.YELLOW));
                    }
                    graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, list, pMouseX, pMouseY);
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
            graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, list, pMouseX, pMouseY);
        }

        if (resetMouseButton.isMouseOver(pMouseX, pMouseY)) {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("gui.quarry.reset"));
            list.add(Component.translatable("gui.quarry.reset.switch").withStyle(ChatFormatting.YELLOW));
            list.add(Component.translatable("gui.quarry.reset.desc").withStyle(ChatFormatting.YELLOW));
            list.add(Component.translatable("gui.quarry.reset.desc_1").withStyle(ChatFormatting.YELLOW));

            graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, list, pMouseX, pMouseY);
        }

        if (posMouseButton.isMouseOver(pMouseX, pMouseY)) {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("gui.areacard.selection.position"));
            list.add(Component.translatable("gui.areacard.selection.position.description").withStyle(ChatFormatting.YELLOW));
            graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, list, pMouseX, pMouseY);
        }

        if (radiusMouseButton.isMouseOver(pMouseX, pMouseY)) {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("gui.areacard.selection.radius"));
            list.add(Component.translatable("gui.areacard.selection.radius.description").withStyle(ChatFormatting.YELLOW));
            graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, list, pMouseX, pMouseY);
        }

        if (chunkMouseButton.isMouseOver(pMouseX, pMouseY)) {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("gui.areacard.selection.chunk"));
            list.add(Component.translatable("gui.areacard.selection.chunk.description").withStyle(ChatFormatting.YELLOW));
            graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, list, pMouseX, pMouseY);
        }

        if (filterMouseButton.isMouseOver(pMouseX, pMouseY)) {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("gui.areacard.filter"));
            list.add(Component.translatable("gui.areacard.selection.filter.description").withStyle(ChatFormatting.YELLOW));
            graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, list, pMouseX, pMouseY);
        }


        // Main Text
        drawCenteredString(graphics, Minecraft.getInstance().font, Component.translatable("item.quarry.area_card").getString(), getSizeX() / 2, 6, TEXT_COLOR, false);
        graphics.text(Minecraft.getInstance().font, Component.translatable("gui.quarry.inventory").getString(), 8, 93, TEXT_COLOR, false);

    }

    public void drawCenteredString(GuiGraphicsExtractor graphics, Font font, String text, int x, int y, int color, boolean shadow) {
        graphics.text(font, text, x - font.width(text) / 2, y, color, shadow);
    }

    public void drawCenteredString(GuiGraphicsExtractor graphics, Font font, Component text, int x, int y, int color, boolean shadow) {
        graphics.text(font, text, x - font.width(text) / 2, y, color, shadow);
    }

    public void renderGhostOverlay(GuiGraphicsExtractor graphics, ItemStack item, int x, int y, boolean normal) {
        graphics.pose().pushMatrix();
        graphics.item(item, x, y);
        //stack.translate(0,0,10);
        graphics.blit(RenderPipelines.GUI_TEXTURED, normal ? Quarry.BLANK : (ClientConfig.enableQuarryDarkmode.get() ? GHOST_OVERLAY_DARK : GHOST_OVERLAY), x, y, 0, 0, 16, 16, 16, 16, 256, 256);
        GuiUtil.reset();
        graphics.pose().popMatrix();
    }

    @Override
    public void onClose() {
        if (Minecraft.getInstance().player == null) return;

        if (changesMade) {
            savePositions();
            if (Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND).is(Quarry.AREA_CARD.get()))
                saveFilter(Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND));
        } else {
            ClientPacketDistributor.sendToServer(new AreaCardItemPacket(Minecraft.getInstance().player.getUUID(), Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND)));
        }
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
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        // focus textboxes on click
        for (ModEditBox editBox : positionInputs) {
            editBox.setFocused(false);
            if (editBox.isHoveredOrFocused()) {
                setInitialFocus(editBox);
                changesMade = true;
            }
        }
        for (ModEditBox editBox : heightInputs) {
            editBox.setFocused(false);
            if (editBox.isHoveredOrFocused()) {
                setInitialFocus(editBox);
                changesMade = true;
            }
        }
        // add and remove filters ability
        for (int i = 0; i < 27; i++) {
            if (isHovering(7 + (i % 9) * 18 + 1, (int) (27 + Math.floor(i / 9D) * 18) + 1, 16, 16, event.x(), event.y())) {
                ItemStack mouse = Minecraft.getInstance().player.containerMenu.getCarried();
                if (Minecraft.getInstance().player.containerMenu.getCarried() == ItemStack.EMPTY) {
                    filters[i] = ItemStack.EMPTY.getItem();
                } else {
                    filters[i] = mouse.getItem();
                }
                changesMade = true;
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    protected void addElements() {
        subInit();
        if (Minecraft.getInstance().player == null) return;

        ItemStack stack = Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND);
        int selection = stack.getOrDefault(Quarry.SELECTION, 0);

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

        resetMouseButton = new ModButton(getSizeX() + 7, 37, 18, 18, Quarry.RESET, () -> {
            stack.set(Quarry.LAST_BLOCK, 0);
        }, null, null, this, 18, 36, true);
        addRenderableWidget(resetMouseButton);

        posMouseButton = new ModButton(-26, 17, 18, 18, POS, () -> {
            stack.set(Quarry.SELECTION, 0);
            changesMade = true;
        }, null, null, this, 18, 36, true);
        radiusMouseButton = new ModButton(-26, 37, 18, 18, RADIUS, () -> {
            stack.set(Quarry.SELECTION, 1);
            changesMade = true;
        }, null, null, this, 18, 36, true);
        chunkMouseButton = new ModButton(-26, 57, 18, 18, CHUNK, () -> {
            stack.set(Quarry.SELECTION, 2);
            changesMade = true;
        }, null, null, this, 18, 36, true);
        filterMouseButton = new ModButton(-26, 77, 18, 18, FILTER, () -> {
            stack.set(Quarry.SELECTION, 3);
            changesMade = true;
        }, null, null, this, 18, 36, true);

        addRenderableWidget(posMouseButton);
        addRenderableWidget(radiusMouseButton);
        addRenderableWidget(chunkMouseButton);
        addRenderableWidget(filterMouseButton);
    }

    public void cycleChunkRadius(int add) {
        changesMade = true;
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
        changesMade = true;
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
        if (Minecraft.getInstance().player == null) return BlockPos.ZERO;

        return new BlockPos(Minecraft.getInstance().player.blockPosition().offset(modifier, modifier, modifier));
    }

    @Override
    public void resize(int pWidth, int pHeight) {
        ArrayList<String> posValues = new ArrayList<>();
        ArrayList<String> heightValues = new ArrayList<>();
        for (ModEditBox editBox : positionInputs)
            posValues.add(editBox.getValue());
        for (ModEditBox editBox : heightInputs)
            heightValues.add(editBox.getValue());
        this.init(pWidth, pHeight);
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

    @SuppressWarnings("ConstantConditions")
    public void savePositions() {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) return;
        ItemStack stack = Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND);
        String[] posString = {"x", "y", "z"};
        if (stack.getOrDefault(Quarry.SELECTION, 0) == 0) {
            for (int e = 1; e <= 2; e++) {
                if (!stack.has(Quarry.get("pos" + e))) return;

                for (int i = 1; i < 3; i++) {
                    CompoundTag tag = stack.get(Quarry.get("pos" + e));
                    assert tag != null;
                    if (!tag.contains("x") && !tag.contains("y") && !tag.contains("z")) {
                        tag.putInt("x", 0);
                        tag.putInt("y", 0);
                        tag.putInt("z", 0);
                        stack.set(Quarry.get("pos" + e), tag);
                    }
                }

                BlockPos pos = NbtUtil.getPos(stack.get(Quarry.get("pos" + e)));
                ModEditBox[] posList = e == 2 ? new ModEditBox[]{pos2x, pos2y, pos2z} : new ModEditBox[]{pos1x, pos1y, pos1z};
                CompoundTag filters = stack.get(Quarry.FILTERS);
                for (int i = 0; i < posList.length; i++) {
                    if (!posList[i].getValue().isEmpty() && !Objects.equals(posList[i].getValue(), String.valueOf(pos.getX())) && posList[i].getValue().matches("^-?(\\d+$)")) {
                        CompoundTag tag = stack.get(Quarry.get("pos" + e));
                        int current = getCurrent(posList, i, posString);

                        tag.putInt(posString[i], current);
                        if (filters != null && !filters.isEmpty())
                            stack.set(Quarry.FILTERS, filters);
                        stack.set(Quarry.LAST_BLOCK, 0);
                        stack.set(Quarry.get("pos" + e), tag);
                        ClientPacketDistributor.sendToServer(new AreaCardItemPacket(Minecraft.getInstance().player.getUUID(), stack));
                    }
                }
            }
        }
        if (stack.getOrDefault(Quarry.SELECTION, 0) == 1) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("x", getOffsetPos(blockRadius).getX());
            tag.putInt("y", getOffsetPos(blockRadius).getY());
            tag.putInt("z", getOffsetPos(blockRadius).getZ());
            stack.set(Quarry.POS_1, tag);
            tag = new CompoundTag();
            tag.putInt("x", getOffsetPos(-blockRadius).getX());
            tag.putInt("y", getOffsetPos(-blockRadius).getY());
            tag.putInt("z", getOffsetPos(-blockRadius).getZ());
            stack.set(Quarry.POS_2, tag);
            ClientPacketDistributor.sendToServer(new AreaCardItemPacket(Minecraft.getInstance().player.getUUID(), stack));
        }
        if (stack.getOrDefault(Quarry.SELECTION, 0) == 2) {
            CompoundTag tag = new CompoundTag();
            int offset1 = 7 + (chunkRadius * 16);
            int offset2 = -8 - (chunkRadius * 16);
            BlockPos chunkMiddle = Minecraft.getInstance().player.chunkPosition().getMiddleBlockPosition(0);
            BlockPos pos1 = chunkMiddle.offset(offset1, 0, offset1);
            BlockPos pos2 = chunkMiddle.offset(offset2, 0, offset2);
            Minecraft.getInstance().level.setBlock(pos1, Blocks.RED_WOOL.defaultBlockState(), 3);
            Minecraft.getInstance().level.setBlock(pos2, Blocks.RED_WOOL.defaultBlockState(), 3);

            tag.putInt("x", pos1.getX());
            tag.putInt("y", Math.min(Integer.parseInt(top.getValue()), 320));
            tag.putInt("z", pos1.getZ());
            stack.set(Quarry.POS_1, tag);
            tag = new CompoundTag();
            tag.putInt("x", pos2.getX());
            tag.putInt("y", Math.max(Integer.parseInt(down.getValue()), -64));
            tag.putInt("z", pos2.getZ());
            stack.set(Quarry.POS_2, tag);
            ClientPacketDistributor.sendToServer(new AreaCardItemPacket(Minecraft.getInstance().player.getUUID(), stack));
        }
    }

    private int getCurrent(ModEditBox[] posList, int i, String[] posString) {
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
        return current;
    }

    public void loadFilter(ItemStack stack) {
        if (stack.getItem() instanceof AreaCard) {

            updateFilters(stack, filters, getMenu().level);
        }
    }

    public void updateFilters(ItemStack cardSlot, Item[] filters, Level level) {
        CompoundTag currentTag = cardSlot.get(Quarry.FILTERS);

        for (int i = 0; i < 27; i++) {
            if (currentTag != null && currentTag.contains(i + "")) {
                filters[i] = currentTag.read(i + "", ItemStack.CODEC).orElse(ItemStack.EMPTY).getItem();
            }
        }
    }

    public void saveFilter(ItemStack stack) {
        if (stack.getItem() instanceof AreaCard) {
            CompoundTag tag = new CompoundTag();

            for (int i = 0; i < filters.length; i++) {
                Item filter = filters[i];
                if (!filter.getDefaultInstance().is(Items.AIR))
                    tag.store(i + "", ItemStack.CODEC, filter.getDefaultInstance());
            }

            stack.set(Quarry.FILTERS, tag);

            ClientPacketDistributor.sendToServer(new AreaCardItemPacket(Minecraft.getInstance().player.getUUID(), stack));
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (Minecraft.getInstance().player == null) return;

        refreshDarkmode();

//        ItemStack stack = Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND);
//        for (ModEditBox editBox : positionInputs) {
//            if (stack.getOrDefault(Quarry.SELECTION, 0) == 0)
//                editBox.tick();
//        }
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
        ClientConfig.setQuarryDarkmode(state);
    }

    public void refreshDarkmode() {
        refreshWidgets();

        for (ModEditBox editBox : positionInputs)
            editBox.setTextColor(ClientConfig.enableQuarryDarkmode.get() ? ARGB.color(0xFF, 0x94, 0x94, 0x94) : LIGHT_TEXT_COLOR);

        for (ModEditBox heightInput : heightInputs) {
            heightInput.setTextColor(ClientConfig.enableQuarryDarkmode.get() ? ARGB.color(0xFF, 0x94, 0x94, 0x94) : LIGHT_TEXT_COLOR);
        }

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
    public Identifier getTexture() {
        if (!getDarkModeConfigValue()) refreshDarkmode();
        String texture = "textures/gui/area_card_gui.png";
        if (getDarkModeConfigValue()) texture = "textures/gui/area_card_gui_dark.png";
        return Identifier.fromNamespaceAndPath(Quarry.MOD_ID, texture);
    }

}
