package de.unhappycodings.quarry.common.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.client.config.ClientConfig;
import de.unhappycodings.quarry.common.item.AreaCard;
import de.unhappycodings.quarry.common.util.NbtUtil;
import net.minecraft.client.Minecraft;
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
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

public class AreaCardLevelRenderer {
    private static final int CELL_BORDER = ARGB.color(255, 0, 155, 155);
    private static final int YELLOW = ARGB.color(255, 255, 255, 0);
    private static final int CHUNK_BORDER = ARGB.colorFromFloat(0.5F, 1.0F, 0.0F, 0.0F);
    private static final int MAJOR_LINES = ARGB.colorFromFloat(1.0F, 0.25F, 0.25F, 1.0F);
    private static final float THIN_WIDTH = 1.0F;
    private static final float THICK_WIDTH = 4.0F;

    public static void renderSquareAboveWorldCentre(SubmitCustomGeometryEvent event) {
        if (!ClientConfig.enableAreaCardCornerRendering.get()) return;

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;

        ItemStack item = player.getItemBySlot(EquipmentSlot.MAINHAND);
        if (item.isEmpty() || !(item.getItem() instanceof AreaCard)) return;

        renderChunkLines(event);

        BlockPos pos1 = NbtUtil.getPos(item.get(Quarry.POS_1));
        if (pos1 == null || !item.has(Quarry.POS_1)) return;

        BlockPos pos2 = NbtUtil.getPos(item.get(Quarry.POS_2));
        if (pos2 != null && item.has(Quarry.POS_2)) {
            renderAreaOutline(event, pos1, pos2);
            return;
        }

        double reach = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue();
        HitResult hitResult = player.pick(reach * 2, 0.0F, false);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            renderAreaOutline(event, pos1, ((BlockHitResult) hitResult).getBlockPos());
        }
    }

    private static void renderAreaOutline(SubmitCustomGeometryEvent event, BlockPos pos1, BlockPos pos2) {
        Vec3 cameraPosition = event.getLevelRenderState().cameraRenderState.pos;
        PoseStack poseStack = event.getPoseStack();

        double minX = Math.min(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());

        double maxX = Math.max(pos1.getX(), pos2.getX()) + 1;
        double maxY = Math.max(pos1.getY(), pos2.getY()) + 1;
        double maxZ = Math.max(pos1.getZ(), pos2.getZ()) + 1;

        float rYellow = 1f, gYellow = 1f, bYellow = 0f, aYellow = 0.5f;
        float rWhite = 1f, gWhite = 1f, bWhite = 1f, aWhite = 0.75f;
        float epsilon = 0.001f;

        int xCount = (int) (maxX - minX);
        int yCount = (int) (maxY - minY);
        int zCount = (int) (maxZ - minZ);

        event.getSubmitNodeCollector().submitCustomGeometry(poseStack, RenderTypes.linesTranslucent(), (pose, vertexConsumer) -> {
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
        });
    }

    private static void renderChunkLines(SubmitCustomGeometryEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;

        Entity entity = minecraft.gameRenderer.mainCamera().entity();
        if (entity == null) return;

        Vec3 cameraPosition = event.getLevelRenderState().cameraRenderState.pos;
        PoseStack poseStack = event.getPoseStack();

        int minY = minecraft.level.getMinY();
        int maxY = minecraft.level.getMaxY() + 1;
        SectionPos cameraSection = SectionPos.of(entity.blockPosition());
        double xstart = cameraSection.minBlockX();
        double zstart = cameraSection.minBlockZ();
        Matrix4fc modelViewMatrix = event.getLevelRenderState().cameraRenderState.viewRotationMatrix;

        event.getSubmitNodeCollector().submitCustomGeometry(poseStack, RenderTypes.linesTranslucent(), (pose, vertexConsumer) -> {
            for (int x = -16; x <= 32; x += 16) {
                for (int z = -16; z <= 32; z += 16) {
                    drawVanillaDebugLine(vertexConsumer, pose, cameraPosition, modelViewMatrix, xstart + x, minY, zstart + z, xstart + x, maxY, zstart + z, CHUNK_BORDER, THICK_WIDTH);
                }
            }
        });
//      DISABLED FOR TESTING
//        for (int x = 2; x < 16; x += 2) {
//            int color = x % 4 == 0 ? CELL_BORDER : YELLOW;
//            drawVanillaDebugLine(vertexConsumer, pose, cameraPosition, modelViewMatrix, xstart + x, minY, zstart, xstart + x, maxY, zstart, color, THIN_WIDTH);
//            drawVanillaDebugLine(vertexConsumer, pose, cameraPosition, modelViewMatrix, xstart + x, minY, zstart + 16.0, xstart + x, maxY, zstart + 16.0, color, THIN_WIDTH);
//        }
//
//        for (int z = 2; z < 16; z += 2) {
//            int color = z % 4 == 0 ? CELL_BORDER : YELLOW;
//            drawVanillaDebugLine(vertexConsumer, pose, cameraPosition, modelViewMatrix, xstart, minY, zstart + z, xstart, maxY, zstart + z, color, THIN_WIDTH);
//            drawVanillaDebugLine(vertexConsumer, pose, cameraPosition, modelViewMatrix, xstart + 16.0, minY, zstart + z, xstart + 16.0, maxY, zstart + z, color, THIN_WIDTH);
//        }
//
//        for (int y = minecraft.level.getMinY(); y <= minecraft.level.getMaxY() + 1; y += 2) {
//            int color = y % 8 == 0 ? CELL_BORDER : YELLOW;
//            drawChunkRing(vertexConsumer, pose, cameraPosition, modelViewMatrix, xstart, y, zstart, color, THIN_WIDTH);
//        }
//
//        for (int x = 0; x <= 16; x += 16) {
//            for (int z = 0; z <= 16; z += 16) {
//                drawVanillaDebugLine(vertexConsumer, pose, cameraPosition, modelViewMatrix, xstart + x, minY, zstart + z, xstart + x, maxY, zstart + z, MAJOR_LINES, THICK_WIDTH);
//            }
//        }
//
//        drawSectionCuboid(vertexConsumer, pose, cameraPosition, modelViewMatrix, cameraSection);
//
//        for (int y = minecraft.level.getMinY(); y <= minecraft.level.getMaxY() + 1; y += 16) {
//            drawChunkRing(vertexConsumer, pose, cameraPosition, modelViewMatrix, xstart, y, zstart, MAJOR_LINES, THICK_WIDTH);
//        }
    }

    private static void drawChunkRing(VertexConsumer buffer, PoseStack.Pose pose, Vec3 camera, Matrix4fc modelViewMatrix, double xstart, double y, double zstart, int color, float width) {
        drawVanillaDebugLine(buffer, pose, camera, modelViewMatrix, xstart, y, zstart, xstart, y, zstart + 16.0, color, width);
        drawVanillaDebugLine(buffer, pose, camera, modelViewMatrix, xstart, y, zstart + 16.0, xstart + 16.0, y, zstart + 16.0, color, width);
        drawVanillaDebugLine(buffer, pose, camera, modelViewMatrix, xstart + 16.0, y, zstart + 16.0, xstart + 16.0, y, zstart, color, width);
        drawVanillaDebugLine(buffer, pose, camera, modelViewMatrix, xstart + 16.0, y, zstart, xstart, y, zstart, color, width);
    }

    private static void drawSectionCuboid(VertexConsumer buffer, PoseStack.Pose pose, Vec3 camera, Matrix4fc modelViewMatrix, SectionPos section) {
        double minX = section.minBlockX();
        double minY = section.minBlockY();
        double minZ = section.minBlockZ();
        double maxX = section.maxBlockX() + 1;
        double maxY = section.maxBlockY() + 1;
        double maxZ = section.maxBlockZ() + 1;

        drawVanillaDebugLine(buffer, pose, camera, modelViewMatrix, minX, minY, minZ, maxX, minY, minZ, MAJOR_LINES, THIN_WIDTH);
        drawVanillaDebugLine(buffer, pose, camera, modelViewMatrix, maxX, minY, minZ, maxX, minY, maxZ, MAJOR_LINES, THIN_WIDTH);
        drawVanillaDebugLine(buffer, pose, camera, modelViewMatrix, maxX, minY, maxZ, minX, minY, maxZ, MAJOR_LINES, THIN_WIDTH);
        drawVanillaDebugLine(buffer, pose, camera, modelViewMatrix, minX, minY, maxZ, minX, minY, minZ, MAJOR_LINES, THIN_WIDTH);

        drawVanillaDebugLine(buffer, pose, camera, modelViewMatrix, minX, maxY, minZ, maxX, maxY, minZ, MAJOR_LINES, THIN_WIDTH);
        drawVanillaDebugLine(buffer, pose, camera, modelViewMatrix, maxX, maxY, minZ, maxX, maxY, maxZ, MAJOR_LINES, THIN_WIDTH);
        drawVanillaDebugLine(buffer, pose, camera, modelViewMatrix, maxX, maxY, maxZ, minX, maxY, maxZ, MAJOR_LINES, THIN_WIDTH);
        drawVanillaDebugLine(buffer, pose, camera, modelViewMatrix, minX, maxY, maxZ, minX, maxY, minZ, MAJOR_LINES, THIN_WIDTH);

        drawVanillaDebugLine(buffer, pose, camera, modelViewMatrix, minX, minY, minZ, minX, maxY, minZ, MAJOR_LINES, THIN_WIDTH);
        drawVanillaDebugLine(buffer, pose, camera, modelViewMatrix, maxX, minY, minZ, maxX, maxY, minZ, MAJOR_LINES, THIN_WIDTH);
        drawVanillaDebugLine(buffer, pose, camera, modelViewMatrix, maxX, minY, maxZ, maxX, maxY, maxZ, MAJOR_LINES, THIN_WIDTH);
        drawVanillaDebugLine(buffer, pose, camera, modelViewMatrix, minX, minY, maxZ, minX, maxY, maxZ, MAJOR_LINES, THIN_WIDTH);
    }

    private static void drawVanillaDebugLine(VertexConsumer buffer, PoseStack.Pose pose, Vec3 camera, Matrix4fc modelViewMatrix, double x1, double y1, double z1, double x2, double y2, double z2, int color, float width) {
        Vector4f start = new Vector4f((float) (x1 - camera.x), (float) (y1 - camera.y), (float) (z1 - camera.z), 1.0F);
        Vector4f end = new Vector4f((float) (x2 - camera.x), (float) (y2 - camera.y), (float) (z2 - camera.z), 1.0F);
        Vector4f startViewSpace = new Vector4f();
        Vector4f endViewSpace = new Vector4f();
        Vector4f intersectionInWorld = new Vector4f();

        start.mul(modelViewMatrix, startViewSpace);
        end.mul(modelViewMatrix, endViewSpace);

        boolean startIsBehindCamera = startViewSpace.z > -0.05F;
        boolean endIsBehindCamera = endViewSpace.z > -0.05F;
        if (startIsBehindCamera && endIsBehindCamera) return;

        if (startIsBehindCamera || endIsBehindCamera) {
            float denom = endViewSpace.z - startViewSpace.z;
            if (Math.abs(denom) < 1.0E-9F) return;

            float intersection = Math.clamp((-0.05F - startViewSpace.z) / denom, 0.0F, 1.0F);
            start.lerp(end, intersection, intersectionInWorld);
            if (startIsBehindCamera) {
                start.set(intersectionInWorld);
            } else {
                end.set(intersectionInWorld);
            }
        }

        buffer.addVertex(pose, start.x, start.y, start.z)
                .setNormal(pose, end.x - start.x, end.y - start.y, end.z - start.z)
                .setColor(color)
                .setLineWidth(width);
        buffer.addVertex(pose, end.x, end.y, end.z)
                .setNormal(pose, end.x - start.x, end.y - start.y, end.z - start.z)
                .setColor(color)
                .setLineWidth(width);
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
