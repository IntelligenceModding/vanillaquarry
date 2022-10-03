package de.unhappycodings.quarry.common.blocks;


import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.common.item.QuarryBlockItem;
import de.unhappycodings.quarry.common.registration.Registration;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {

    public static final RegistryObject<QuarryBlock> QUARRY = register("quarry_block", QuarryBlock::new, QuarryBlockItem::new);

    private ModBlocks() {
        throw new IllegalStateException("ModBlocks class");
    }

    public static void register() {
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = Registration.BLOCKS.register(name, block);
        Registration.ITEMS.register(name, () -> new BlockItem(toReturn.get(), new Item.Properties().tab(Quarry.creativeTab)));
        return toReturn;
    }

    private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<T> block) {
        return Registration.BLOCKS.register(name, block);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block, Supplier<BlockItem> blockItem) {
        RegistryObject<T> registryObject = registerNoItem(name, block);
        Registration.ITEMS.register(name, blockItem);
        return registryObject;
    }

}
