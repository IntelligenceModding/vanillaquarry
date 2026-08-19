package de.unhappycodings.quarry.common.blockentity;

import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.common.block.QuarryBlock;
import de.unhappycodings.quarry.common.config.CommonConfig;
import de.unhappycodings.quarry.common.config.ServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class FEQuarryEntity extends QuarryEntity {
    private final SimpleEnergyHandler energyHandler = new SimpleEnergyHandler(
            ServerConfig.feQuarryEnergyCapacity.get(),
            ServerConfig.feQuarryMaxReceive.get(),
            0
    ) {
        @Override
        protected void onEnergyChanged(int transferAmount) {
            FEQuarryEntity.this.setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    };

    public FEQuarryEntity(BlockPos pos, BlockState blockState) {
        super(Quarry.FE_QUARRY_ENTITY.get(), pos, blockState);
    }

    @Override
    public void tick(net.minecraft.world.level.Level level, BlockPos pos, BlockState state, QuarryEntity blockEntity) {
        if (!ServerConfig.enableFEQuarry.get()) {
            if (state.getValue(QuarryBlock.POWERED) || state.getValue(QuarryBlock.ACTIVE) || state.getValue(QuarryBlock.WORKING)) {
                level.setBlockAndUpdate(pos, state.setValue(QuarryBlock.POWERED, false).setValue(QuarryBlock.ACTIVE, false).setValue(QuarryBlock.WORKING, false));
            }
            return;
        }

        super.tick(level, pos, state, blockEntity);
    }

    @Override
    protected void handlePower(BlockPos pos, BlockState state) {
        boolean powered = energyHandler.getAmountAsInt() > 0;

        if (powered != state.getValue(QuarryBlock.POWERED))
            level.setBlockAndUpdate(pos, state.setValue(QuarryBlock.POWERED, powered));
        if (!state.getValue(QuarryBlock.POWERED) && state.getValue(QuarryBlock.ACTIVE))
            level.setBlockAndUpdate(pos, state.setValue(QuarryBlock.ACTIVE, powered));
        if (!state.getValue(QuarryBlock.ACTIVE) && state.getValue(QuarryBlock.WORKING))
            level.setBlockAndUpdate(pos, state.setValue(QuarryBlock.POWERED, powered).setValue(QuarryBlock.WORKING, false));

        if (energyHandler.getAmountAsInt() >= CommonConfig.feQuarryIdleConsumption.get() && burnTicks >= 20 && !state.getValue(QuarryBlock.ACTIVE)) {
            consumePower(CommonConfig.feQuarryIdleConsumption.get());
            burnTicks = 0;
        }

        burnTicks++;
    }

    @Override
    protected boolean usesFuelItems() {
        return false;
    }

    @Override
    protected boolean hasPowerFor(float amount) {
        return energyHandler.getAmountAsInt() > amount;
    }

    @Override
    protected void consumePower(int amount) {
        energyHandler.set(Math.max(0, energyHandler.getAmountAsInt() - amount));
    }

    @Override
    protected ResourceHandler<ItemResource> getSidedResourceHandler(Direction side) {
        if (side == Direction.UP) return null;
        return super.getSidedResourceHandler(side);
    }

    public EnergyHandler getEnergyHandler(Direction side) {
        return energyHandler;
    }

    @Override
    public boolean isEnergyPowered() {
        return true;
    }

    @Override
    public int getBurnTime() {
        return energyHandler.getAmountAsInt();
    }

    @Override
    public void setBurnTime(int burnTime) {
        energyHandler.set(Math.max(0, burnTime));
    }

    @Override
    public int getTotalBurnTime() {
        return energyHandler.getCapacityAsInt();
    }

    @Override
    public void setTotalBurnTime(int totalBurnTime) {
    }

    @Override
    public int getEnergyStored() {
        return energyHandler.getAmountAsInt();
    }

    @Override
    public int getEnergyCapacity() {
        return energyHandler.getCapacityAsInt();
    }

    @Override
    public long getStoredFuelTime() {
        return getEnergyStored();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        energyHandler.serialize(output.child("Energy"));
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energyHandler.deserialize(input.childOrEmpty("Energy"));
    }
}
