package de.unhappycodings.quarry.common.data;

import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.common.blocks.ModBlocks;
import de.unhappycodings.quarry.common.item.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Objects;

public class ItemModelProvider extends net.minecraftforge.client.model.generators.ItemModelProvider {

    public ItemModelProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, Quarry.MOD_ID, exFileHelper);
    }


    @SuppressWarnings("ConstantConditions")
    @Override
    protected void registerModels() {
        withExistingParent(ModBlocks.QUARRY.get().getRegistryName().toString(), new ResourceLocation(Quarry.MOD_ID, "block/quarry_block_off"));
        simpleItem(ModItems.AREA_CARD.get());
    }

    private void simpleItem(Item item) {
        withExistingParent(Objects.requireNonNull(item.getRegistryName()).toString(), "item/generated")
                .texture("layer0", new ResourceLocation(this.modid, "item/" + item.getRegistryName().getPath()));
    }

}
