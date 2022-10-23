package de.unhappycodings.quarry.common.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.client.config.ClientConfig;
import de.unhappycodings.quarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.quarry.common.blocks.QuarryBlock;
import de.unhappycodings.quarry.common.config.CommonConfig;
import de.unhappycodings.quarry.common.item.AreaCardItem;
import de.unhappycodings.quarry.common.util.NbtUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = Quarry.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {

    @SubscribeEvent
    public static void onQuarryBlockDestroy(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        Level level = event.getPlayer().getLevel();
        BlockPos pos = event.getPos();
        if (!(level.getBlockState(pos).getBlock() instanceof QuarryBlock)) return;
        if (!Objects.equals(((QuarryBlockEntity) level.getBlockEntity(pos)).getOwner(), player.getStringUUID()) && ((QuarryBlockEntity) event.getLevel().getBlockEntity(pos)).getLocked()) {
            String owner = ((QuarryBlockEntity) level.getBlockEntity(pos)).getOwner();
            event.setCanceled(true);
            if (owner.isEmpty()) owner = "undefined";
            player.sendSystemMessage(Component.translatable("gui.quarry.message.quarry_from").append(" " + owner + " ").append(Component.translatable("gui.quarry.message.is_locked")).withStyle(ChatFormatting.YELLOW));
        }
    }


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
