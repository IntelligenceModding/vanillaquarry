package de.unhappycodings.quarry.common.data;

import de.unhappycodings.quarry.common.blocks.ModBlocks;
import de.unhappycodings.quarry.common.item.ModItems;
import de.unhappycodings.quarry.common.util.ItemUtil;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {

    public RecipeProvider(DataGenerator gen) {
        super(gen.getPackOutput());
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.QUARRY.get())
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
                .unlockedBy("has_item", has(Items.FURNACE)).save(consumer,
                        ItemUtil.getRegName(ModBlocks.QUARRY.get()).getPath() + "_crafted");
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.AREA_CARD.get(), 1)
                .define('a', Items.REDSTONE_TORCH)
                .define('b', Items.REDSTONE)
                .define('c', Items.OBSERVER)
                .define('d', Items.REPEATER)
                .define('e', Items.COMPARATOR)
                .define('f', Items.HOPPER)
                .pattern("  a").pattern("bcb").pattern("def")
                .unlockedBy("has_item", has(Items.OBSERVER)).save(consumer,
                        ItemUtil.getRegName(ModItems.AREA_CARD.get()).getPath() + "_crafted");
    }

}
