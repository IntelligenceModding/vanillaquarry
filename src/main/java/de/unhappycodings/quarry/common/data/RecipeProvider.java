package de.unhappycodings.quarry.common.data;

import de.unhappycodings.quarry.common.blocks.ModBlocks;
import de.unhappycodings.quarry.common.item.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {

    public RecipeProvider(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(ModBlocks.QUARRY.get(), 1)
                .define('a', Items.REDSTONE_TORCH)
                .define('b', Items.REDSTONE)
                .define('c', Items.REPEATER)
                .define('d', Items.COMPARATOR)
                .define('e', Items.ANDESITE)
                .define('f', Items.FURNACE)
                .pattern("bea").pattern("efe").pattern("ced")
                .unlockedBy("has_item", has(Items.OBSERVER)).save(consumer,
                        ModBlocks.QUARRY.get().getRegistryName().getPath() + "_crafted");
        ShapedRecipeBuilder.shaped(ModItems.AREA_CARD.get(), 1)
                .define('a', Items.REDSTONE_TORCH)
                .define('b', Items.REDSTONE)
                .define('c', Items.OBSERVER)
                .define('d', Items.REPEATER)
                .define('e', Items.COMPARATOR)
                .define('f', Items.HOPPER)
                .pattern("  a").pattern("bcb").pattern("def")
                .unlockedBy("has_item", has(Items.OBSERVER)).save(consumer,
                        ModItems.AREA_CARD.get().getRegistryName().getPath() + "_crafted");
    }
}
