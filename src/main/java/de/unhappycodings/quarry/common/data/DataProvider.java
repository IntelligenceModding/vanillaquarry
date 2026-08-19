package de.unhappycodings.quarry.common.data;

import de.unhappycodings.quarry.Quarry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Quarry.MOD_ID)
public class DataProvider {

    @SubscribeEvent
    public static void onClientDataGen(GatherDataEvent.Client event) {
        event.addProvider(new LanguageProvider(event.getGenerator(), "en_us"));
        event.addProvider(new GermanLanguageProvider(event.getGenerator(), "de_de"));
        event.addProvider(new RecipeProvider.Runner(event.getGenerator().getPackOutput(), event.getLookupProvider()));
    }

    @SubscribeEvent
    public static void onServerDataGen(GatherDataEvent.Server event) {
        event.addProvider(new RecipeProvider.Runner(event.getGenerator().getPackOutput(), event.getLookupProvider()));
    }
}
