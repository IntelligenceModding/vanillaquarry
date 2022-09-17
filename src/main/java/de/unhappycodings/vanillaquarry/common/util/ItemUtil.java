package de.unhappycodings.vanillaquarry.common.util;

import de.unhappycodings.vanillaquarry.common.tags.ModTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemUtil {

    public static ResourceLocation getRegName(Item item) {
        return ForgeRegistries.ITEMS.getKey(item);
    }

    public static String getRegString(Item item) {
        return ForgeRegistries.ITEMS.getKey(item).toString();
    }

    public static ResourceLocation getRegName(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    public static String getRegString(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block).toString();
    }

}
