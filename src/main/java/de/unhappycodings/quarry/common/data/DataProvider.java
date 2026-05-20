package de.unhappycodings.quarry.common.data;

import de.unhappycodings.quarry.Quarry;
import net.minecraft.data.DataGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Quarry.MOD_ID)
public class DataProvider {

    @SubscribeEvent
    public static void onDataGen(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        generator.addProvider(true, new LanguageProvider(generator, "en_us"));
        generator.addProvider(true, new GermanLanguageProvider(generator, "de_de"));
        generator.addProvider(true, new RecipeProvider(generator.getPackOutput(), event.getLookupProvider()));
    }
}
