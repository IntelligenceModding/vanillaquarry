package de.unhappycodings.quarry.common.tags;

import de.unhappycodings.quarry.Quarry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;

public class ModTags {

    public static class Block {
        public static final TagKey<net.minecraft.world.level.block.Block> BURNABLES = tag("burnables");

        private static TagKey<net.minecraft.world.level.block.Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(Quarry.MOD_ID, name));
        }

        private static TagKey<net.minecraft.world.level.block.Block> forgeTag(String name) {
            return BlockTags.create(new ResourceLocation("forge", name));
        }
    }
}
