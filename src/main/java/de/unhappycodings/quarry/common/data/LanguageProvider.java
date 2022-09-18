package de.unhappycodings.quarry.common.data;

import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.common.blocks.ModBlocks;
import de.unhappycodings.quarry.common.item.ModItems;
import net.minecraft.data.DataGenerator;

public class LanguageProvider extends net.minecraftforge.common.data.LanguageProvider {

    public LanguageProvider(DataGenerator gen, String locale) {
        super(gen, Quarry.MOD_ID, locale);
    }

    @Override
    protected void addTranslations() {
        add(ModBlocks.QUARRY.get(), "Quarry");
        add(ModItems.AREA_CARD.get(), "Area Card");

        add("itemGroup.quarry.items", "Quarry");

        add("gui.quarry.quarry.text.inventory", "Inventory");
        add("gui.quarry.quarry.text.fuel", "Fuel");
        add("gui.quarry.quarry.text.out", "Out");
        add("gui.quarry.quarry.text.speed", "Speed");
        add("gui.quarry.quarry.text.stop", "stop");
        add("gui.quarry.quarry.power.on", "On");
        add("gui.quarry.quarry.power.off", "Off");
        add("gui.quarry.quarry.mode.default", "Default");
        add("gui.quarry.quarry.mode.efficient", "Efficient");
        add("gui.quarry.quarry.mode.fortune", "Fortune");
        add("gui.quarry.quarry.mode.silktouch", "Silk Touch");
        add("gui.quarry.quarry.mode.void", "Void");
        add("gui.quarry.quarry.lock.private", "Private");
        add("gui.quarry.quarry.lock.public", "Public");
        add("gui.quarry.quarry.lock.private.description", "players can use and modify everything");
        add("gui.quarry.quarry.lock.public.description", "access only for you");
        add("gui.quarry.quarry.lock.owner", "Owner: %s");

        add("gui.quarry.quarry.message.quarry_from", "Quarry of");
        add("gui.quarry.quarry.message.is_locked", "is set to private and locked!");

        add("gui.quarry.quarry.tooltip.consumption", "Consumption:");
        add("gui.quarry.quarry.tooltip.coal", "1 coal:");
        add("gui.quarry.quarry.tooltip.blocks", "blocks");
        add("gui.quarry.quarry.tooltip.informations", "Informations");
        add("gui.quarry.quarry.tooltip.when_turned_off", "When turned off, the quarry");
        add("gui.quarry.quarry.tooltip.will_consume", "will consume # BurnTick(s) per second.");
        add("gui.quarry.quarry.tooltip.changing_speed", "Changing the speed does also");
        add("gui.quarry.quarry.tooltip.affect_fuel", "affect the fuel consumption!");
        add("gui.quarry.quarry.tooltip.use_config", "Change values in the config.");
        add("gui.quarry.quarry.tooltip.speed.80", "at 80% speed");

        add("item.quarry.areacard.text.box", "Box (Solid)");
        add("item.quarry.areacard.text.mined", "#Mined");
        add("item.quarry.areacard.text.from", "From");
        add("item.quarry.areacard.text.to", "To");

        add("message.quarry.savedfirst", "First position saved! Now select the second corner.");
        add("message.quarry.savedsecond", "New settings copied to the area card!");
    }
}