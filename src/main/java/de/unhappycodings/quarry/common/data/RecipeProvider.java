package de.unhappycodings.quarry.common.data;

import de.unhappycodings.quarry.Quarry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import org.apache.http.impl.conn.SchemeRegistryFactory;

import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {

    public RecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Quarry.QUARRY_BLOCK.get())
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
                .unlockedBy("has_item", has(Items.FURNACE)).save(recipeOutput,
                        BuiltInRegistries.BLOCK.getKey(Quarry.QUARRY_BLOCK.get()) + "_crafted");
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Quarry.AREA_CARD.get(), 1)
                .define('a', Items.REDSTONE_TORCH)
                .define('b', Items.REDSTONE)
                .define('c', Items.OBSERVER)
                .define('d', Items.REPEATER)
                .define('e', Items.COMPARATOR)
                .define('f', Items.HOPPER)
                .pattern("  a").pattern("bcb").pattern("def")
                .unlockedBy("has_item", has(Items.OBSERVER)).save(recipeOutput,
                        BuiltInRegistries.ITEM.getKey(Quarry.AREA_CARD.get()) + "_crafted");
        super.buildRecipes(recipeOutput);
    }
}
