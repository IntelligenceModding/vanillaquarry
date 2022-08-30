package de.unhappycodings.vanillaquarry.common.blocks;


import de.unhappycodings.vanillaquarry.VanillaQuarry;
import de.unhappycodings.vanillaquarry.common.registration.Registration;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {

    public static final RegistryObject<QuarryBlock> QUARRY = register("quarry_block", QuarryBlock::new);

    private ModBlocks() {
        throw new IllegalStateException("ModBlocks class");
    }

    public static void register() {

    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = Registration.BLOCKS.register(name, block);
        Registration.ITEMS.register(name, () -> new BlockItem(toReturn.get(), new Item.Properties().tab(VanillaQuarry.creativeTab)));
        return toReturn;
    }

}
