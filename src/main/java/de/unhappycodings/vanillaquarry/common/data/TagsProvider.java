package de.unhappycodings.vanillaquarry.common.data;

import de.unhappycodings.vanillaquarry.VanillaQuarry;
import de.unhappycodings.vanillaquarry.common.registration.Registration;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class TagsProvider extends net.minecraft.data.tags.TagsProvider<Block> {

    @SuppressWarnings("deprecation")
    protected TagsProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, Registry.BLOCK, VanillaQuarry.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {

        // Interate through all Blocks and add them the "Minable with Pickaxe" tag
        for (RegistryObject<Block> block : Registration.BLOCKS.getEntries()) {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.get());
        }

    }

    @NotNull
    @Override
    protected Path getPath(ResourceLocation location) {
        return this.generator.getOutputFolder().resolve("data/" + location.getNamespace() + "/tags/blocks/" + location.getPath() + ".json");
    }

    @NotNull
    @Override
    public String getName() {
        return "Block tags";
    }
}
