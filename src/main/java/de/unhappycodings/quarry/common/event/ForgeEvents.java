package de.unhappycodings.quarry.common.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.client.config.ClientConfig;
import de.unhappycodings.quarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.quarry.common.blocks.ModBlocks;
import de.unhappycodings.quarry.common.blocks.QuarryBlock;
import de.unhappycodings.quarry.common.config.CommonConfig;
import de.unhappycodings.quarry.common.item.AreaCardItem;
import de.unhappycodings.quarry.common.util.NbtUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = Quarry.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {

    @SubscribeEvent
    public static void onQuarryBlockDestroy(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        Level level = event.getPlayer().level();
        BlockPos pos = event.getPos();
        if (!(level.getBlockState(pos).getBlock() instanceof QuarryBlock)) return;
        event.setCanceled(true);
        if ((!Objects.equals(((QuarryBlockEntity) level.getBlockEntity(pos)).getOwner(), player.getName().getString() + "@" + player.getStringUUID()) && ((QuarryBlockEntity) event.getLevel().getBlockEntity(pos)).getLocked()) && !player.hasPermissions(2)) {
            String owner = ((QuarryBlockEntity) level.getBlockEntity(pos)).getOwner();
            if (owner.isEmpty()) owner = "undefined";
            player.sendSystemMessage(Component.translatable("gui.quarry.message.quarry_from").append(" " + owner + " ").append(Component.translatable("gui.quarry.message.is_locked")).withStyle(ChatFormatting.YELLOW));
        } else {
            QuarryBlockEntity machine = (QuarryBlockEntity) level.getBlockEntity(pos);
            ItemStack machineStack = new ItemStack(ModBlocks.QUARRY.get(), 1);
            machine.saveToItem(machineStack);
            if (machine.hasCustomName()) machineStack.setHoverName(machine.getCustomName());
            ItemEntity itementity = new ItemEntity(level, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, machineStack);
            itementity.setDefaultPickUpDelay();
            level.addFreshEntity(itementity);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }


    @SuppressWarnings({"ConstantConditions", "removal"})
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void renderSquareAboveWorldCentre(RenderLevelStageEvent event) {
        if (!ClientConfig.enableAreaCardCornerRendering.get()) return;
        Player player = Minecraft.getInstance().player;
        ItemStack item = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (item.isEmpty()) return;
        if (item.getItem() instanceof AreaCardItem) {
            CompoundTag nbt = item.getOrCreateTag();
            if (nbt.contains("pos1"))
                renderCube(event, NbtUtil.getPos((CompoundTag) nbt.get("pos1")), Color.decode(CommonConfig.areaCardOverlayColorFirstCorner.get()));
            if (nbt.contains("pos2"))
                renderCube(event, NbtUtil.getPos((CompoundTag) nbt.get("pos2")), Color.decode(CommonConfig.areaCardOverlayColorSecondCorner.get()));


        }
    }

    public static void renderCube(RenderLevelStageEvent event, BlockPos pos, Color color) {
        VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        float x = pos.getX(), y = pos.getY(), z = pos.getZ();
        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = 0.1f;

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        // Down
        buffer.vertex(x + 0, y + 0, z + 0).color(r, g, b, a).endVertex();
        buffer.vertex(x + 1, y + 0, z + 0).color(r, g, b, a).endVertex();
        buffer.vertex(x + 1, y + 0, z + 1).color(r, g, b, a).endVertex();
        buffer.vertex(x + 0, y + 0, z + 1).color(r, g, b, a).endVertex();
        // Up
        buffer.vertex(x + 0, y + 1, z + 0).color(r, g, b, a).endVertex();
        buffer.vertex(x + 0, y + 1, z + 1).color(r, g, b, a).endVertex();
        buffer.vertex(x + 1, y + 1, z + 1).color(r, g, b, a).endVertex();
        buffer.vertex(x + 1, y + 1, z + 0).color(r, g, b, a).endVertex();
        // North
        buffer.vertex(x + 0, y + 1, z + 0).color(r, g, b, a).endVertex();
        buffer.vertex(x + 1, y + 1, z + 0).color(r, g, b, a).endVertex();
        buffer.vertex(x + 1, y + 0, z + 0).color(r, g, b, a).endVertex();
        buffer.vertex(x + 0, y + 0, z + 0).color(r, g, b, a).endVertex();
        // South
        buffer.vertex(x + 0, y + 1, z + 1).color(r, g, b, a).endVertex();
        buffer.vertex(x + 0, y + 0, z + 1).color(r, g, b, a).endVertex();
        buffer.vertex(x + 1, y + 0, z + 1).color(r, g, b, a).endVertex();
        buffer.vertex(x + 1, y + 1, z + 1).color(r, g, b, a).endVertex();
        // East
        buffer.vertex(x + 0, y + 1, z + 0).color(r, g, b, a).endVertex();
        buffer.vertex(x + 0, y + 0, z + 0).color(r, g, b, a).endVertex();
        buffer.vertex(x + 0, y + 0, z + 1).color(r, g, b, a).endVertex();
        buffer.vertex(x + 0, y + 1, z + 1).color(r, g, b, a).endVertex();
        // West
        buffer.vertex(x + 1, y + 1, z + 0).color(r, g, b, a).endVertex();
        buffer.vertex(x + 1, y + 1, z + 1).color(r, g, b, a).endVertex();
        buffer.vertex(x + 1, y + 0, z + 1).color(r, g, b, a).endVertex();
        buffer.vertex(x + 1, y + 0, z + 0).color(r, g, b, a).endVertex();

        vertexBuffer.bind();
        vertexBuffer.upload(buffer.end());

        Vec3 view = Minecraft.getInstance().getEntityRenderDispatcher().camera.getPosition();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        PoseStack matrix = event.getPoseStack();
        matrix.pushPose();
        matrix.translate(-view.x, -view.y, -view.z);
        vertexBuffer.drawWithShader(matrix.last().pose(), new Matrix4f(event.getProjectionMatrix()), RenderSystem.getShader());
        VertexBuffer.unbind();
        matrix.popPose();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

}
