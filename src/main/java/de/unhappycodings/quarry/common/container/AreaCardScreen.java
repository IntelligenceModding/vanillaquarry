package de.unhappycodings.quarry.common.container;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.client.config.ClientConfig;
import de.unhappycodings.quarry.client.gui.GuiUtil;
import de.unhappycodings.quarry.client.gui.widgets.ModButton;
import de.unhappycodings.quarry.common.container.base.BaseScreen;
import de.unhappycodings.quarry.common.container.base.ModEditBox;
import de.unhappycodings.quarry.common.item.AreaCardItem;
import de.unhappycodings.quarry.common.network.PacketHandler;
import de.unhappycodings.quarry.common.network.toserver.AreaCardItemPacket;
import de.unhappycodings.quarry.common.util.CalcUtil;
import de.unhappycodings.quarry.common.util.NbtUtil;
import de.unhappycodings.quarry.common.util.RenderUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AreaCardScreen extends BaseScreen<AreaCardContainer> {
    public static final ResourceLocation GHOST_OVERLAY = new ResourceLocation(Quarry.MOD_ID, "textures/gui/slot/filter_overlay.png");
    public static final ResourceLocation GHOST_OVERLAY_DARK = new ResourceLocation(Quarry.MOD_ID, "textures/gui/slot/filter_overlay_dark.png");
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
    public static ModButton darkmodeMouseButton;
    AreaCardContainer container;
    int selectionMode = 0;
    boolean darkmodeButtonIsHovered;
    boolean init1 = false;
    boolean init2 = false;
    boolean init3 = false;
    boolean init4 = false;
    int blockRadius = 0;
    int chunkRadius = 0;
    long blockCount = 1;
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
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int x, int y, float partialTicks) {
        super.render(matrixStack, x, y, partialTicks);
        ItemStack stack = Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND);
        for (ModEditBox editBox : positionInputs) {
            if (editBox != null && stack.getOrCreateTag().getInt("Selection") == 0)
                editBox.render(matrixStack, x, y, partialTicks);
        }
        for (ModEditBox editBox : heightInputs) {
            if (editBox != null && stack.getOrCreateTag().getInt("Selection") == 2)
                editBox.render(matrixStack, x, y, partialTicks);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void renderBg(@NotNull PoseStack matrixStack, float partialTicks, int x, int y) {
        super.renderBg(matrixStack, partialTicks, x, y);
        ItemStack stack = Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.getOrCreateTag().getInt("Selection") == 1) {
            this.blit(matrixStack, leftPos + 60, topPos + 61, 0, 150, 78, 14);
        }
        if (stack.getOrCreateTag().getInt("Selection") == 2) {
            this.blit(matrixStack, leftPos + 17, topPos + 52, 0, 150, 78, 14); // Count output field
            this.blit(matrixStack, leftPos + 115, topPos + 25, 198, 0, 56, 56); // Chunk visualisation
            this.blit(matrixStack, leftPos + 15, topPos + 70, 0, 165, 81, 15); // Coordinates field

            for (int i = 0; i < 17; i++) {
                for (int e = 0; e < 17; e++) {
                    if (posList[i][e] <= chunkRadius) {
                        blit(matrixStack, (int) (leftPos + 118 + (Math.ceil(e * 3))), (int) (topPos + 28 + (Math.ceil(i * 3))), 198, 57, 2, 2);
                    }
                }
            }
        }
    }

    @Override
    protected void renderLabels(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY) {
        ItemStack stack = Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.getOrCreateTag().getInt("Selection") == 0) {
            drawText(Component.translatable("item.quarry.areacard.text.pos_1").getString(), pPoseStack, 34, 20);
            drawText(Component.translatable("item.quarry.areacard.text.pos_2").getString(), pPoseStack, 118, 20);
            drawText(Component.literal("X").getString(), pPoseStack, 9, 35);
            drawText(Component.literal("Y").getString(), pPoseStack, 9, 52);
            drawText(Component.literal("Z").getString(), pPoseStack, 9, 69);
            drawText(Component.literal("X").getString(), pPoseStack, 184, 35);
            drawText(Component.literal("Y").getString(), pPoseStack, 184, 52);
            drawText(Component.literal("Z").getString(), pPoseStack, 184, 69);
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
            drawText(Component.translatable("item.quarry.areacard.text.around").getString(), pPoseStack, 61, 32);
            drawCenteredText(Component.literal(String.valueOf(blockRadius)).getString(), pPoseStack, 101, 46);
            drawCenteredText(Component.literal("#").append(String.valueOf(blockCount)).getString(), pPoseStack, 100, 65);
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
            drawText(Component.translatable("item.quarry.areacard.text.framing").getString(), pPoseStack, 18, 23);
            drawCenteredText(Component.literal(String.valueOf(chunkRadius)).getString(), pPoseStack, 56, 37);
            drawCenteredTextColored(Component.literal(valid ? "#" : "").append(String.valueOf(valid ? count : Component.translatable("item.quarry.areacard.text.illegal").getString())).getString(), pPoseStack, 55, 56, valid ? 1315860 : 16670302);
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

        if (darkmodeButtonIsHovered) {
            List<Component> list = new ArrayList<>();
            if (getDarkModeConfigValue()) {
                list.add(Component.translatable("gui.quarry.quarry.tooltip.darkmode.dark"));
                list.add(Component.translatable("gui.quarry.quarry.tooltip.darkmode.dark.switch").withStyle(ChatFormatting.YELLOW));
            } else {
                list.add(Component.translatable("gui.quarry.quarry.tooltip.darkmode.white"));
                list.add(Component.translatable("gui.quarry.quarry.tooltip.darkmode.white.switch").withStyle(ChatFormatting.YELLOW));
            }
            this.renderComponentTooltip(pPoseStack, list, pMouseX - leftPos, pMouseY - topPos);
        }

        // Main Text
        drawText(Component.translatable("item.quarry.areacard.text.pos").getString(), pPoseStack, 33, 6);
        drawText(Component.translatable("item.quarry.areacard.text.radius").getString(), pPoseStack, 84, 6);
        drawText(Component.translatable("item.quarry.areacard.text.chunk").getString(), pPoseStack, 140, 6);
        drawText(Component.translatable("item.quarry.areacard.text.save").getString(), pPoseStack, 88, 91);
        drawText(Component.translatable("item.quarry.areacard.text.filter").getString(), pPoseStack, 87, 108);

        // Filter Stuff
        CompoundTag tag = stack.getOrCreateTag().getCompound("Filters");
        renderGhostOverlay(pPoseStack, new ItemStack(Blocks.COBBLESTONE), 40, 120, tag.getBoolean("0"));
        renderGhostOverlay(pPoseStack, new ItemStack(Blocks.STONE), 58, 120, tag.getBoolean("1"));
        renderGhostOverlay(pPoseStack, new ItemStack(Blocks.GRAVEL), 76, 120, tag.getBoolean("2"));
        renderGhostOverlay(pPoseStack, new ItemStack(Blocks.DIRT), 94, 120, tag.getBoolean("3"));
        renderGhostOverlay(pPoseStack, new ItemStack(Blocks.SAND), 112, 120, tag.getBoolean("4"));
        renderGhostOverlay(pPoseStack, new ItemStack(Blocks.RED_SAND), 130, 120, tag.getBoolean("5"));
        renderGhostOverlay(pPoseStack, new ItemStack(Blocks.NETHERRACK), 148, 120, tag.getBoolean("6"));

    }

    public void renderGhostOverlay(PoseStack stack, ItemStack item, int x, int y, boolean normal) {
        stack.pushPose();
        RenderUtil.renderGuiItem(item, x, y);
        RenderSystem.setShaderTexture(0, normal ? Quarry.BLANK : (ClientConfig.enableQuarryDarkmode.get() ? GHOST_OVERLAY_DARK : GHOST_OVERLAY));
        RenderSystem.setShaderColor(1, 1, 1, 0.65f);
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        //stack.translate(0,0,10);
        GuiComponent.blit(stack, x, y, 0, 0, 16, 16, 16, 16);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        GuiUtil.reset();
        stack.popPose();
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    protected void init() {
        super.init();
        pos1x = new ModEditBox(font, leftPos + 20, topPos + 34, 71, 10, Component.empty());
        pos1y = new ModEditBox(font, leftPos + 20, topPos + 51, 71, 10, Component.empty());
        pos1z = new ModEditBox(font, leftPos + 20, topPos + 68, 71, 10, Component.empty());
        pos2x = new ModEditBox(font, leftPos + 107, topPos + 34, 71, 10, Component.empty());
        pos2y = new ModEditBox(font, leftPos + 107, topPos + 51, 71, 10, Component.empty());
        pos2z = new ModEditBox(font, leftPos + 107, topPos + 68, 71, 10, Component.empty());
        positionInputs = new ModEditBox[]{pos1x, pos1y, pos1z, pos2x, pos2y, pos2z};

        top = new ModEditBox(font, leftPos + 27, topPos + 73, 25, 10, Component.empty());
        down = new ModEditBox(font, leftPos + 69, topPos + 73, 25, 10, Component.empty());
        heightInputs = new ModEditBox[]{top, down};

        subInit();
        addElements();
    }

    @Override
    protected boolean isHovering(int pX, int pY, int pWidth, int pHeight, double pMouseX, double pMouseY) {
        if (darkmodeMouseButton != null && darkmodeMouseButton.isMouseOver(pMouseX, pMouseY)) {
            darkmodeButtonIsHovered = true;
        } else {
            if (darkmodeButtonIsHovered) darkmodeButtonIsHovered = false;
        }
        return super.isHovering(pX, pY, pWidth, pHeight, pMouseX, pMouseY);
    }

    protected void subInit() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        for (ModEditBox editBox : positionInputs) {
            editBox.setBordered(false);
            editBox.setEditable(true);
            editBox.setMaxLength(10);
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
        for (ModEditBox editBox : positionInputs) {
            editBox.setFocus(false);
            if (editBox.isHoveredOrFocused()) {
                setInitialFocus(editBox);
            }
        }
        for (ModEditBox editBox : heightInputs) {
            editBox.setFocus(false);
            if (editBox.isHoveredOrFocused()) {
                setInitialFocus(editBox);
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    protected void addElements() {
        boolean darkmode = getDarkModeConfigValue();
        ItemStack stack = Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND);
        // Position Fields
        if (stack.getOrCreateTag().getInt("Selection") == 0) {
            addRenderableOnly(new ModButton(18, 31, 75, 14, darkmode ? Quarry.FIELD_DARK : Quarry.FIELD, null, null, null, this, 75, 28, false));
            addRenderableOnly(new ModButton(18, 48, 75, 14, darkmode ? Quarry.FIELD_DARK : Quarry.FIELD, null, null, null, this, 75, 28, false));
            addRenderableOnly(new ModButton(18, 65, 75, 14, darkmode ? Quarry.FIELD_DARK : Quarry.FIELD, null, null, null, this, 75, 28, false));
            addRenderableOnly(new ModButton(105, 31, 75, 14, darkmode ? Quarry.FIELD_DARK : Quarry.FIELD, null, null, null, this, 75, 28, false));
            addRenderableOnly(new ModButton(105, 48, 75, 14, darkmode ? Quarry.FIELD_DARK : Quarry.FIELD, null, null, null, this, 75, 28, false));
            addRenderableOnly(new ModButton(105, 65, 75, 14, darkmode ? Quarry.FIELD_DARK : Quarry.FIELD, null, null, null, this, 75, 28, false));
        }
        if (stack.getOrCreateTag().getInt("Selection") == 1) {
            addRenderableWidget(new ModButton(75, 43, 10, 14, darkmode ? Quarry.COUNTER_DOWN_DARK : Quarry.COUNTER_DOWN, () -> cycleBlockRadius(-1), null, null, this, 10, 28, true));
            addRenderableWidget(new ModButton(115, 43, 10, 14, darkmode ? Quarry.COUNTER_UP_DARK : Quarry.COUNTER_UP, () -> cycleBlockRadius(1), null, null, this, 10, 28, true));
        }
        if (stack.getOrCreateTag().getInt("Selection") == 2) {
            addRenderableWidget(new ModButton(37, 34, 10, 14, darkmode ? Quarry.COUNTER_DOWN_DARK : Quarry.COUNTER_DOWN, () -> cycleChunkRadius(-1), null, null, this, 10, 28, true));
            addRenderableWidget(new ModButton(63, 34, 10, 14, darkmode ? Quarry.COUNTER_UP_DARK : Quarry.COUNTER_UP, () -> cycleChunkRadius(1), null, null, this, 10, 28, true));
        }

        darkmodeMouseButton = new ModButton(182, 5, 9, 9, darkmode ? Quarry.DARK_MODE : Quarry.WHITE_MODE, () -> {
            refreshWidgets();
            setDarkModeConfigValue(!getDarkModeConfigValue());
        }, null, null, this, 9, 18, true);
        addRenderableWidget(darkmodeMouseButton);
        addRenderableWidget(new ModButton(40, 120, 16, 16, Quarry.BLANK, () -> changeFilter(0), null, null, this, 16, 16, true));
        addRenderableWidget(new ModButton(58, 120, 16, 16, Quarry.BLANK, () -> changeFilter(1), null, null, this, 16, 16, true));
        addRenderableWidget(new ModButton(76, 120, 16, 16, Quarry.BLANK, () -> changeFilter(2), null, null, this, 16, 16, true));
        addRenderableWidget(new ModButton(94, 120, 16, 16, Quarry.BLANK, () -> changeFilter(3), null, null, this, 16, 16, true));
        addRenderableWidget(new ModButton(112, 120, 16, 16, Quarry.BLANK, () -> changeFilter(4), null, null, this, 16, 16, true));
        addRenderableWidget(new ModButton(130, 120, 16, 16, Quarry.BLANK, () -> changeFilter(5), null, null, this, 16, 16, true));
        addRenderableWidget(new ModButton(148, 120, 16, 16, Quarry.BLANK, () -> changeFilter(6), null, null, this, 16, 16, true));

        addRenderableWidget(new ModButton(23, 6, 7, 7, stack.getOrCreateTag().getInt("Selection") == 0 ? Quarry.SELECTOR : Quarry.SELECTOR_OFF, () -> stack.getOrCreateTag().putInt("Selection", 0), null, null, this, 7, 14, true));
        addRenderableWidget(new ModButton(74, 6, 7, 7, stack.getOrCreateTag().getInt("Selection") == 1 ? Quarry.SELECTOR : Quarry.SELECTOR_OFF, () -> stack.getOrCreateTag().putInt("Selection", 1), null, null, this, 7, 14, true));
        addRenderableWidget(new ModButton(130, 6, 7, 7, stack.getOrCreateTag().getInt("Selection") == 2 ? Quarry.SELECTOR : Quarry.SELECTOR_OFF, () -> stack.getOrCreateTag().putInt("Selection", 2), null, null, this, 7, 14, true));

        addRenderableWidget(new ModButton(70, 88, 59, 14, darkmode ? Quarry.SAVE_DARK : Quarry.SAVE, () -> savePositions(), null, null, this, 59, 28, true));
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

    public void changeFilter(int index) {
        ItemStack stack = Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.getItem() instanceof AreaCardItem) {
            CompoundTag tag = new CompoundTag();
            CompoundTag currentTag = stack.getOrCreateTag().getCompound("Filters");
            for (int i = 0; i < 9; i++) {
                if (currentTag.contains(String.valueOf(i)) && i != index) {
                    if (currentTag.getBoolean(String.valueOf(i))) tag.putBoolean(String.valueOf(i), true);
                }
            }
            if (!currentTag.getBoolean(String.valueOf(index))) tag.putBoolean(String.valueOf(index), true);
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
    }

    public void refreshWidgets() {
        clearWidgets();
        addElements();
    }

    @Override
    public int getSizeX() {
        return 197;
    }

    @Override
    public int getSizeY() {
        return 149;
    }

    @Override
    public ResourceLocation getTexture() {
        if (!getDarkModeConfigValue()) refreshDarkmode();
        String texture = "textures/gui/area_card_gui.png";
        if (getDarkModeConfigValue()) texture = "textures/gui/area_card_gui_dark.png";
        return new ResourceLocation(Quarry.MOD_ID, texture);
    }

    public void drawCenteredText(String text, PoseStack stack, int x, int y) {
        Minecraft.getInstance().font.draw(stack, text, x - (Minecraft.getInstance().font.width(text) / 2f), y, 1315860);
    }

    public void drawCenteredTextColored(String text, PoseStack stack, int x, int y, int color) {
        Minecraft.getInstance().font.draw(stack, text, x - (Minecraft.getInstance().font.width(text) / 2f), y, color);
    }

    public void drawText(String text, PoseStack stack, int x, int y) {
        Minecraft.getInstance().font.draw(stack, text, x, y, 1315860);
    }
}
