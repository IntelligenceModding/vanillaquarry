package de.unhappycodings.quarry.common.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.client.config.ClientConfig;
import de.unhappycodings.quarry.common.item.AreaCard;
import de.unhappycodings.quarry.common.util.NbtUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

@EventBusSubscriber(value = Dist.CLIENT, modid = Quarry.MOD_ID)
public class AreaCardLevelRenderer {

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void renderSquareAboveWorldCentre(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) return;

        if (!ClientConfig.enableAreaCardCornerRendering.get()) return;
        Player player = Minecraft.getInstance().player;
        ItemStack item = player.getItemBySlot(EquipmentSlot.MAINHAND);
        if (item.isEmpty()) return;
        if (item.getItem() instanceof AreaCard) {

            renderChunkLines(event);

            BlockPos pos1 = NbtUtil.getPos(item.get(Quarry.POS_1));
            BlockPos pos2 = NbtUtil.getPos(item.get(Quarry.POS_2));
            if (pos1 != null && item.has(Quarry.POS_1)) {

                if (pos2 != null && item.has(Quarry.POS_2)) {
                    renderAreaOutline(event, pos1, pos2);
                } else {
                    double reach = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue();
                    HitResult hitResult = player.pick(reach * 2, 0.0F, false);

                    if (hitResult.getType() == HitResult.Type.BLOCK)
                        renderAreaOutline(event, pos1, ((BlockHitResult) hitResult).getBlockPos());

                }
            }

        }
    }

    private static void renderAreaOutline(RenderLevelStageEvent event, BlockPos pos1, BlockPos pos2) {
        Minecraft minecraft = Minecraft.getInstance();
        MultiBufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        Vec3 vec3 = Minecraft.getInstance().getEntityRenderDispatcher().camera.getPosition();
        PoseStack poseStack = event.getPoseStack();
        Matrix4f matrix4f = poseStack.last().pose();

        poseStack.translate(-vec3.x, -vec3.y, -vec3.z);

        double minX = Math.min(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());

        double maxX = Math.max(pos1.getX(), pos2.getX()) + 1;
        double maxY = Math.max(pos1.getY(), pos2.getY()) + 1;
        double maxZ = Math.max(pos1.getZ(), pos2.getZ()) + 1;

        float rYellow = 1f, gYellow = 1f, bYellow = 0f, aYellow = 0.5f;
        float rWhite = 1f, gWhite = 1f, bWhite = 1f, aWhite = 0.75f;
        float epsilon = 0.001f;

        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.debugLineStrip(1.0));

        int xCount = (int) (maxX - minX);
        int yCount = (int) (maxY - minY);
        int zCount = (int) (maxZ - minZ);

        for (double y : new double[]{minY, maxY}) {
            for (int i = 1; i < zCount; i++) {
                double z = minZ + i;
                boolean useBlue = (i % 4 == 0) && (i + 4 <= zCount);
                float r = useBlue ? 0f : rYellow;
                float g = useBlue ? 1f : gYellow;
                float b = useBlue ? 1f : bYellow;
                drawLine(vertexconsumer, matrix4f, minX + epsilon, y, z, maxX - epsilon, y, z, r, g, b, 0.5f, aYellow);

            }

            for (int i = 1; i < xCount; i++) {
                double x = minX + i;
                boolean useBlue = (i % 4 == 0) && (i + 4 <= xCount);
                float r = useBlue ? 0f : rYellow;
                float g = useBlue ? 1f : gYellow;
                float b = useBlue ? 1f : bYellow;
                drawLine(vertexconsumer, matrix4f, x, y, minZ + epsilon, x, y, maxZ - epsilon, r, g, b, 0.5f, aYellow);
            }
        }

        for (double x : new double[]{minX, maxX}) {
            for (int i = 1; i < zCount; i++) {
                double z = minZ + i;
                boolean useBlue = (i % 4 == 0) && (i + 4 <= zCount);
                float r = useBlue ? 0f : rYellow;
                float g = useBlue ? 1f : gYellow;
                float b = useBlue ? 1f : bYellow;
                drawLine(vertexconsumer, matrix4f, x, minY + epsilon, z, x, maxY - epsilon, z, r, g, b, 0.5f, aYellow);
            }
            for (int i = 1; i < yCount; i++) {
                double y = minY + i;
                boolean useBlue = (i % 4 == 0) && (i + 4 <= yCount);
                float r = useBlue ? 0f : rYellow;
                float g = useBlue ? 1f : gYellow;
                float b = useBlue ? 1f : bYellow;
                drawLine(vertexconsumer, matrix4f, x, y, minZ + epsilon, x, y, maxZ - epsilon, r, g, b, 0.5f, aYellow);
            }
        }

        for (double z : new double[]{minZ, maxZ}) {
            for (int i = 1; i < xCount; i++) {
                double x = minX + i;
                boolean useBlue = (i % 4 == 0) && (i + 4 <= xCount);
                float r = useBlue ? 0f : rYellow;
                float g = useBlue ? 1f : gYellow;
                float b = useBlue ? 1f : bYellow;
                drawLine(vertexconsumer, matrix4f, x, minY + epsilon, z, x, maxY - epsilon, z, r, g, b, 0.5f, aYellow);
            }
            for (int i = 1; i < yCount; i++) {
                double y = minY + i;
                boolean useBlue = (i % 4 == 0) && (i + 4 <= yCount);
                float r = useBlue ? 0f : rYellow;
                float g = useBlue ? 1f : gYellow;
                float b = useBlue ? 1f : bYellow;
                drawLine(vertexconsumer, matrix4f, minX + epsilon, y, z, maxX - epsilon, y, z, r, g, b, 0.5f, aYellow);
            }
        }

        for (double x : new double[]{minX, maxX}) {
            for (double z : new double[]{minZ, maxZ}) {
                drawLine(vertexconsumer, matrix4f, x, minY, z, x, maxY, z, rWhite, gWhite, bWhite, 2.0f, aWhite);
            }
        }

        for (double y : new double[]{minY, maxY}) {
            drawLine(vertexconsumer, matrix4f, minX, y, minZ, maxX, y, minZ, rWhite, gWhite, bWhite, 2.0f, aWhite);
            drawLine(vertexconsumer, matrix4f, minX, y, maxZ, maxX, y, maxZ, rWhite, gWhite, bWhite, 2.0f, aWhite);
            drawLine(vertexconsumer, matrix4f, minX, y, minZ, minX, y, maxZ, rWhite, gWhite, bWhite, 2.0f, aWhite);
            drawLine(vertexconsumer, matrix4f, maxX, y, minZ, maxX, y, maxZ, rWhite, gWhite, bWhite, 2.0f, aWhite);
        }
        vertexconsumer = bufferSource.getBuffer(RenderType.debugLineStrip((double) 2.0F));
    }

    private static void renderChunkLines(RenderLevelStageEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        MultiBufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        Vec3 vec3 = minecraft.getEntityRenderDispatcher().camera.getPosition();
        PoseStack poseStack = new PoseStack();

        Entity entity = minecraft.gameRenderer.getMainCamera().getEntity();
        float f = (float) ((double) minecraft.level.getMinBuildHeight() - vec3.y);
        float f1 = (float) ((double) minecraft.level.getMaxBuildHeight() - vec3.y);
        ChunkPos chunkpos = entity.chunkPosition();
        float f2 = (float) ((double) chunkpos.getMinBlockX() - vec3.x);
        float f3 = (float) ((double) chunkpos.getMinBlockZ() - vec3.z);
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.debugLineStrip(1.0));
        Matrix4f matrix4f = poseStack.last().pose();

        for (int i = -16; i <= 32; i += 16) {
            for (int j = -16; j <= 32; j += 16) {
                vertexconsumer.addVertex(matrix4f, f2 + (float) i, f, f3 + (float) j).setColor(1.0F, 0.0F, 0.0F, 0.0F);
                vertexconsumer.addVertex(matrix4f, f2 + (float) i, f, f3 + (float) j).setColor(1.0F, 0.0F, 0.0F, 0.5F);
                vertexconsumer.addVertex(matrix4f, f2 + (float) i, f1, f3 + (float) j).setColor(1.0F, 0.0F, 0.0F, 0.5F);
                vertexconsumer.addVertex(matrix4f, f2 + (float) i, f1, f3 + (float) j).setColor(1.0F, 0.0F, 0.0F, 0.0F);
            }
        }

        vertexconsumer = bufferSource.getBuffer(RenderType.debugLineStrip((double) 2.0F));

    }

    private static void drawLine(VertexConsumer buffer, Matrix4f matrix, double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b, float width, float a) {
        buffer.addVertex(matrix, (float) x1, (float) y1, (float) z1).setColor(r, g, b, 0);
        buffer.addVertex(matrix, (float) x1, (float) y1, (float) z1).setColor(r, g, b, a);
        buffer.addVertex(matrix, (float) x2, (float) y2, (float) z2).setColor(r, g, b, a);
        buffer.addVertex(matrix, (float) x2, (float) y2, (float) z2).setColor(r, g, b, 0);
    }

}
