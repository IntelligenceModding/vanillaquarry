package de.unhappycodings.quarry.common.blockentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.client.config.ClientConfig;
import de.unhappycodings.quarry.common.block.QuarryBlock;
import de.unhappycodings.quarry.common.blockentity.QuarryEntity;
import de.unhappycodings.quarry.common.util.CalcUtil;
import de.unhappycodings.quarry.common.util.NbtUtil;
import de.unhappycodings.quarry.common.util.TextUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

import java.awt.*;

public class QuarryEntityRenderer implements BlockEntityRenderer<QuarryEntity, QuarryEntityRenderer.State> {

    private static final int YELLOW = 0xFFFFFF00;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int RED = 0xFFFF0000;
    private static final int GREEN = 0xFF00FF00;
    private static final int ORANGE = 0xFFFF8800;
    private static final int PANE = 0x88000000;
    private static final int FULL_BRIGHT = 15728880;

    public QuarryEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static String formatTime(double seconds) {
        double weeks = seconds / 604800f;
        seconds %= 604800f;

        double days = seconds / 86400f;
        seconds %= 86400;

        double hours = seconds / 3600f;
        seconds %= 3600;

        double minutes = seconds / 60f;
        seconds %= 60;

        StringBuilder sb = new StringBuilder();

        if (Math.floor(weeks) > 0) sb.append((int) Math.floor(weeks)).append("w ");
        if (Math.floor(days) > 0) sb.append((int) Math.floor(days)).append("d ");
        if (Math.floor(hours) > 0) sb.append((int) Math.floor(hours)).append("h ");
        if (Math.floor(minutes) > 0 && Math.floor(weeks) < 1) sb.append((int) Math.floor(minutes)).append("m ");
        if ((Math.floor(seconds) > 0 || sb.isEmpty()) && Math.floor(days) < 1 && Math.floor(weeks) < 1) {
            sb.append(Math.round(seconds * 10) / 10f).append("s");
        }

        return sb.toString().trim();
    }

