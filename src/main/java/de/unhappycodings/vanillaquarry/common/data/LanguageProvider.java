package de.unhappycodings.vanillaquarry.common.data;

import de.unhappycodings.vanillaquarry.VanillaQuarry;
import de.unhappycodings.vanillaquarry.common.blocks.ModBlocks;
import de.unhappycodings.vanillaquarry.common.item.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

public class LanguageProvider extends net.minecraftforge.common.data.LanguageProvider {

    public LanguageProvider(DataGenerator gen, String locale) {
        super(gen, VanillaQuarry.MOD_ID, locale);
    }

    @Override
    protected void addTranslations() {
        add(ModBlocks.QUARRY.get(), "Quarry");
        add(ModItems.AREA_CARD.get(), "Area Card");
        add("message.vanillaquarry.savedfirst", "First position saved! Now select the second corner.");
        add("message.vanillaquarry.savedsecond", "New settings copied to the area card!");
    }
}