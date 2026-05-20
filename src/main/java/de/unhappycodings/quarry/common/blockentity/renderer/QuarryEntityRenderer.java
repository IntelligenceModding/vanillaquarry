package de.unhappycodings.quarry.common.blockentity.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QuarryEntityRenderer implements BlockEntityRenderer<QuarryEntity> {

    private final int YELLOW = 0xFFFF00;
    private final int WHITE = 0xFFFFFF;
    private final int RED = 0xFF0000;
    private final int GREEN = 0x00FF00;
    private final int ORANGE = 0xFF8800;
    private boolean blink;
    private long lastBlink = 0;

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

        if (Math.floor(weeks) > 0) {
            sb.append((int) Math.floor(weeks)).append("w ");
        }

        if (Math.floor(days) > 0) {
            sb.append((int) Math.floor(days)).append("d ");
        }

        if (Math.floor(hours) > 0) {
            sb.append((int) Math.floor(hours)).append("h ");
        }

        if (Math.floor(minutes) > 0 && Math.floor(weeks) < 1) {
            sb.append((int) Math.floor(minutes)).append("m ");
        }

        if ((Math.floor(seconds) > 0 || sb.isEmpty()) && Math.floor(days) < 1 && Math.floor(weeks) < 1) {
            sb.append(Math.round(seconds * 10) / 10f).append("s");
        }

        return sb.toString().trim();
    }

    public static int lerpColor3(int color1, int color2, int color3, float percent) {
        percent = Math.max(0f, Math.min(100f, percent));

        int start, end;
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

    public float map(float value, float oldMin, float oldMax, float newMin, float newMax) {
        return ((value - oldMin) / (oldMax - oldMin)) * (newMax - newMin) + newMin;
    }

    @Override
    public void render(QuarryEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (ClientConfig.enableQuarryHolograph.get()) {
            BlockState state = blockEntity.getBlockState();
            Direction facing = state.getValue(QuarryBlock.FACING);

            poseStack.pushPose();

            // Render transparent background pane
            poseStack.translate(0.5 + (facing == Direction.NORTH ? 0.5f : facing == Direction.SOUTH ? -0.5f : 0) + facing.getStepX() * 0.5, 1.0, 0.5 + (facing == Direction.EAST ? 0.5f : facing == Direction.WEST ? -0.5f : 0) + facing.getStepZ() * 0.5);
            renderTransparentPane(facing, poseStack, buffer, packedLight, packedOverlay);
            poseStack.popPose();

            poseStack.pushPose();
            RenderSystem.disableCull();

            // Translate back to the blocks front
            poseStack.translate(0.5 + facing.getStepX() * 0.5, 1.0, 0.5 + facing.getStepZ() * 0.5);

            // Rotate rendering based on the facing direction
            handleRotate(poseStack, facing);

            poseStack.pushPose();
            poseStack.translate(0, 0.5, 0.01);
            poseStack.scale(0.005f, -0.005f, 0.005f);

            drawCenteredText(100, 40, Component.literal(TextUtil.truncateWithEllipsis(getTranslatable("gui.quarry.holo.quarry"), 55)).withColor(WHITE).withStyle(ChatFormatting.UNDERLINE), poseStack, buffer);
            drawCenteredText(100, 55, Component.literal(blockEntity.getOwner().split("@")[0]).withColor(WHITE), poseStack, buffer);

            // Render settings text as loop filter etc
            renderSettings(blockEntity, poseStack, buffer);

            ItemStack stack = blockEntity.getInventory().getStackInSlot(12);

            if (stack.is(Quarry.AREA_CARD.get())) {
                // Get values to variables
                int blockCount = CalcUtil.getBlockAmount(NbtUtil.getPos(stack.get(Quarry.POS_1)), NbtUtil.getPos(stack.get(Quarry.POS_2)));
                int lastBlock = stack.has(Quarry.LAST_BLOCK) ? stack.get(Quarry.LAST_BLOCK) : 0;
                float percentage = (float) lastBlock / (float) blockCount * 100f;
                long gameTime = blockEntity.getLevel().getGameTime();
                if (gameTime - lastBlink >= 10) {
                    blink = !blink;
                    lastBlink = gameTime;
                }

                boolean active = blockEntity.getBlockState().getValue(QuarryBlock.ACTIVE);

                boolean outOfRange = blockEntity.outOfRange;
                boolean inventoryFull = blockEntity.inventoryFull;
                boolean skippingAir = blockEntity.skippingAir;

                drawText(40, 108, Component.literal(TextUtil.truncateWithEllipsis(getTranslatable(!active ? "gui.quarry.holo.stop" : outOfRange ? "gui.quarry.holo.outofrange" : inventoryFull ? "gui.quarry.holo.invfull" : skippingAir ? "gui.quarry.holo.skipping" : "gui.quarry.holo.mining"), 80)).withStyle(blink && ((outOfRange || inventoryFull) && active) ? ChatFormatting.UNDERLINE : ChatFormatting.RESET).withColor(!active ? RED : outOfRange || inventoryFull || skippingAir ? ORANGE : GREEN), poseStack, buffer);
                drawRightboundText(160, 108, Component.literal(getMode(blockEntity.getMode())), poseStack, buffer);

                drawText(40, 118, Component.literal(Math.round(percentage * 100.0) / 100.0 + "%").withColor(lerpColor3(Color.RED.getRGB(), Color.YELLOW.getRGB(), Color.GREEN.getRGB(), percentage)), poseStack, buffer);
                drawCenteredText(100, 118, Component.literal("#" + lastBlock).withColor(WHITE), poseStack, buffer);
                drawRightboundText(160, 118, Component.literal("100%").withColor(GREEN), poseStack, buffer);

                // Calculate burntime based on all burnables in fuel slots
                List<ItemStack> fuelSlots = new ArrayList<>();
                for (int i = 0; i <= 5; i++)
                    fuelSlots.add(blockEntity.getItem(i, blockEntity.getLevel(), blockEntity.getBlockPos()));

                long totalBurnTime = blockEntity.getBurnTime();
                for (ItemStack itemStack : fuelSlots)
                    for (int i = 0; i < itemStack.getCount(); i++)
                        totalBurnTime += itemStack.getBurnTime(RecipeType.SMELTING);

                // Render remaining fuel and work time
                renderRemainingTime(blockEntity, poseStack, totalBurnTime, blockCount - lastBlock, buffer);

                // Render bar of mine progress
                renderProgressBar(poseStack, percentage, buffer);
            } else {
                // Render bar of mine progress
                renderProgressBar(poseStack, 0, buffer);
            }

            poseStack.popPose();
            RenderSystem.enableCull();
            poseStack.popPose();

        }

    }

    void renderRemainingTime(QuarryEntity blockEntity, PoseStack poseStack, long totalBurnTime, int blocksRemain, MultiBufferSource buffer) {
        float runsPerSec = 20f / blockEntity.getTicksForSpeed(blockEntity.getSpeed()); // 20 ticks

        drawCenteredText(100, 145, Component.literal(getTranslatable("gui.quarry.holo.estimate")).withColor(WHITE), poseStack, buffer);
        drawText(40, 155, Component.literal(TextUtil.truncateWithEllipsis(getTranslatable("gui.quarry.holo.fuel"), 40)).withColor(WHITE), poseStack, buffer);
        drawRightboundText(160, 155, Component.literal(formatTime(totalBurnTime / (runsPerSec * CalcUtil.getNeededTicks(blockEntity.getMode(), blockEntity.getSpeed())))).withColor(WHITE), poseStack, buffer);
        drawText(40, 165, Component.literal(TextUtil.truncateWithEllipsis(getTranslatable("gui.quarry.holo.work"), 40)).withColor(WHITE), poseStack, buffer);
        drawRightboundText(160, 165, Component.literal(formatTime(blocksRemain / runsPerSec)).withColor(WHITE), poseStack, buffer);
    }

    private void handleRotate(PoseStack poseStack, Direction facing) {
        switch (facing) {
            case SOUTH -> {
            }
            case NORTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(-90));
        }
    }

    private void renderTransparentPane(Direction facing, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(ResourceLocation.fromNamespaceAndPath(Quarry.MOD_ID, "block/quarry_background"));

        handleRotate(poseStack, facing);
        poseStack.translate(0, 0, 0.005);

        VertexConsumer consumer = buffer.getBuffer(RenderType.translucentMovingBlock());
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        consumer.addVertex(matrix, 0, 0, 0).setColor(255, 255, 255, 255).setUv(u0, v1).setLight(packedLight).setOverlay(packedOverlay).setNormal(pose, 0, 1, 0);
        consumer.addVertex(matrix, 1, 0, 0).setColor(255, 255, 255, 255).setUv(u1, v1).setLight(packedLight).setOverlay(packedOverlay).setNormal(pose, 0, 1, 0);
        consumer.addVertex(matrix, 1, 1, 0).setColor(255, 255, 255, 255).setUv(u1, v0).setLight(packedLight).setOverlay(packedOverlay).setNormal(pose, 0, 1, 0);
        consumer.addVertex(matrix, 0, 1, 0).setColor(255, 255, 255, 255).setUv(u0, v0).setLight(packedLight).setOverlay(packedOverlay).setNormal(pose, 0, 1, 0);
    }

    private void renderProgressBar(PoseStack poseStack, float percentage, MultiBufferSource buffer) {
        poseStack.pushPose();
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.translate(-60, 30, 0);

        renderBar(buffer, poseStack, -58, 30, 235, 10, percentage, true, false);

        poseStack.popPose();
    }

    private void renderSettings(QuarryEntity blockEntity, PoseStack poseStack, MultiBufferSource buffer) {
        drawText(40, 70, Component.literal(TextUtil.truncateWithEllipsis(getTranslatable("gui.quarry.holo.security"), 60)).withColor(blockEntity.getLocked() ? YELLOW : GREEN), poseStack, buffer);
        drawText(40, 80, Component.literal(TextUtil.truncateWithEllipsis(getTranslatable("gui.quarry.holo.loop"), 60)).withColor(blockEntity.getLoop() ? GREEN : RED), poseStack, buffer);
        drawText(40, 90, Component.literal(TextUtil.truncateWithEllipsis(getTranslatable("gui.quarry.holo.filter"), 60)).withColor(blockEntity.getFilter() ? GREEN : RED), poseStack, buffer);
        drawRightboundText(160, 70, Component.literal(TextUtil.truncateWithEllipsis(getTranslatable("gui.quarry.holo.inout"), 60)).withColor(blockEntity.getEject() > 0 ? GREEN : RED), poseStack, buffer);
        drawRightboundText(160, 80, Component.literal(TextUtil.truncateWithEllipsis(getTranslatable("gui.quarry.holo.skip"), 60)).withColor(blockEntity.getSkip() ? GREEN : RED), poseStack, buffer);
        drawRightboundText(160, 90, Component.literal(TextUtil.truncateWithEllipsis(getTranslatable("gui.quarry.holo.replace"), 60)).withColor(blockEntity.getReplace() ? GREEN : RED), poseStack, buffer);

    }

    private String getTranslatable(String key) {
        return Component.translatable(key).getString();
    }

    @NotNull
    @Override
    public AABB getRenderBoundingBox(QuarryEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 2, pos.getY() + 3, pos.getZ() + 2);
    }

    private void renderBar(MultiBufferSource buffer, PoseStack poseStack, int x, int y, int width, int height, float value, boolean outline, boolean center) {
        VertexConsumer fill = buffer.getBuffer(RenderType.debugQuads());
        float filledWidth = width / 100f * value;
        Matrix4f matrix = poseStack.last().pose();

        if (center) {
            int halfW = width / 2;
            draw(matrix, fill, x + halfW, y, (int) (filledWidth / 2), height, value < 50 ? Color.RED.getRGB() : value > 75 ? Color.GREEN.getRGB() : Color.ORANGE.getRGB());
            draw(matrix, fill, (int) (x + halfW - filledWidth / 2), y, (int) (filledWidth / 2), height, value < 50 ? Color.RED.getRGB() : value > 75 ? Color.GREEN.getRGB() : Color.ORANGE.getRGB());
        } else {

            for (int i = 0; i < filledWidth; i++)
                draw(matrix, fill, x + i, y, 1, height, lerpColor3(Color.RED.getRGB(), Color.YELLOW.getRGB(), Color.GREEN.getRGB(), map(i, 0, width, 0, 100)));
        }

        if (outline) {
            int frameSpace = 2;
            draw(matrix, fill, x - frameSpace, y - frameSpace, width + (frameSpace * 2), 1, Color.WHITE.getRGB()); // Top
            draw(matrix, fill, x - frameSpace, y + height + frameSpace - 1, width + (frameSpace * 2), 1, Color.WHITE.getRGB()); // Bottom
            draw(matrix, fill, x - frameSpace, y - frameSpace, 1, height + (frameSpace * 2), Color.WHITE.getRGB()); // Left
            draw(matrix, fill, x + width + frameSpace - 1, y - frameSpace + 1, 1, height + (frameSpace * 2) - 1, Color.WHITE.getRGB()); // Right
        }

    }

    private void draw(Matrix4f matrix, VertexConsumer vertex, int x, int y, int w, int h, int color) {
        vertex.addVertex(matrix, x, y + h, 0.001f).setColor(color);
        vertex.addVertex(matrix, x + w, y + h, 0.001f).setColor(color);
        vertex.addVertex(matrix, x + w, y, 0.001f).setColor(color);
        vertex.addVertex(matrix, x, y, 0.001f).setColor(color);
    }

    private String getMode(int mode) {
        return switch (mode) {
            case 0 -> Component.translatable("gui.quarry.mode.default").getString();
            case 1 -> Component.translatable("gui.quarry.mode.efficient").getString();
            case 2 -> Component.translatable("gui.quarry.mode.fortune").getString();
            case 3 -> Component.translatable("gui.quarry.mode.silktouch").getString();
            case 4 -> Component.translatable("gui.quarry.mode.void").getString();
            default -> "unknown";
        };
    }

    private void drawText(int x, int y, Component text, PoseStack poseStack, MultiBufferSource buffer) {
        Font font = Minecraft.getInstance().font;
        font.drawInBatch(text, x - 100, y - 100, 0xFFFFFF, false, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);

    }

    private void drawRightboundText(int x, int y, Component text, PoseStack poseStack, MultiBufferSource buffer) {
        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(text);

        drawText(-textWidth + x, y, text, poseStack, buffer);
    }

    private void drawCenteredText(int x, int y, Component text, PoseStack poseStack, MultiBufferSource buffer) {
        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(text);

        drawText(-textWidth / 2 + x, y, text, poseStack, buffer);
    }

}
