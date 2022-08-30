package de.unhappycodings.vanillaquarry.common.blockentity;

import de.unhappycodings.vanillaquarry.VanillaQuarry;
import de.unhappycodings.vanillaquarry.common.blocks.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, VanillaQuarry.MOD_ID);

    public static final RegistryObject<BlockEntityType<QuarryBlockEntity>> QUARRY_BLOCK = BLOCK_ENTITIES.register("quarry_block",
            () -> BlockEntityType.Builder.of(QuarryBlockEntity::new, ModBlocks.QUARRY.get()).build(null));

    private ModBlockEntities() {
    }

}
