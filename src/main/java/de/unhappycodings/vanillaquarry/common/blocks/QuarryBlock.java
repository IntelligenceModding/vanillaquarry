package de.unhappycodings.vanillaquarry.common.blocks;

import de.unhappycodings.vanillaquarry.common.blockentity.ModBlockEntities;
import de.unhappycodings.vanillaquarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.vanillaquarry.common.network.PacketHandler;
import de.unhappycodings.vanillaquarry.common.network.toserver.QuarryModePacket;
import de.unhappycodings.vanillaquarry.common.network.toserver.QuarryOwnerPacket;
import de.unhappycodings.vanillaquarry.common.network.toserver.QuarrySpeedPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
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

public class QuarryBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static BooleanProperty POWERED = BooleanProperty.create("powered");
    public static BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static BooleanProperty WORKING = BooleanProperty.create("working");

    public QuarryBlock() {
        super(Properties.copy(Blocks.STONE).strength(3.0F, 6.0F));
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
    public void playerWillDestroy(@NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, Player pPlayer) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof QuarryBlockEntity machine) {
            if (!pLevel.isClientSide) {
                ItemStack machineStack = new ItemStack(this, 1);
                machine.saveToItem(machineStack);
                if (machine.hasCustomName()) machineStack.setHoverName(machine.getCustomName());
                ItemEntity itementity = new ItemEntity(pLevel, (double) pPos.getX() + 0.5D, (double) pPos.getY() + 0.5D, (double) pPos.getZ() + 0.5D, machineStack);
                itementity.setDefaultPickUpDelay();
                pLevel.addFreshEntity(itementity);
            }
        }
        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    @SuppressWarnings("deprecation")
    @NotNull
    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level levelIn, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand handIn, @NotNull BlockHitResult hit) {
        if (!Objects.equals(((QuarryBlockEntity) levelIn.getBlockEntity(pos)).getOwner(), player.getStringUUID()) && ((QuarryBlockEntity) levelIn.getBlockEntity(pos)).getLocked()) {
            if (levelIn.isClientSide && handIn == InteractionHand.MAIN_HAND) {
                String owner = ((QuarryBlockEntity) levelIn.getBlockEntity(pos)).getOwner();
                if (owner.isEmpty()) owner = "undefined";
                player.sendSystemMessage(Component.translatable("gui.vanillaquarry.quarry.message.quarry_from").append(" " + owner + " ").append(Component.translatable("gui.vanillaquarry.quarry.message.is_locked")).withStyle(ChatFormatting.YELLOW));
            }
            return InteractionResult.SUCCESS;
        }
        MenuProvider namedContainerProvider = this.getMenuProvider(state, levelIn, pos);
        if (namedContainerProvider != null) {
            if (levelIn.isClientSide) {
                PacketHandler.sendToServer(new QuarrySpeedPacket(pos, (byte) 0));
                PacketHandler.sendToServer(new QuarryModePacket(pos, (byte) -1));
                PacketHandler.sendToServer(new QuarryOwnerPacket(pos, true));
            }
            if (player instanceof ServerPlayer serverPlayerEntity)
                NetworkHooks.openScreen(serverPlayerEntity, namedContainerProvider, pos);

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
