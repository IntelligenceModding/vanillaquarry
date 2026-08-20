package de.unhappycodings.quarry.common.block;

import com.mojang.serialization.MapCodec;
import de.unhappycodings.quarry.Quarry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class EnergyQuarryBlock extends QuarryBlock {
    public static final MapCodec<EnergyQuarryBlock> CODEC = simpleCodec(EnergyQuarryBlock::new);

    public EnergyQuarryBlock(Properties properties) {
        super(properties);
    }

    public EnergyQuarryBlock() {
        super();
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return Quarry.ENERGY_QUARRY_ENTITY.get().create(pos, state);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
