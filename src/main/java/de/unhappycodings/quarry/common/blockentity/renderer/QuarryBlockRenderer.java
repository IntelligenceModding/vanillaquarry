package de.unhappycodings.quarry.common.blockentity.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import de.unhappycodings.quarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.quarry.common.config.CommonConfig;
import de.unhappycodings.quarry.common.item.ModItems;
import de.unhappycodings.quarry.common.util.NbtUtil;
import de.unhappycodings.quarry.common.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.debug.ChunkBorderRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.logging.Level;

public class QuarryBlockRenderer implements BlockEntityRenderer<QuarryBlockEntity> {
    public final BlockEntityRendererProvider.Context context;

    public QuarryBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(@NotNull QuarryBlockEntity entity, float tick, @NotNull PoseStack stack, @NotNull MultiBufferSource source, int pPackedLight, int pPackedOverlay) {
        ItemStack itemStack = entity.getItem(12);
        if (itemStack.is(ModItems.AREA_CARD.get())) {
            CompoundTag tag = entity.getItem(12).getOrCreateTag();
            CompoundTag pos1 = (CompoundTag) tag.get("pos1");
            CompoundTag pos2 = (CompoundTag) tag.get("pos2");
            if (pos1 == null || pos2 == null) return;

            BlockPos posToRenderSquareAt = NbtUtil.getPos(pos1);
            Minecraft minecraft = Minecraft.getInstance();
            Vec3 cameraPos = minecraft.gameRenderer.getMainCamera().getPosition();

            Matrix4f matrix = stack.last().pose();
            VertexConsumer buffer = source.getBuffer(RenderType.translucent());

            // Translate the positions back to the point of the block.
            float x = (float) (-cameraPos.x + posToRenderSquareAt.getX());
            float y = (float) (-cameraPos.y + posToRenderSquareAt.getY());
            float z = (float) (-cameraPos.z + posToRenderSquareAt.getZ());

            int color = CommonConfig.areaCardOverlayColorFirstCorner.get();
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;
            float a = 0.5f;
            stack.pushPose();

            // Down
            buffer.vertex(matrix, x + 0, y + 0, z + 0).color(r, g, b, a).endVertex();
            buffer.vertex(matrix, x + 1, y + 0, z + 0).color(r, g, b, a).endVertex();
            buffer.vertex(matrix, x + 1, y + 0, z + 1).color(r, g, b, a).endVertex();
            buffer.vertex(matrix, x + 0, y + 0, z + 1).color(r, g, b, a).endVertex();

            buffer.vertex(matrix, x + 0, y + 0, z + 0).color(color).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix, x + 1, y + 0, z + 0).color(color).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix, x + 1, y + 0, z + 1).color(color).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix, x + 0, y + 0, z + 1).color(color).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();

            stack.popPose();

            System.out.println("render");

        }
    }

}
