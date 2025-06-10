package de.unhappycodings.quarry.client.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.client.config.ClientConfig;
import de.unhappycodings.quarry.common.config.CommonConfig;
import de.unhappycodings.quarry.common.item.AreaCardItem;
import de.unhappycodings.quarry.common.util.NbtUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.util.BiConsumer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.awt.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Quarry.MOD_ID)
public class AreaCardLevelRenderer {

    // Normal vectors for each face
    private static final Vec3 NORMAL_UP = new Vec3(0.0f, 1.0f, 0.0f);
    private static final Vec3 NORMAL_DOWN = new Vec3(0.0f, -1.0f, 0.0f);
    private static final Vec3 NORMAL_NORTH = new Vec3(0.0f, 0.0f, -1.0f);
    private static final Vec3 NORMAL_SOUTH = new Vec3(0.0f, 0.0f, 1.0f);
    private static final Vec3 NORMAL_EAST = new Vec3(-1.0f, 0.0f, 0.0f);
    private static final Vec3 NORMAL_WEST = new Vec3(1.0f, 0.0f, 0.0f);

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void renderSquareAboveWorldCentre(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        if (!ClientConfig.enableAreaCardCornerRendering.get()) return;
        Player player = Minecraft.getInstance().player;
        ItemStack item = player.getItemBySlot(EquipmentSlot.MAINHAND);
        if (item.isEmpty()) return;
        if (item.getItem() instanceof AreaCardItem) {
            CompoundTag nbt = item.getOrCreateTag();
            BlockPos pos1 = NbtUtil.getPos(nbt.getCompound("pos1"));
            BlockPos pos2 = NbtUtil.getPos(nbt.getCompound("pos2"));
            if (pos1 != null)
                renderCube(event, pos1, Color.decode(CommonConfig.areaCardOverlayColorFirstCorner.get()));
            if (pos2 != null)
                renderCube(event, pos2, Color.decode(CommonConfig.areaCardOverlayColorSecondCorner.get()));
        }
    }

    public static void renderCube(@NotNull RenderLevelStageEvent event, @NotNull BlockPos pos, @NotNull Color color) {
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        Vec3 view = Minecraft.getInstance().getEntityRenderDispatcher().camera.getPosition();

        float x = pos.getX();
        float y = pos.getY();
        float z = pos.getZ();

        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = 0.3f;

        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferbuilder.begin(RenderType.translucent().mode(), DefaultVertexFormat.POSITION_COLOR_NORMAL);

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(-view.x, -view.y, -view.z);

        Matrix4f matrix4f = poseStack.last().pose();

        // Helper method to add a vertex with color and normal
        BiConsumer<Vec3, Vec3> addVertex = (vertex, normal) ->
                bufferbuilder.vertex(matrix4f, (float) (x + vertex.x), (float) (y + vertex.y), (float) (z + vertex.z))
                        .color(r, g, b, a)
                        .normal((float) normal.x, (float) normal.y, (float) normal.z)
                        .endVertex();

        // Down face
        addVertex.accept(new Vec3(0, 0, 0), NORMAL_DOWN);
        addVertex.accept(new Vec3(1, 0, 0), NORMAL_DOWN);
        addVertex.accept(new Vec3(1, 0, 1), NORMAL_DOWN);
        addVertex.accept(new Vec3(0, 0, 1), NORMAL_DOWN);

        // Up face
        addVertex.accept(new Vec3(0, 1, 0), NORMAL_UP);
        addVertex.accept(new Vec3(0, 1, 1), NORMAL_UP);
        addVertex.accept(new Vec3(1, 1, 1), NORMAL_UP);
        addVertex.accept(new Vec3(1, 1, 0), NORMAL_UP);

        // North face
        addVertex.accept(new Vec3(0, 1, 0), NORMAL_NORTH);
        addVertex.accept(new Vec3(1, 1, 0), NORMAL_NORTH);
        addVertex.accept(new Vec3(1, 0, 0), NORMAL_NORTH);
        addVertex.accept(new Vec3(0, 0, 0), NORMAL_NORTH);

        // South face
        addVertex.accept(new Vec3(0, 1, 1), NORMAL_SOUTH);
        addVertex.accept(new Vec3(0, 0, 1), NORMAL_SOUTH);
        addVertex.accept(new Vec3(1, 0, 1), NORMAL_SOUTH);
        addVertex.accept(new Vec3(1, 1, 1), NORMAL_SOUTH);

        // East face
        addVertex.accept(new Vec3(0, 1, 0), NORMAL_EAST);
        addVertex.accept(new Vec3(0, 0, 0), NORMAL_EAST);
        addVertex.accept(new Vec3(0, 0, 1), NORMAL_EAST);
        addVertex.accept(new Vec3(0, 1, 1), NORMAL_EAST);

        // West face
        addVertex.accept(new Vec3(1, 1, 0), NORMAL_WEST);
        addVertex.accept(new Vec3(1, 1, 1), NORMAL_WEST);
        addVertex.accept(new Vec3(1, 0, 1), NORMAL_WEST);
        addVertex.accept(new Vec3(1, 0, 0), NORMAL_WEST);

        BufferUploader.drawWithShader(bufferbuilder.end());
        poseStack.popPose();

        RenderSystem.enableDepthTest();
    }
}
