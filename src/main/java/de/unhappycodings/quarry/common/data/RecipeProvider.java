package de.unhappycodings.quarry.common.data;

import de.unhappycodings.quarry.Quarry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {

    public RecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    public void buildRecipes() {
        shaped(RecipeCategory.MISC, Quarry.QUARRY_BLOCK.get())
                .define('a', Items.REDSTONE_TORCH)
                .define('b', Items.REDSTONE)
                .define('c', Items.REPEATER)
                .define('d', Items.COMPARATOR)
                .define('e', Items.FURNACE)
                .define('f', Items.HOPPER)
                .define('g', Items.DIAMOND_SHOVEL)
                .define('h', Items.DIAMOND_PICKAXE)
                .define('i', Items.DIAMOND_AXE)
                .pattern("bfa").pattern("geh").pattern("cid")
                .unlockedBy("has_item", has(Items.FURNACE)).save(output,
                        BuiltInRegistries.BLOCK.getKey(Quarry.QUARRY_BLOCK.get()) + "_crafted");
        shaped(RecipeCategory.MISC, Quarry.ENERGY_QUARRY_BLOCK.get())
                .define('q', Quarry.QUARRY_BLOCK.get())
                .define('r', Items.REDSTONE_BLOCK)
                .define('d', Items.REDSTONE)
                .pattern("ddd").pattern("dqd").pattern("drd")
                .unlockedBy("has_item", has(Quarry.QUARRY_BLOCK.get())).save(output,
                        BuiltInRegistries.BLOCK.getKey(Quarry.ENERGY_QUARRY_BLOCK.get()) + "_crafted");
        shapeless(RecipeCategory.MISC, Quarry.QUARRY_BLOCK.get())
                .requires(Quarry.ENERGY_QUARRY_BLOCK.get())
                .unlockedBy("has_item", has(Quarry.ENERGY_QUARRY_BLOCK.get())).save(output,
                        Quarry.MOD_ID + ":quarry_block_from_energy_quarry");
        shaped(RecipeCategory.MISC, Quarry.AREA_CARD.get(), 1)
                .define('a', Items.REDSTONE_TORCH)
                .define('b', Items.REDSTONE)
                .define('c', Items.OBSERVER)
                .define('d', Items.REPEATER)
                .define('e', Items.COMPARATOR)
                .define('f', Items.HOPPER)
                .pattern("  a").pattern("bcb").pattern("def")
                .unlockedBy("has_item", has(Items.OBSERVER)).save(output,
                        BuiltInRegistries.ITEM.getKey(Quarry.AREA_CARD.get()) + "_crafted");
    }

    public static class Runner extends net.minecraft.data.recipes.RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected net.minecraft.data.recipes.RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new RecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return Quarry.MOD_ID + " recipes";
        }
    }
}
