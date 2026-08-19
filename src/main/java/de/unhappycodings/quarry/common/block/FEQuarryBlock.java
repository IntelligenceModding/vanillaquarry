package de.unhappycodings.quarry.common.block;

import com.mojang.serialization.MapCodec;
import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.common.config.ServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FEQuarryBlock extends QuarryBlock {
    public static final MapCodec<FEQuarryBlock> CODEC = simpleCodec(FEQuarryBlock::new);

    public FEQuarryBlock(Properties properties) {
        super(properties);
    }

    public FEQuarryBlock() {
        super();
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return Quarry.FE_QUARRY_ENTITY.get().create(pos, state);
    }

    @Override
    protected boolean isEnabled(Level level) {
        return ServerConfig.enableFEQuarry.get();
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
