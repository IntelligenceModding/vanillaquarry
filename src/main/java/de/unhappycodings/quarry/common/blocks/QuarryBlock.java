package de.unhappycodings.quarry.common.blocks;

import de.unhappycodings.quarry.common.blockentity.ModBlockEntities;
import de.unhappycodings.quarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.quarry.common.network.PacketHandler;
import de.unhappycodings.quarry.common.network.toserver.QuarryBooleanPacket;
import de.unhappycodings.quarry.common.network.toserver.QuarryIntPacket;
import de.unhappycodings.quarry.common.network.toserver.QuarryModePacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class QuarryBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE_NORTH = Shapes.or(Block.box(0, 0, 0, 16, 1, 16), Block.box(1, 1, 3, 15, 15, 15), Block.box(0, 4, 5, 1, 8, 11), Block.box(0, 8, 6, 1, 9, 10), Block.box(0, 3, 6, 1, 4, 10), Block.box(1, 6, 1, 15, 15, 3), Block.box(12, 1, 1, 15, 6, 3), Block.box(1, 1, 1, 4, 6, 3), Block.box(6, 15, 5, 10, 16, 11), Block.box(2, 7, 0, 14, 11, 1), Block.box(10, 15, 6, 11, 16, 10), Block.box(5, 15, 6, 6, 16, 10), Block.box(0, 15, 0, 16, 16, 1), Block.box(0, 15, 15, 16, 16, 16), Block.box(0, 15, 1, 1, 16, 15), Block.box(15, 15, 1, 16, 16, 15), Block.box(0, 1, 0, 1, 15, 1), Block.box(0, 1, 15, 1, 15, 16), Block.box(15, 1, 15, 16, 15, 16), Block.box(10, 1, 15, 11, 15, 16), Block.box(5, 1, 15, 6, 15, 16), Block.box(15, 1, 0, 16, 15, 1), Block.box(15, 1, 4, 16, 15, 5), Block.box(15, 1, 11, 16, 15, 12));
    public static final VoxelShape SHAPE_EAST = Shapes.or(Block.box(0, 0, 0, 16, 1, 16), Block.box(1, 1, 1, 13, 15, 15), Block.box(5, 4, 0, 11, 8, 1), Block.box(6, 8, 0, 10, 9, 1), Block.box(6, 3, 0, 10, 4, 1), Block.box(13, 6, 1, 15, 15, 15), Block.box(13, 1, 12, 15, 6, 15), Block.box(13, 1, 1, 15, 6, 4), Block.box(5, 15, 6, 11, 16, 10), Block.box(15, 7, 2, 16, 11, 14), Block.box(6, 15, 10, 10, 16, 11), Block.box(6, 15, 5, 10, 16, 6), Block.box(15, 15, 0, 16, 16, 16), Block.box(0, 15, 0, 1, 16, 16), Block.box(1, 15, 0, 15, 16, 1), Block.box(1, 15, 15, 15, 16, 16), Block.box(15, 1, 0, 16, 15, 1), Block.box(0, 1, 0, 1, 15, 1), Block.box(0, 1, 15, 1, 15, 16), Block.box(0, 1, 10, 1, 15, 11), Block.box(0, 1, 5, 1, 15, 6), Block.box(15, 1, 15, 16, 15, 16), Block.box(11, 1, 15, 12, 15, 16), Block.box(4, 1, 15, 5, 15, 16));
    public static final VoxelShape SHAPE_SOUTH = Shapes.or(Block.box(0, 0, 0, 16, 1, 16), Block.box(1, 1, 1, 15, 15, 13), Block.box(15, 4, 5, 16, 8, 11), Block.box(15, 8, 6, 16, 9, 10), Block.box(15, 3, 6, 16, 4, 10), Block.box(1, 6, 13, 15, 15, 15), Block.box(1, 1, 13, 4, 6, 15), Block.box(12, 1, 13, 15, 6, 15), Block.box(6, 15, 5, 10, 16, 11), Block.box(2, 7, 15, 14, 11, 16), Block.box(5, 15, 6, 6, 16, 10), Block.box(10, 15, 6, 11, 16, 10), Block.box(0, 15, 15, 16, 16, 16), Block.box(0, 15, 0, 16, 16, 1), Block.box(15, 15, 1, 16, 16, 15), Block.box(0, 15, 1, 1, 16, 15), Block.box(15, 1, 15, 16, 15, 16), Block.box(15, 1, 0, 16, 15, 1), Block.box(0, 1, 0, 1, 15, 1), Block.box(5, 1, 0, 6, 15, 1), Block.box(10, 1, 0, 11, 15, 1), Block.box(0, 1, 15, 1, 15, 16), Block.box(0, 1, 11, 1, 15, 12), Block.box(0, 1, 4, 1, 15, 5));
    public static final VoxelShape SHAPE_WEST = Shapes.or(Block.box(0, 0, 0, 16, 1, 16), Block.box(3, 1, 1, 15, 15, 15), Block.box(5, 4, 15, 11, 8, 16), Block.box(6, 8, 15, 10, 9, 16), Block.box(6, 3, 15, 10, 4, 16), Block.box(1, 6, 1, 3, 15, 15), Block.box(1, 1, 1, 3, 6, 4), Block.box(1, 1, 12, 3, 6, 15), Block.box(5, 15, 6, 11, 16, 10), Block.box(0, 7, 2, 1, 11, 14), Block.box(6, 15, 5, 10, 16, 6), Block.box(6, 15, 10, 10, 16, 11), Block.box(0, 15, 0, 1, 16, 16), Block.box(15, 15, 0, 16, 16, 16), Block.box(1, 15, 15, 15, 16, 16), Block.box(1, 15, 0, 15, 16, 1), Block.box(0, 1, 15, 1, 15, 16), Block.box(15, 1, 15, 16, 15, 16), Block.box(15, 1, 0, 16, 15, 1), Block.box(15, 1, 5, 16, 15, 6), Block.box(15, 1, 10, 16, 15, 11), Block.box(0, 1, 0, 1, 15, 1), Block.box(4, 1, 0, 5, 15, 1), Block.box(11, 1, 0, 12, 15, 1));
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty WORKING = BooleanProperty.create("working");

    public QuarryBlock() {
        super(Properties.copy(Blocks.STONE).strength(3.0F, 6.0F).isRedstoneConductor((blockState, blockGetter, blockPos) -> false));
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(WORKING, false).setValue(ACTIVE, false).setValue(FACING, Direction.NORTH));
    }

    @SuppressWarnings("deprecation")
    @NotNull
    @Override
    public VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull CollisionContext collisionContext) {
        return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    }

    @SuppressWarnings("deprecation")
    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull CollisionContext collisionContext) {
        return switch (state.getValue(FACING)) {
            case NORTH -> SHAPE_NORTH;
            case EAST -> SHAPE_EAST;
            case SOUTH -> SHAPE_SOUTH;
            default -> SHAPE_WEST;
        };
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void setPlacedBy(Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, @Nullable LivingEntity pPlacer, @NotNull ItemStack pStack) {
        QuarryBlockEntity blockEntity = (QuarryBlockEntity) pLevel.getBlockEntity(pPos);
        if (Objects.equals(blockEntity.getOwner(), "undefined") || !Objects.equals(blockEntity.getOwner(), "@"))
            blockEntity.setOwner(pPlacer.getName().getString() + "@" + pPlacer.getStringUUID());
        blockEntity.setChanged();
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
    }

    @SuppressWarnings("deprecation")
    @NotNull
    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level levelIn, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand handIn, @NotNull BlockHitResult hit) {
        QuarryBlockEntity blockEntity = (QuarryBlockEntity) levelIn.getBlockEntity(pos);
        if (blockEntity == null) return InteractionResult.FAIL;

        if ((!Objects.equals(blockEntity.getOwner(), player.getName().getString() + "@" + player.getStringUUID()) && blockEntity.getLocked()) && !player.hasPermissions(2)) {
            if (levelIn.isClientSide && handIn == InteractionHand.MAIN_HAND) {
                String owner = blockEntity.getOwner();
                if (owner.isEmpty()) owner = "undefined";
                player.sendSystemMessage(Component.translatable("gui.quarry.message.quarry_from").append(" " + owner.split("@")[0] + " ").append(Component.translatable("gui.quarry.message.is_locked")).withStyle(ChatFormatting.YELLOW));
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
                NetworkHooks.openScreen(serverPlayerEntity, namedContainerProvider, pos);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void animateTick(BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull RandomSource pRandom) {
        if (pState.getValue(POWERED)) {
            double d0 = (double) pPos.getX() + 0.5D;
            double d1 = pPos.getY();
            double d2 = (double) pPos.getZ() + 0.5D;
            if (pRandom.nextDouble() < 0.1D) {
                pLevel.playLocalSound(d0, d1, d2, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            } else if (pRandom.nextDouble() < 0.2D) {
                pLevel.playLocalSound(d0, d1, d2, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }

            Direction direction = pState.getValue(FACING);
            Direction.Axis directionAxis = direction.getAxis();
            double d4 = pRandom.nextDouble() * 0.6D - 0.3D;
            double d5 = directionAxis == Direction.Axis.X ? (double) direction.getStepX() * 0.4D : d4;
            double d6 = pRandom.nextDouble() * 9.0D / 16.0D;
            double d7 = directionAxis == Direction.Axis.Z ? (double) direction.getStepZ() * 0.4D : d4;
            pLevel.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
            pLevel.addParticle(ParticleTypes.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);

            if (pState.getValue(WORKING)) {
                pLevel.addParticle(ParticleTypes.SMOKE, d0, d1 + 1.1D, d2, 0.0D, 0.0D, 0.0D);
            }
        }
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

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(POWERED) ? 15 : 0;
    }

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
