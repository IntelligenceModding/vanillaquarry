package de.unhappycodings.quarry.common.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.client.config.ClientConfig;
import de.unhappycodings.quarry.common.item.AreaCard;
import de.unhappycodings.quarry.common.util.NbtUtil;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class AreaCardLevelRenderer {
    private static final int CHUNK_BORDER = ARGB.colorFromFloat(0.8F, 1.0F, 0.0F, 0.0F);
    private static final float CHUNK_MARKER_WIDTH = 1.0F;

    public static void renderSquareAboveWorldCentre(LevelRenderContext context) {
        if (!ClientConfig.enableAreaCardCornerRendering.get()) return;

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;

        ItemStack item = player.getItemBySlot(EquipmentSlot.MAINHAND);
        if (item.isEmpty() || !(item.getItem() instanceof AreaCard)) return;

        renderChunkLines(context);

        BlockPos pos1 = NbtUtil.getPos(item.get(Quarry.POS_1));
        if (pos1 == null || !item.has(Quarry.POS_1)) return;

        BlockPos pos2 = NbtUtil.getPos(item.get(Quarry.POS_2));
        if (pos2 != null && item.has(Quarry.POS_2)) {
            renderAreaOutline(context, pos1, pos2);
            return;
        }

        double reach = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue();
        HitResult hitResult = player.pick(reach * 2, 0.0F, false);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            renderAreaOutline(context, pos1, ((BlockHitResult) hitResult).getBlockPos());
        }
    }

    private static void renderAreaOutline(LevelRenderContext context, BlockPos pos1, BlockPos pos2) {
        Minecraft minecraft = Minecraft.getInstance();
        MultiBufferSource bufferSource = context.bufferSource();
        Vec3 cameraPosition = minecraft.gameRenderer.getMainCamera().position();
        PoseStack poseStack = context.poseStack();

        poseStack.pushPose();
        PoseStack.Pose pose = poseStack.last();

        double minX = Math.min(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());

        double maxX = Math.max(pos1.getX(), pos2.getX()) + 1;
        double maxY = Math.max(pos1.getY(), pos2.getY()) + 1;
        double maxZ = Math.max(pos1.getZ(), pos2.getZ()) + 1;

        float rYellow = 1f, gYellow = 1f, bYellow = 0f, aYellow = 0.5f;
        float rWhite = 1f, gWhite = 1f, bWhite = 1f, aWhite = 0.75f;
        float epsilon = 0.001f;

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderTypes.linesTranslucent());

        int xCount = (int) (maxX - minX);
        int yCount = (int) (maxY - minY);
        int zCount = (int) (maxZ - minZ);

        for (double y : new double[]{minY, maxY}) {
            for (int i = 1; i < zCount; i++) {
                double z = minZ + i;
                boolean useBlue = (i % 4 == 0) && (i + 4 <= zCount);
                drawLine(vertexConsumer, pose, cameraPosition, minX + epsilon, y, z, maxX - epsilon, y, z, useBlue ? 0f : rYellow, useBlue ? 1f : gYellow, useBlue ? 1f : bYellow, 0.5f, aYellow);
            }

            for (int i = 1; i < xCount; i++) {
                double x = minX + i;
                boolean useBlue = (i % 4 == 0) && (i + 4 <= xCount);
                drawLine(vertexConsumer, pose, cameraPosition, x, y, minZ + epsilon, x, y, maxZ - epsilon, useBlue ? 0f : rYellow, useBlue ? 1f : gYellow, useBlue ? 1f : bYellow, 0.5f, aYellow);
            }
        }

        for (double x : new double[]{minX, maxX}) {
            for (int i = 1; i < zCount; i++) {
                double z = minZ + i;
                boolean useBlue = (i % 4 == 0) && (i + 4 <= zCount);
                drawLine(vertexConsumer, pose, cameraPosition, x, minY + epsilon, z, x, maxY - epsilon, z, useBlue ? 0f : rYellow, useBlue ? 1f : gYellow, useBlue ? 1f : bYellow, 0.5f, aYellow);
            }
            for (int i = 1; i < yCount; i++) {
                double y = minY + i;
                boolean useBlue = (i % 4 == 0) && (i + 4 <= yCount);
                drawLine(vertexConsumer, pose, cameraPosition, x, y, minZ + epsilon, x, y, maxZ - epsilon, useBlue ? 0f : rYellow, useBlue ? 1f : gYellow, useBlue ? 1f : bYellow, 0.5f, aYellow);
            }
        }

        for (double z : new double[]{minZ, maxZ}) {
            for (int i = 1; i < xCount; i++) {
                double x = minX + i;
                boolean useBlue = (i % 4 == 0) && (i + 4 <= xCount);
                drawLine(vertexConsumer, pose, cameraPosition, x, minY + epsilon, z, x, maxY - epsilon, z, useBlue ? 0f : rYellow, useBlue ? 1f : gYellow, useBlue ? 1f : bYellow, 0.5f, aYellow);
            }
            for (int i = 1; i < yCount; i++) {
                double y = minY + i;
                boolean useBlue = (i % 4 == 0) && (i + 4 <= yCount);
                drawLine(vertexConsumer, pose, cameraPosition, minX + epsilon, y, z, maxX - epsilon, y, z, useBlue ? 0f : rYellow, useBlue ? 1f : gYellow, useBlue ? 1f : bYellow, 0.5f, aYellow);
            }
        }

        for (double x : new double[]{minX, maxX}) {
            for (double z : new double[]{minZ, maxZ}) {
                drawLine(vertexConsumer, pose, cameraPosition, x, minY, z, x, maxY, z, rWhite, gWhite, bWhite, 2.0f, aWhite);
            }
        }

        for (double y : new double[]{minY, maxY}) {
            drawLine(vertexConsumer, pose, cameraPosition, minX, y, minZ, maxX, y, minZ, rWhite, gWhite, bWhite, 2.0f, aWhite);
            drawLine(vertexConsumer, pose, cameraPosition, minX, y, maxZ, maxX, y, maxZ, rWhite, gWhite, bWhite, 2.0f, aWhite);
            drawLine(vertexConsumer, pose, cameraPosition, minX, y, minZ, minX, y, maxZ, rWhite, gWhite, bWhite, 2.0f, aWhite);
            drawLine(vertexConsumer, pose, cameraPosition, maxX, y, minZ, maxX, y, maxZ, rWhite, gWhite, bWhite, 2.0f, aWhite);
        }

        poseStack.popPose();
    }

    private static void renderChunkLines(LevelRenderContext context) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;

        Entity entity = minecraft.gameRenderer.getMainCamera().entity();
        if (entity == null) return;

        MultiBufferSource bufferSource = context.bufferSource();
        Vec3 cameraPosition = minecraft.gameRenderer.getMainCamera().position();
        PoseStack poseStack = context.poseStack();

        poseStack.pushPose();
        PoseStack.Pose pose = poseStack.last();

        int minY = minecraft.level.getMinY();
        int maxY = minecraft.level.getMaxY() + 1;
        SectionPos cameraSection = SectionPos.of(entity.blockPosition());
        double xstart = cameraSection.minBlockX();
        double zstart = cameraSection.minBlockZ();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderTypes.linesTranslucent());

        for (int x = -16; x <= 32; x += 16) {
            for (int z = -16; z <= 32; z += 16) {
                drawArgbLine(vertexConsumer, pose, cameraPosition, xstart + x, minY, zstart + z, xstart + x, maxY, zstart + z, CHUNK_BORDER, CHUNK_MARKER_WIDTH);
            }
        }

        poseStack.popPose();
    }

    private static void drawArgbLine(VertexConsumer buffer, PoseStack.Pose pose, Vec3 camera, double x1, double y1, double z1, double x2, double y2, double z2, int color, float width) {
        float a = ((color >> 24) & 0xFF) / 255.0F;
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        drawLine(buffer, pose, camera, x1, y1, z1, x2, y2, z2, r, g, b, width, a);
    }

    private static void drawLine(VertexConsumer buffer, PoseStack.Pose pose, Vec3 camera, double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b, float width, float a) {
        float startX = (float) (x1 - camera.x);
        float startY = (float) (y1 - camera.y);
        float startZ = (float) (z1 - camera.z);
        float endX = (float) (x2 - camera.x);
        float endY = (float) (y2 - camera.y);
        float endZ = (float) (z2 - camera.z);
        addLineVertex(buffer, pose, startX, startY, startZ, endX - startX, endY - startY, endZ - startZ, r, g, b, a, width);
        addLineVertex(buffer, pose, endX, endY, endZ, endX - startX, endY - startY, endZ - startZ, r, g, b, a, width);
    }

    private static void addLineVertex(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, float z, float normalX, float normalY, float normalZ, float r, float g, float b, float a, float width) {
        float length = (float) Math.sqrt(normalX * normalX + normalY * normalY + normalZ * normalZ);
        if (length == 0.0F) return;

        normalX /= length;
        normalY /= length;
        normalZ /= length;
        buffer.addVertex(pose, x, y, z).setColor(r, g, b, a).setNormal(pose, normalX, normalY, normalZ).setLineWidth(width);
    }
}
