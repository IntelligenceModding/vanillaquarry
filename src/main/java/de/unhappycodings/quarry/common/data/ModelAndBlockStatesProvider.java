package de.unhappycodings.quarry.common.data;

import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.common.blocks.ModBlocks;
import de.unhappycodings.quarry.common.blocks.QuarryBlock;
import de.unhappycodings.quarry.common.util.ItemUtil;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModelAndBlockStatesProvider extends BlockStateProvider {
    DataGenerator gen;

    public ModelAndBlockStatesProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, Quarry.MOD_ID, exFileHelper);
        this.gen = gen;
    }

    @Override
    protected void registerStatesAndModels() {
        quarryBlock(ModBlocks.QUARRY.get());
    }


    @SuppressWarnings("ConstantConditions")
    public void quarryBlock(QuarryBlock block) {
        ModelFile quarryOff = models().withExistingParent(ItemUtil.getRegString(block) + "_off", new ResourceLocation(Quarry.MOD_ID, "generation/quarry_block")).texture("1", new ResourceLocation(Quarry.MOD_ID, "block/quarry_block_off")).texture("2", new ResourceLocation(Quarry.MOD_ID, "block/quarry_block_side")).texture("3", new ResourceLocation(Quarry.MOD_ID, "block/quarry_block_top")).texture("particle", new ResourceLocation(Quarry.MOD_ID, "block/quarry_block_side"));
        ModelFile quarryIdle = models().withExistingParent(ItemUtil.getRegString(block) + "_idle", new ResourceLocation(Quarry.MOD_ID, "generation/quarry_block")).texture("1", new ResourceLocation(Quarry.MOD_ID, "block/quarry_block_idle")).texture("2", new ResourceLocation(Quarry.MOD_ID, "block/quarry_block_side")).texture("3", new ResourceLocation(Quarry.MOD_ID, "block/quarry_block_top")).texture("particle", new ResourceLocation(Quarry.MOD_ID, "block/quarry_block_side"));
        ModelFile quarryOn = models().withExistingParent(ItemUtil.getRegString(block) + "_on", new ResourceLocation(Quarry.MOD_ID, "generation/quarry_block")).texture("1", new ResourceLocation(Quarry.MOD_ID, "block/quarry_block_on")).texture("2", new ResourceLocation(Quarry.MOD_ID, "block/quarry_block_side")).texture("3", new ResourceLocation(Quarry.MOD_ID, "block/quarry_block_top")).texture("particle", new ResourceLocation(Quarry.MOD_ID, "block/quarry_block_side"));
        ModelFile quarryWorking = models().withExistingParent(ItemUtil.getRegString(block) + "_work", new ResourceLocation(Quarry.MOD_ID, "generation/quarry_block")).texture("1", new ResourceLocation(Quarry.MOD_ID, "block/quarry_block_work")).texture("2", new ResourceLocation(Quarry.MOD_ID, "block/quarry_block_side")).texture("3", new ResourceLocation(Quarry.MOD_ID, "block/quarry_block_top")).texture("particle", new ResourceLocation(Quarry.MOD_ID, "block/quarry_block_side"));
        quarryBlock(block, quarryOff, quarryIdle, quarryOn, quarryWorking);
    }

    public void quarryBlock(QuarryBlock block, ModelFile off, ModelFile idle, ModelFile on, ModelFile work) {
        getVariantBuilder(block).forAllStates(state -> {
            Direction facing = state.getValue(QuarryBlock.FACING);
            boolean powered = state.getValue(QuarryBlock.POWERED);
            boolean working = state.getValue(QuarryBlock.WORKING);
            boolean active = state.getValue(QuarryBlock.ACTIVE);
            if (powered && !active)
                return ConfiguredModel.builder().modelFile(idle).rotationY(getSensorRotation(facing)).build();
            if (active && !working)
                return ConfiguredModel.builder().modelFile(on).rotationY(getSensorRotation(facing)).build();
            if (working) return ConfiguredModel.builder().modelFile(work).rotationY(getSensorRotation(facing)).build();
            return ConfiguredModel.builder().modelFile(off).rotationY(getSensorRotation(facing)).build();
        });
    }

    private static int getSensorRotation(Direction facing) {
        return switch (facing) {
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
    }

}
