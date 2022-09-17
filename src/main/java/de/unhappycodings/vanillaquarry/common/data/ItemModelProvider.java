package de.unhappycodings.vanillaquarry.common.data;

import de.unhappycodings.vanillaquarry.VanillaQuarry;
import de.unhappycodings.vanillaquarry.common.blocks.ModBlocks;
import de.unhappycodings.vanillaquarry.common.item.ModItems;
import de.unhappycodings.vanillaquarry.common.util.ItemUtil;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Objects;

public class ItemModelProvider extends net.minecraftforge.client.model.generators.ItemModelProvider {

    public ItemModelProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, VanillaQuarry.MOD_ID, exFileHelper);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void registerModels() {
        withExistingParent(ItemUtil.getRegString(ModBlocks.QUARRY.get()), new ResourceLocation(VanillaQuarry.MOD_ID, "block/quarry_block_off"));
        simpleItem(ModItems.AREA_CARD.get());
    }

    private void simpleItem(Item item) {
        withExistingParent(ItemUtil.getRegString(item), "item/generated").texture("layer0", new ResourceLocation(this.modid, "item/" + ItemUtil.getRegName(item).getPath()));
    }

}