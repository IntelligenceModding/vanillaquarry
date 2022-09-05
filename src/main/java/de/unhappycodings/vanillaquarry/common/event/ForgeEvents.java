package de.unhappycodings.vanillaquarry.common.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import de.unhappycodings.vanillaquarry.VanillaQuarry;
import de.unhappycodings.vanillaquarry.client.config.ClientConfig;
import de.unhappycodings.vanillaquarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.vanillaquarry.common.config.CommonConfig;
import de.unhappycodings.vanillaquarry.common.item.AreaCardItem;
import de.unhappycodings.vanillaquarry.common.util.NbtUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@Mod.EventBusSubscriber(modid = VanillaQuarry.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {

    @SuppressWarnings({"ConstantConditions", "removal"})
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void renderSquareAboveWorldCentre(RenderLevelLastEvent event) {
        if (!ClientConfig.enableAreaCardCornerRendering.get()) return;
        Player player = Minecraft.getInstance().player;
        ItemStack item = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (item.isEmpty()) return;
        if (item.getItem() instanceof AreaCardItem) {
            CompoundTag nbt = item.getOrCreateTag();
            if (nbt.contains("pos1")) {
                CompoundTag positionTag = (CompoundTag) nbt.get("pos1");
                BlockPos posToRenderSquareAt = NbtUtil.getPos(positionTag);
                Minecraft minecraft = Minecraft.getInstance();
                Vec3 cameraPos = minecraft.gameRenderer.getMainCamera().getPosition();

                RenderSystem.disableDepthTest();
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

                Matrix4f matrix = event.getPoseStack().last().pose();
                RenderSystem.setTextureMatrix(matrix);
                Tesselator tes = Tesselator.getInstance();
                BufferBuilder buffer = tes.getBuilder();
                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

                // Translate the positions back to the point of the block.
                float x = (float) (-cameraPos.x + posToRenderSquareAt.getX());
                float y = (float) (-cameraPos.y + posToRenderSquareAt.getY());
                float z = (float) (-cameraPos.z + posToRenderSquareAt.getZ());

                Color color = Color.decode(CommonConfig.areaCardOverlayColorFirstCorner.get());
                float r = color.getRed() / 255f;
                float g = color.getGreen() / 255f;
                float b = color.getBlue() / 255f;
                float a = 0.5f;

                // Down
                buffer.vertex(matrix, x + 0, y + 0, z + 0).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 1, y + 0, z + 0).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 1, y + 0, z + 1).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 0, y + 0, z + 1).color(r, g, b, a).endVertex();
                // Up
                buffer.vertex(matrix, x + 0, y + 1, z + 0).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 0, y + 1, z + 1).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 1, y + 1, z + 1).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 1, y + 1, z + 0).color(r, g, b, a).endVertex();
                // North
                buffer.vertex(matrix, x + 0, y + 1, z + 0).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 1, y + 1, z + 0).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 1, y + 0, z + 0).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 0, y + 0, z + 0).color(r, g, b, a).endVertex();
                // South
                buffer.vertex(matrix, x + 0, y + 1, z + 1).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 0, y + 0, z + 1).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 1, y + 0, z + 1).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 1, y + 1, z + 1).color(r, g, b, a).endVertex();
                // East
                buffer.vertex(matrix, x + 0, y + 1, z + 0).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 0, y + 0, z + 0).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 0, y + 0, z + 1).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 0, y + 1, z + 1).color(r, g, b, a).endVertex();
                // West
                buffer.vertex(matrix, x + 1, y + 1, z + 0).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 1, y + 1, z + 1).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 1, y + 0, z + 1).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 1, y + 0, z + 0).color(r, g, b, a).endVertex();

                tes.end();
                RenderSystem.enableDepthTest();
                RenderSystem.depthFunc(0x207);
            }
            if (nbt.contains("pos2")) {
                CompoundTag positionTag = (CompoundTag) nbt.get("pos2");
                BlockPos posToRenderSquareAt = NbtUtil.getPos(positionTag);
                Minecraft minecraft = Minecraft.getInstance();
                Vec3 cameraPos = minecraft.gameRenderer.getMainCamera().getPosition();

                RenderSystem.disableDepthTest();
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

                Matrix4f matrix = event.getPoseStack().last().pose();
                RenderSystem.setTextureMatrix(matrix);
                Tesselator tes = Tesselator.getInstance();
                BufferBuilder buffer = tes.getBuilder();
                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

                // Translate the positions back to the point of the block.
                float x = (float) (-cameraPos.x + posToRenderSquareAt.getX());
                float y = (float) (-cameraPos.y + posToRenderSquareAt.getY());
                float z = (float) (-cameraPos.z + posToRenderSquareAt.getZ());

                Color color = Color.decode(CommonConfig.areaCardOverlayColorSecondCorner.get());
                float r = color.getRed() / 255f;
                float g = color.getGreen() / 255f;
                float b = color.getBlue() / 255f;
                float a = 0.5f;

                // Down
                buffer.vertex(matrix, x + 0, y + 0, z + 0).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 1, y + 0, z + 0).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 1, y + 0, z + 1).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 0, y + 0, z + 1).color(r, g, b, a).endVertex();
                // Up
                buffer.vertex(matrix, x + 0, y + 1, z + 0).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 0, y + 1, z + 1).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 1, y + 1, z + 1).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 1, y + 1, z + 0).color(r, g, b, a).endVertex();
                // North
                buffer.vertex(matrix, x + 0, y + 1, z + 0).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 1, y + 1, z + 0).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 1, y + 0, z + 0).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 0, y + 0, z + 0).color(r, g, b, a).endVertex();
                // South
                buffer.vertex(matrix, x + 0, y + 1, z + 1).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 0, y + 0, z + 1).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 1, y + 0, z + 1).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 1, y + 1, z + 1).color(r, g, b, a).endVertex();
                // East
                buffer.vertex(matrix, x + 0, y + 1, z + 0).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 0, y + 0, z + 0).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 0, y + 0, z + 1).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 0, y + 1, z + 1).color(r, g, b, a).endVertex();
                // West
                buffer.vertex(matrix, x + 1, y + 1, z + 0).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 1, y + 1, z + 1).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 1, y + 0, z + 1).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, x + 1, y + 0, z + 0).color(r, g, b, a).endVertex();

                tes.end();
                RenderSystem.enableDepthTest();
                RenderSystem.depthFunc(0x207);
            }

        }
    }

}
