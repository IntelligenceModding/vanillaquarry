package de.unhappycodings.quarry.common.data;

import de.unhappycodings.quarry.Quarry;
import net.minecraft.data.DataGenerator;

public class LanguageProvider extends net.neoforged.neoforge.common.data.LanguageProvider {

    public LanguageProvider(DataGenerator gen, String locale) {
        super(gen.getPackOutput(), Quarry.MOD_ID, locale);
    }

    @Override
    protected void addTranslations() {
        add(Quarry.QUARRY_BLOCK.get(), "Quarry");
        add(Quarry.AREA_CARD.get(), "Area Card");

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
        add("gui.quarry.replacing", "Quarry will eliminate nearby fluid sources!");

        add("gui.quarry.consumption", "Consumption:");
        add("gui.quarry.coal", "1 coal:");
        add("gui.quarry.blocks", "blocks");
        add("gui.quarry.informations", "Informations");
        add("gui.quarry.maxrange", "Max Range is %s blocks cubic.");
        add("gui.quarry.when_turned_off", "When turned off, the quarry");
        add("gui.quarry.will_consume", "will consume %s BurnTick(s) per second.");
        add("gui.quarry.changing_speed", "Changing the speed or mode does also");
        add("gui.quarry.affect_fuel", "affect the fuel consumption!");
        add("gui.quarry.use_config", "values changable in config");
        add("gui.quarry.speed.80", "at 80% speed");

        add("gui.quarry.owner", "Owner:");
        add("gui.quarry.security", "Safety:");
        add("gui.quarry.fueled", "Fueled:");
        add("gui.quarry.yes", "Yes");
        add("gui.quarry.no", "No");

        add("gui.quarry.holo.quarry", "QUARRY");
        add("gui.quarry.holo.security", "Security");
        add("gui.quarry.holo.loop", "Loop");
        add("gui.quarry.holo.filter", "Filter");
        add("gui.quarry.holo.inout", "In/Out");
        add("gui.quarry.holo.skip", "Skip Air");
        add("gui.quarry.holo.replace", "Replace Liquid");
        add("gui.quarry.holo.estimate", "Estimate");
        add("gui.quarry.holo.fuel", "Fuel:");
        add("gui.quarry.holo.work", "Work:");
        add("gui.quarry.holo.stop", "Stop");
        add("gui.quarry.holo.mining", "Mining");
        add("gui.quarry.holo.outofrange", "Out of Range");
        add("gui.quarry.holo.invfull", "Inv. full");

        add("gui.quarry.loop.always", "Always Loop");
        add("gui.quarry.loop.restart", "restarts after finished.");
        add("gui.quarry.loop.never", "Don't Loop");
        add("gui.quarry.loop.stop", "stop after area is mined.");

        add("gui.quarry.filter.always", "Always Filter");
        add("gui.quarry.filter.filters", "uses the area cards item filter.");
        add("gui.quarry.filter.never", "Don't Filter");
        add("gui.quarry.filter.all", "mines all blocks!");

        add("gui.quarry.skip.always", "Always Skip Air");
        add("gui.quarry.skip.skipped", "Don't try to break blocks as air.");
        add("gui.quarry.skip.skips", "Skips 32 air per operation max.");
        add("gui.quarry.skip.never", "Don't Skip Air");
        add("gui.quarry.skip.iterate", "Iterate through all blocks, even air!");

        add("gui.quarry.replace.always", "Always Replace Liquids");
        add("gui.quarry.replace.always.description", "Replaces nearby fluid sources with cobblestone.");
        add("gui.quarry.replace.never", "Don't Replace Liquids");
        add("gui.quarry.replace.never.description", "Ignores nearby fluid sources.");

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

        add("gui.areacard.box", "Box (Solid)");
        add("gui.areacard.mined", "#Mined");
        add("gui.areacard.from", "From");
        add("gui.areacard.to", "To");
        add("gui.areacard.filters_active", "%s filter(s) set!");
        add("gui.areacard.filters_enable", "Don't forget to enable filters in the quarry!");

        add("gui.areacard.around", "Blocks around");
        add("gui.areacard.framing", "Framing chunks");
        add("gui.areacard.illegal", "Illegal State");

        add("gui.areacard.save", "Save");
        add("gui.areacard.pos", "Pos");
        add("gui.areacard.radius", "Radius");
        add("gui.areacard.chunk", "Chunk");
        add("gui.areacard.pos_1", "Position 1");
        add("gui.areacard.pos_2", "Position 2");
        add("gui.areacard.filter", "Filter");

        add("gui.areacard.selection.filter", "Filter: ");
        add("gui.areacard.selection.filter.set", "Click with item in mouse to set");
        add("gui.areacard.selection.filter.unset", "Filter: Not Set");
        add("gui.areacard.selection.filter.reset", "Click with bare hand to remove filter");

        add("gui.areacard.selection.position", "Position");
        add("gui.areacard.selection.position.description", "Set both positions to your desire");

        add("gui.areacard.selection.radius", "Radius");
        add("gui.areacard.selection.radius.description", "Square radius around your current position");

        add("gui.areacard.selection.chunk", "Chunk");
        add("gui.areacard.selection.chunk.description", "Square chunk radius around your current chunk");

        add("gui.areacard.selection.filter.description", "Set filters to get rid of useless drops");

        add("message.quarry.savedfirst", "First position saved! Now select the second corner.");
        add("message.quarry.savedsecond", "New settings copied to the area card!");
    }
}