    public static int lerpColor3(int color1, int color2, int color3, float percent) {
        percent = Math.max(0f, Math.min(100f, percent));

        int start;
        int end;
        float t;
        if (percent <= 50f) {
            start = color1;
            end = color2;
            t = percent / 50f;
        } else {
            start = color2;
            end = color3;
            t = (percent - 50f) / 50f;
        }

        int a1 = (start >> 24) & 0xFF;
        int r1 = (start >> 16) & 0xFF;
        int g1 = (start >> 8) & 0xFF;
        int b1 = start & 0xFF;

        int a2 = (end >> 24) & 0xFF;
        int r2 = (end >> 16) & 0xFF;
        int g2 = (end >> 8) & 0xFF;
        int b2 = end & 0xFF;

        int a = (int) (a1 + (a2 - a1) * t);
        int r = (int) (r1 + (r2 - r1) * t);
        int g = (int) (g1 + (g2 - g1) * t);
        int b = (int) (b1 + (b2 - b1) * t);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(QuarryEntity blockEntity, State state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.render = ClientConfig.enableQuarryHolograph.get();
        if (!state.render) return;

        BlockState blockState = blockEntity.getBlockState();
        state.facing = blockState.getValue(QuarryBlock.FACING);
        state.energy = blockEntity.isEnergyPowered();
        state.owner = blockEntity.getOwner().split("@")[0];
        state.locked = blockEntity.getLocked();
        state.loop = blockEntity.getLoop();
        state.filter = blockEntity.getFilter();
        state.eject = blockEntity.getEject() > 0;
        state.skip = blockEntity.getSkip();
        state.replace = blockEntity.getReplace();
        state.active = blockState.getValue(QuarryBlock.ACTIVE);
        state.outOfRange = blockEntity.outOfRange;
        state.inventoryFull = blockEntity.inventoryFull;
        state.skippingAir = blockEntity.skippingAir;
        state.modeText = getMode(blockEntity.getMode());
        state.percentage = 0;
        state.lastBlock = 0;
        state.remainingFuel = "";
        state.remainingWork = "";

        ItemStack stack = blockEntity.getInventory().getItem(12);
        state.hasCard = stack.is(Quarry.AREA_CARD.get());
        if (!state.hasCard) return;

        int blockCount = CalcUtil.getBlockAmount(NbtUtil.getPos(stack.get(Quarry.POS_1)), NbtUtil.getPos(stack.get(Quarry.POS_2)));
        state.lastBlock = stack.has(Quarry.LAST_BLOCK) ? stack.get(Quarry.LAST_BLOCK) : 0;
        state.percentage = blockCount <= 0 ? 0 : (float) state.lastBlock / (float) blockCount * 100f;
        state.blink = blockEntity.getLevel() != null && blockEntity.getLevel().getGameTime() / 10 % 2 == 0;

        if (blockEntity.getLevel() == null || blockCount <= 0) return;

        float runsPerSec = 20f / blockEntity.getTicksForSpeed(blockEntity.getSpeed());
        state.remainingFuel = formatTime(blockEntity.getStoredFuelTime() / (runsPerSec * CalcUtil.getNeededTicks(blockEntity.getMode(), blockEntity.getSpeed(), blockEntity.isEnergyPowered())));
        state.remainingWork = formatTime((blockCount - state.lastBlock) / runsPerSec);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (!state.render) return;

        poseStack.pushPose();
        poseStack.translate(0.5 + state.facing.getStepX() * 0.5, 1.0, 0.5 + state.facing.getStepZ() * 0.5);
        handleRotate(poseStack, state.facing);
        poseStack.translate(0, 0.5, 0.01);
        poseStack.scale(0.005f, -0.005f, 0.005f);

        drawCenteredText(submitNodeCollector, poseStack, 100, 40, Component.literal(TextUtil.truncateWithEllipsis(getTranslatable(state.energy ? "gui.quarry.holo.fe_quarry" : "gui.quarry.holo.quarry"), 55)).withColor(WHITE).withStyle(ChatFormatting.UNDERLINE), WHITE);
        drawCenteredText(submitNodeCollector, poseStack, 100, 55, Component.literal(state.owner).withColor(WHITE), WHITE);
        renderSettings(state, poseStack, submitNodeCollector);

        if (state.hasCard) {
            int statusColor = !state.active ? RED : state.outOfRange || state.inventoryFull || state.skippingAir ? ORANGE : GREEN;
            String statusKey = !state.active ? "gui.quarry.holo.stop" : state.outOfRange ? "gui.quarry.holo.outofrange" : state.inventoryFull ? "gui.quarry.holo.invfull" : state.skippingAir ? "gui.quarry.holo.skipping" : "gui.quarry.holo.mining";
            Component status = Component.literal(TextUtil.truncateWithEllipsis(getTranslatable(statusKey), 80))
                    .withStyle(state.blink && ((state.outOfRange || state.inventoryFull) && state.active) ? ChatFormatting.UNDERLINE : ChatFormatting.RESET)
                    .withColor(statusColor);

            drawText(submitNodeCollector, poseStack, 40, 108, status, statusColor);
            drawRightboundText(submitNodeCollector, poseStack, 160, 108, Component.literal(state.modeText).withColor(WHITE), WHITE);
            drawText(submitNodeCollector, poseStack, 40, 118, Component.literal(Math.round(state.percentage * 100.0) / 100.0 + "%").withColor(lerpColor3(Color.RED.getRGB(), Color.YELLOW.getRGB(), Color.GREEN.getRGB(), state.percentage)), lerpColor3(Color.RED.getRGB(), Color.YELLOW.getRGB(), Color.GREEN.getRGB(), state.percentage));
            drawCenteredText(submitNodeCollector, poseStack, 100, 118, Component.literal("#" + state.lastBlock).withColor(WHITE), WHITE);
            drawRightboundText(submitNodeCollector, poseStack, 160, 118, Component.literal("100%").withColor(GREEN), GREEN);
            renderRemainingTime(state, poseStack, submitNodeCollector);
        }

        renderProgressBar(poseStack, state.percentage, submitNodeCollector);
        poseStack.popPose();
    }

    private static void renderRemainingTime(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        drawCenteredText(submitNodeCollector, poseStack, 100, 145, Component.literal(getTranslatable("gui.quarry.holo.estimate")).withColor(WHITE), WHITE);
        drawText(submitNodeCollector, poseStack, 40, 155, Component.literal(TextUtil.truncateWithEllipsis(getTranslatable("gui.quarry.holo.fuel"), 40)).withColor(WHITE), WHITE);
        drawRightboundText(submitNodeCollector, poseStack, 160, 155, Component.literal(state.remainingFuel).withColor(WHITE), WHITE);
        drawText(submitNodeCollector, poseStack, 40, 165, Component.literal(TextUtil.truncateWithEllipsis(getTranslatable("gui.quarry.holo.work"), 40)).withColor(WHITE), WHITE);
        drawRightboundText(submitNodeCollector, poseStack, 160, 165, Component.literal(state.remainingWork).withColor(WHITE), WHITE);
    }

    private static void handleRotate(PoseStack poseStack, Direction facing) {
        switch (facing) {
            case SOUTH -> {
            }
            case NORTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(-90));
        }
    }

