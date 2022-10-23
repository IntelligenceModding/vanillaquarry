package de.unhappycodings.quarry.common.blocks;

import de.unhappycodings.quarry.common.blockentity.ModBlockEntities;
import de.unhappycodings.quarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.quarry.common.network.PacketHandler;
import de.unhappycodings.quarry.common.network.toserver.QuarryBooleanPacket;
import de.unhappycodings.quarry.common.network.toserver.QuarryIntPacket;
import de.unhappycodings.quarry.common.network.toserver.QuarryModePacket;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Random;

public class QuarryBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty WORKING = BooleanProperty.create("working");

    public QuarryBlock() {
        super(Properties.copy(Blocks.STONE).strength(3.0F, 6.0F).lightLevel(state -> state.getValue(QuarryBlock.POWERED) ? 10 : 0));
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(WORKING, false).setValue(ACTIVE, false).setValue(FACING, Direction.NORTH));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void setPlacedBy(Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, @Nullable LivingEntity pPlacer, @NotNull ItemStack pStack) {
        QuarryBlockEntity blockEntity = (QuarryBlockEntity) pLevel.getBlockEntity(pPos);
        if (Objects.equals(blockEntity.getOwner(), "undefined")) blockEntity.setOwner(pPlacer.getStringUUID());
        blockEntity.setChanged();
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
    }

    @Override
    public void animateTick(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Random pRandom) {
        if (pState.getValue(POWERED)) {
            double d0 = (double) pPos.getX() + 0.5D;
            double d1 = (double) pPos.getY();
            double d2 = (double) pPos.getZ() + 0.5D;
            if (pRandom.nextDouble() < 0.1D) {
                pLevel.playLocalSound(d0, d1, d2, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }
            Direction direction = pState.getValue(FACING);
            Direction.Axis directionAxis = direction.getAxis();
            double d3 = 0.52D;
            double d4 = pRandom.nextDouble() * 0.6D - 0.3D;
            double d5 = directionAxis == Direction.Axis.X ? (double) direction.getStepX() * 0.52D : d4;
            double d6 = pRandom.nextDouble() * 6.0D / 16.0D;
            double d7 = directionAxis == Direction.Axis.Z ? (double) direction.getStepZ() * 0.52D : d4;
            pLevel.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
            pLevel.addParticle(ParticleTypes.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
        }
    }

    @SuppressWarnings("deprecation")
    @NotNull
    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level levelIn, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand handIn, @NotNull BlockHitResult hit) {
        if (!Objects.equals(((QuarryBlockEntity) levelIn.getBlockEntity(pos)).getOwner(), player.getStringUUID()) && ((QuarryBlockEntity) levelIn.getBlockEntity(pos)).getLocked()) {
            if (levelIn.isClientSide && handIn == InteractionHand.MAIN_HAND) {
                String owner = ((QuarryBlockEntity) levelIn.getBlockEntity(pos)).getOwner();
                if (owner.isEmpty()) owner = "undefined";
                player.sendMessage(new TranslatableComponent("gui.quarry.quarry.message.quarry_from").append(" " + owner + " ").append(new TranslatableComponent("gui.quarry.quarry.message.is_locked")).withStyle(ChatFormatting.YELLOW), Util.NIL_UUID);
            }
            return InteractionResult.SUCCESS;
        }
        MenuProvider namedContainerProvider = this.getMenuProvider(state, levelIn, pos);
        if (namedContainerProvider != null) {
            if (levelIn.isClientSide) {
                PacketHandler.sendToServer(new QuarryIntPacket(pos, (byte) 0, "speed"));
                PacketHandler.sendToServer(new QuarryModePacket(pos, (byte) -1));
                PacketHandler.sendToServer(new QuarryBooleanPacket(pos, true, "locked"));
            }
            if (player instanceof ServerPlayer serverPlayerEntity)
                NetworkHooks.openGui(serverPlayerEntity, namedContainerProvider, pos);

        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(POWERED, false).setValue(WORKING, false).setValue(ACTIVE, false).setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, WORKING, ACTIVE, FACING);
    }

    @SuppressWarnings("deprecation")
    @NotNull
    @Override
    public RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState blockState, @NotNull BlockEntityType<T> type) {
        return level.isClientSide ? null : (a, b, c, blockEntity) -> ((QuarryBlockEntity) blockEntity).tick();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return ModBlockEntities.QUARRY_BLOCK.get().create(pos, state);
    }

}
