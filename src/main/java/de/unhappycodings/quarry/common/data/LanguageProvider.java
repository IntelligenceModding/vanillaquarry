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
        add("gui.quarry.inventory", "Inventory");
        add("gui.quarry.fuel", "Fuel");
        add("gui.quarry.out", "Out");
        add("gui.quarry.speed", "Speed");
        add("gui.quarry.stop", "stop");
        add("gui.quarry.power.on", "On");
        add("gui.quarry.power.off", "Off");
        add("gui.quarry.mode.default", "Default");
        add("gui.quarry.mode.efficient", "Efficient");
        add("gui.quarry.mode.fortune", "Fortune");
        add("gui.quarry.mode.silktouch", "Silk Touch");
        add("gui.quarry.mode.void", "Void");
        add("gui.quarry.lock.private", "Private");
        add("gui.quarry.lock.public", "Public");
        add("gui.quarry.lock.public.description", "players can use and modify everything");
        add("gui.quarry.lock.private.description", "access only for you");
        add("gui.quarry.lock.owner", "Owner: %s");
        add("gui.quarry.replace", "Block Input / Replacement");
        add("gui.quarry.replace_1", "when blocks mined by the quarry, here");
        add("gui.quarry.replace_2", "inserted blocks will be placed as replacement.");
        add("gui.quarry.replace_3", "Hopper/Pull input via right side! (front view)");

        add("gui.quarry.message.quarry_from", "Quarry of");
        add("gui.quarry.message.is_locked", "is set to private and locked!");

        add("gui.quarry.admin", "Admin Access!");
        add("gui.quarry.others", "This is not your Quarry!");
        add("gui.quarry.replacing", "Quarry will eliminate nearby Fluid Sources!");

        add("gui.quarry.consumption", "Consumption:");
        add("gui.quarry.coal", "1 coal:");
        add("gui.quarry.blocks", "blocks");
        add("gui.quarry.informations", "Informations");
        add("gui.quarry.when_turned_off", "When turned off, the quarry");
        add("gui.quarry.will_consume", "will consume # BurnTick(s) per second.");
        add("gui.quarry.changing_speed", "Changing the speed does also");
        add("gui.quarry.affect_fuel", "affect the fuel consumption!");
        add("gui.quarry.use_config", "values changable in config");
        add("gui.quarry.speed.80", "at 80% speed");

        add("gui.quarry.owner", "Owner:");
        add("gui.quarry.security", "Safety:");
        add("gui.quarry.fueled", "Fueled:");
        add("gui.quarry.yes", "Yes");
        add("gui.quarry.no", "No");

        add("gui.quarry.loop.always", "Always loop");
        add("gui.quarry.loop.restart", "restarts after finished.");
        add("gui.quarry.loop.never", "Don't loop");
        add("gui.quarry.loop.stop", "stop after area is mined.");

        add("gui.quarry.filter.always", "Always Filter");
        add("gui.quarry.filter.filters", "uses the area cards item filter.");
        add("gui.quarry.filter.never", "Don't Filter");
        add("gui.quarry.filter.all", "mines all blocks!");

        add("gui.quarry.skip.always", "Always Skip Air");
        add("gui.quarry.skip.skipped", "Don't try to break blocks as air.");
        add("gui.quarry.skip.never", "Don't Skip Air");
        add("gui.quarry.skip.iterate", "Iterate through all blocks, even air!");

        add("gui.quarry.output.dont", "Don't Eject/Pull");
        add("gui.quarry.output.in_out_hoppers", "output and input possible with hoppers.");
        add("gui.quarry.output.pull", "Only Pull");
        add("gui.quarry.output.pulls_above", "pulls items from above and right side! (front view)");
        add("gui.quarry.output.out_hoppers", "output possible with hoppers.");
        add("gui.quarry.output.eject", "Only Eject");
        add("gui.quarry.output.eject_below", "ejects items to bottom!");
        add("gui.quarry.output.in_hoppers", "input possible with hoppers.");
        add("gui.quarry.output.both", "Eject and Pull");

        add("gui.quarry.darkmode.dark", "Darkmode");
        add("gui.quarry.darkmode.dark.switch", "click to switch to whitemode.");
        add("gui.quarry.darkmode.white", "Whitemode");
        add("gui.quarry.darkmode.white.switch", "click to switch to darkmode.");

        add("item.quarry.areacard.text.box", "Box (Solid)");
        add("item.quarry.areacard.text.mined", "#Mined");
        add("item.quarry.areacard.text.from", "From");
        add("item.quarry.areacard.text.to", "To");
        add("item.quarry.areacard.text.filters_active", "Filters are active!");

        add("item.quarry.areacard.text.around", "Blocks around");
        add("item.quarry.areacard.text.framing", "Framing chunks");
        add("item.quarry.areacard.text.illegal", "Illegal State");

        add("item.quarry.areacard.text.save", "Save");
        add("item.quarry.areacard.text.pos", "Pos");
        add("item.quarry.areacard.text.radius", "Radius");
        add("item.quarry.areacard.text.chunk", "Chunk");
        add("item.quarry.areacard.text.pos_1", "Position 1");
        add("item.quarry.areacard.text.pos_2", "Position 2");
        add("item.quarry.areacard.text.filter", "Filter");

        add("message.quarry.savedfirst", "First position saved! Now select the second corner.");
        add("message.quarry.savedsecond", "New settings copied to the area card!");
    }
}
