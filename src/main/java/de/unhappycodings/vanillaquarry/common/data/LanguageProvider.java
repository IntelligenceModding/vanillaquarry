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

        add("gui.vanillaquarry.quarry.tooltip.consumption.100", "Consumption: 100 ticks");
        add("gui.vanillaquarry.quarry.tooltip.amount.16", "1 coal: 16 blocks");
        add("gui.vanillaquarry.quarry.tooltip.consumption.200", "Consumption: 200 ticks");
        add("gui.vanillaquarry.quarry.tooltip.amount.8", "1 coal: 8 blocks");
        add("gui.vanillaquarry.quarry.tooltip.consumption.80", "Consumption: 80 ticks");
        add("gui.vanillaquarry.quarry.tooltip.amount.20", "1 coal: 20 blocks");

        add("gui.vanillaquarry.quarry.tooltip.speed.80", "at 80% speed");

        add("item.vanillaquarry.areacard.text.box", "Box (Solid)");
        add("item.vanillaquarry.areacard.text.mined", "#Mined");
        add("item.vanillaquarry.areacard.text.from", "From");
        add("item.vanillaquarry.areacard.text.to", "To");

        add("message.vanillaquarry.savedfirst", "First position saved! Now select the second corner.");
        add("message.vanillaquarry.savedsecond", "New settings copied to the area card!");
    }
}