    private static void renderProgressBar(PoseStack poseStack, float percentage, SubmitNodeCollector submitNodeCollector) {
        poseStack.pushPose();
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.translate(-60, 30, 0);
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.textBackground(), (pose, buffer) -> renderBar(pose, buffer, -58, 30, 235, 10, percentage, true));
        poseStack.popPose();
    }

    private static void renderSettings(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        drawText(submitNodeCollector, poseStack, 40, 70, Component.literal(TextUtil.truncateWithEllipsis(getTranslatable("gui.quarry.holo.security"), 60)).withColor(state.locked ? YELLOW : GREEN), state.locked ? YELLOW : GREEN);
        drawText(submitNodeCollector, poseStack, 40, 80, Component.literal(TextUtil.truncateWithEllipsis(getTranslatable("gui.quarry.holo.loop"), 60)).withColor(state.loop ? GREEN : RED), state.loop ? GREEN : RED);
        drawText(submitNodeCollector, poseStack, 40, 90, Component.literal(TextUtil.truncateWithEllipsis(getTranslatable("gui.quarry.holo.filter"), 60)).withColor(state.filter ? GREEN : RED), state.filter ? GREEN : RED);
        drawRightboundText(submitNodeCollector, poseStack, 160, 70, Component.literal(TextUtil.truncateWithEllipsis(getTranslatable("gui.quarry.holo.inout"), 60)).withColor(state.eject ? GREEN : RED), state.eject ? GREEN : RED);
        drawRightboundText(submitNodeCollector, poseStack, 160, 80, Component.literal(TextUtil.truncateWithEllipsis(getTranslatable("gui.quarry.holo.skip"), 60)).withColor(state.skip ? GREEN : RED), state.skip ? GREEN : RED);
        drawRightboundText(submitNodeCollector, poseStack, 160, 90, Component.literal(TextUtil.truncateWithEllipsis(getTranslatable("gui.quarry.holo.replace"), 60)).withColor(state.replace ? GREEN : RED), state.replace ? GREEN : RED);
    }

    private static String getTranslatable(String key) {
        return Component.translatable(key).getString();
    }

    @NotNull
    @Override
    public AABB getRenderBoundingBox(QuarryEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 2, pos.getY() + 3, pos.getZ() + 2);
    }

    private static void renderBar(PoseStack.Pose pose, VertexConsumer vertex, int x, int y, int width, int height, float value, boolean outline) {
        float filledWidth = width / 100f * value;

        for (int i = 0; i < filledWidth; i++) {
            drawQuad(pose, vertex, x + i, y, 1, height, lerpColor3(Color.RED.getRGB(), Color.YELLOW.getRGB(), Color.GREEN.getRGB(), map(i, 0, width, 0, 100)));
        }

        if (outline) {
            int frameSpace = 2;
            drawQuad(pose, vertex, x - frameSpace, y - frameSpace, width + frameSpace * 2, 1, WHITE);
            drawQuad(pose, vertex, x - frameSpace, y + height + frameSpace - 1, width + frameSpace * 2, 1, WHITE);
            drawQuad(pose, vertex, x - frameSpace, y - frameSpace, 1, height + frameSpace * 2, WHITE);
            drawQuad(pose, vertex, x + width + frameSpace - 1, y - frameSpace + 1, 1, height + frameSpace * 2 - 1, WHITE);
        }
    }

    private static float map(float value, float oldMin, float oldMax, float newMin, float newMax) {
        return ((value - oldMin) / (oldMax - oldMin)) * (newMax - newMin) + newMin;
    }

    private static void drawQuad(PoseStack.Pose pose, VertexConsumer vertex, int x, int y, int w, int h, int color) {
        vertex.addVertex(pose, x, y + h, 0.001f).setColor(color).setLight(FULL_BRIGHT);
        vertex.addVertex(pose, x + w, y + h, 0.001f).setColor(color).setLight(FULL_BRIGHT);
        vertex.addVertex(pose, x + w, y, 0.001f).setColor(color).setLight(FULL_BRIGHT);
        vertex.addVertex(pose, x, y, 0.001f).setColor(color).setLight(FULL_BRIGHT);
    }

    private static String getMode(int mode) {
        return switch (mode) {
            case 0 -> Component.translatable("gui.quarry.mode.default").getString();
            case 1 -> Component.translatable("gui.quarry.mode.efficient").getString();
            case 2 -> Component.translatable("gui.quarry.mode.fortune").getString();
            case 3 -> Component.translatable("gui.quarry.mode.silktouch").getString();
            case 4 -> Component.translatable("gui.quarry.mode.void").getString();
            default -> "unknown";
        };
    }

    private static void drawText(SubmitNodeCollector submitNodeCollector, PoseStack poseStack, int x, int y, Component text, int color) {
        submitNodeCollector.submitText(poseStack, x - 100, y - 100, text.getVisualOrderText(), false, Font.DisplayMode.NORMAL, FULL_BRIGHT, color, 0, 0);
    }

    private static void drawRightboundText(SubmitNodeCollector submitNodeCollector, PoseStack poseStack, int x, int y, Component text, int color) {
        int textWidth = Minecraft.getInstance().font.width(text);
        drawText(submitNodeCollector, poseStack, -textWidth + x, y, text, color);
    }

    private static void drawCenteredText(SubmitNodeCollector submitNodeCollector, PoseStack poseStack, int x, int y, Component text, int color) {
        int textWidth = Minecraft.getInstance().font.width(text);
        drawText(submitNodeCollector, poseStack, -textWidth / 2 + x, y, text, color);
    }

    public static class State extends BlockEntityRenderState {
        private boolean render;
        private Direction facing = Direction.NORTH;
        private String owner = "";
        private String modeText = "";
        private String remainingFuel = "";
        private String remainingWork = "";
        private boolean energy;
        private boolean locked;
        private boolean loop;
        private boolean filter;
        private boolean eject;
        private boolean skip;
        private boolean replace;
        private boolean active;
        private boolean outOfRange;
        private boolean inventoryFull;
        private boolean skippingAir;
        private boolean hasCard;
        private boolean blink;
        private float percentage;
        private int lastBlock;
    }
}
