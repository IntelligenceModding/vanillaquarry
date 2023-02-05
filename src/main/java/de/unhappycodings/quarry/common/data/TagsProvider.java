package de.unhappycodings.quarry.common.data;

import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.common.registration.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class TagsProvider extends net.minecraft.data.tags.TagsProvider<Block> {
    private final DeferredRegister<Block> blockRegistry;
    private PackOutput packOutput;

    @SuppressWarnings("deprecation")
    protected TagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> future, @Nullable ExistingFileHelper existingFileHelper, DeferredRegister<Block> registry) {
        super(packOutput, ForgeRegistries.BLOCKS.getRegistryKey(), future, Quarry.MOD_ID, existingFileHelper);
        this.packOutput = packOutput;
        this.blockRegistry = registry;
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        // Interate through all Blocks and add them the "Mineable with Pickaxe" tag
        for (RegistryObject<Block> block : Registration.BLOCKS.getEntries()) {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(getResourceKey(block));
        }
    }

    @NotNull
    @Override
    protected Path getPath(ResourceLocation location) {
        return this.packOutput.getOutputFolder().resolve("data/" + location.getNamespace() + "/tags/blocks/" + location.getPath() + ".json");
    }

    @NotNull
    @Override
    public String getName() {
        return "Block tags";
    }

    public ResourceKey<Block> getResourceKey(RegistryObject<Block> block) {
        return ForgeRegistries.BLOCKS.getResourceKey(block.get()).get();
    }
}