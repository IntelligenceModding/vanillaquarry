package de.unhappycodings.quarry.common.blockentity;

import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.common.block.QuarryBlock;
import de.unhappycodings.quarry.common.config.CommonConfig;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class EnergyQuarryEntity extends QuarryEntity {
    public final SimpleEnergyStorage energyStorage = new SimpleEnergyStorage(
            CommonConfig.energyQuarryEnergyCapacity.get(),
            CommonConfig.energyQuarryMaxReceive.get(),
            0
    ) {
        @Override
        protected void onFinalCommit() {
            markEnergyChanged();
        }
    };

    public EnergyQuarryEntity(BlockPos pos, BlockState blockState) {
        super(Quarry.ENERGY_QUARRY_ENTITY.get(), pos, blockState);
    }

    @Override
    protected void handlePower(BlockPos pos, BlockState state) {
        boolean powered = energyStorage.amount > 0;

        if (powered != state.getValue(QuarryBlock.POWERED))
            level.setBlockAndUpdate(pos, state.setValue(QuarryBlock.POWERED, powered));
        if (!state.getValue(QuarryBlock.POWERED) && state.getValue(QuarryBlock.ACTIVE))
            level.setBlockAndUpdate(pos, state.setValue(QuarryBlock.ACTIVE, powered));
        if (!state.getValue(QuarryBlock.ACTIVE) && state.getValue(QuarryBlock.WORKING))
            level.setBlockAndUpdate(pos, state.setValue(QuarryBlock.POWERED, powered).setValue(QuarryBlock.WORKING, false));

        if (energyStorage.amount >= CommonConfig.energyQuarryIdleConsumption.get() && burnTicks >= 20 && !state.getValue(QuarryBlock.ACTIVE)) {
            consumePower(CommonConfig.energyQuarryIdleConsumption.get());
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
        return energyStorage.amount > amount;
    }

    @Override
    protected void consumePower(int amount) {
        setEnergy(energyStorage.amount - amount);
    }

    @Override
    public Storage<ItemVariant> getSidedStorage(Direction side) {
        if (side == Direction.UP) return null;
        return super.getSidedStorage(side);
    }

    public @Nullable EnergyStorage getEnergyStorage(Direction side) {
        return energyStorage;
    }

    @Override
    public boolean isEnergyPowered() {
        return true;
    }

    @Override
    public int getBurnTime() {
        return (int) Math.min(Integer.MAX_VALUE, energyStorage.amount);
    }

    @Override
    public void setBurnTime(int burnTime) {
        setEnergy(burnTime);
    }

    @Override
    public int getTotalBurnTime() {
        return (int) Math.min(Integer.MAX_VALUE, energyStorage.capacity);
    }

    @Override
    public void setTotalBurnTime(int totalBurnTime) {
    }

    @Override
    public int getEnergyStored() {
        return getBurnTime();
    }

    @Override
    public int getEnergyCapacity() {
        return getTotalBurnTime();
    }

    @Override
    public long getStoredFuelTime() {
        return energyStorage.amount;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putLong("Energy", energyStorage.amount);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        setEnergy(input.getLongOr("Energy", 0L));
    }

    @Override
    protected void writeBlockEntityData(ValueOutput output) {
        super.writeBlockEntityData(output);
        output.putLong("Energy", energyStorage.amount);
    }

    @Override
    protected void discardBlockEntityComponentData(ValueOutput output) {
        super.discardBlockEntityComponentData(output);
        output.discard("Energy");
    }

    private void setEnergy(long amount) {
        energyStorage.amount = Math.max(0, Math.min(energyStorage.capacity, amount));
        markEnergyChanged();
    }

    private void markEnergyChanged() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }
}
