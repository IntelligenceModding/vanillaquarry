package de.unhappycodings.vanillaquarry.common.tags;

import de.unhappycodings.vanillaquarry.VanillaQuarry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;

public class ModTags {

    public static class Block {
        public static final TagKey<net.minecraft.world.level.block.Block> BURNABLES
                = tag("burnables");

        private static TagKey<net.minecraft.world.level.block.Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(VanillaQuarry.MOD_ID, name));
        }

        private static TagKey<net.minecraft.world.level.block.Block> forgeTag(String name) {
            return BlockTags.create(new ResourceLocation("forge", name));
        }
    }
}
