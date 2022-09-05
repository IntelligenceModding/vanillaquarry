package de.unhappycodings.vanillaquarry.common.data;

import de.unhappycodings.vanillaquarry.VanillaQuarry;
import de.unhappycodings.vanillaquarry.common.blocks.ModBlocks;
import de.unhappycodings.vanillaquarry.common.item.ModItems;
import net.minecraft.data.DataGenerator;

public class LanguageProvider extends net.minecraftforge.common.data.LanguageProvider {

    public LanguageProvider(DataGenerator gen, String locale) {
        super(gen, VanillaQuarry.MOD_ID, locale);
    }

    @Override
    protected void addTranslations() {
        add(ModBlocks.QUARRY.get(), "Quarry");
        add(ModItems.AREA_CARD.get(), "Area Card");

        add("itemGroup.vanillaquarry.items", "Vanilla Quarry");

        add("gui.vanillaquarry.quarry.text.inventory", "Inventory");
        add("gui.vanillaquarry.quarry.text.fuel", "Fuel");
        add("gui.vanillaquarry.quarry.text.out", "Out");
        add("gui.vanillaquarry.quarry.text.speed", "Speed");
        add("gui.vanillaquarry.quarry.power.on", "On");
        add("gui.vanillaquarry.quarry.power.off", "Off");
        add("gui.vanillaquarry.quarry.mode.default", "Default");
        add("gui.vanillaquarry.quarry.mode.efficient", "Efficient");
        add("gui.vanillaquarry.quarry.mode.fortune", "Fortune");
        add("gui.vanillaquarry.quarry.mode.silktouch", "Silk Touch");
        add("gui.vanillaquarry.quarry.mode.void", "Void");

        add("gui.vanillaquarry.quarry.tooltip.consumption", "Consumption:");
        add("gui.vanillaquarry.quarry.tooltip.coal", "1 coal:");
        add("gui.vanillaquarry.quarry.tooltip.blocks", "blocks");

        add("gui.vanillaquarry.quarry.tooltip.informations", "Informations");
        add("gui.vanillaquarry.quarry.tooltip.when_turned_off", "When turned off, the quarry");
        add("gui.vanillaquarry.quarry.tooltip.will_consume", "will consume 1 BurnTick per second.");
        add("gui.vanillaquarry.quarry.tooltip.changing_speed", "Changing the speed does also");
        add("gui.vanillaquarry.quarry.tooltip.affect_fuel", "affect the fuel consumption!");

        add("gui.vanillaquarry.quarry.tooltip.speed.80", "at 80% speed");

        add("item.vanillaquarry.areacard.text.box", "Box (Solid)");
        add("item.vanillaquarry.areacard.text.mined", "#Mined");
        add("item.vanillaquarry.areacard.text.from", "From");
        add("item.vanillaquarry.areacard.text.to", "To");

        add("message.vanillaquarry.savedfirst", "First position saved! Now select the second corner.");
        add("message.vanillaquarry.savedsecond", "New settings copied to the area card!");
    }
}