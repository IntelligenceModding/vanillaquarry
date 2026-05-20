package de.unhappycodings.quarry.common.data;

import de.unhappycodings.quarry.Quarry;
import net.minecraft.data.DataGenerator;

public class GermanLanguageProvider extends net.neoforged.neoforge.common.data.LanguageProvider {

    public GermanLanguageProvider(DataGenerator gen, String locale) {
        super(gen.getPackOutput(), Quarry.MOD_ID, locale);
    }

    @Override
    protected void addTranslations() {
        add(Quarry.QUARRY_BLOCK.get(), "Quarry");
        add(Quarry.AREA_CARD.get(), "Auswahlkarte");

        add("itemGroup.quarry.items", "Quarry");
        add("gui.quarry.inventory", "Inventar");
        add("gui.quarry.fuel", "Brennst.");
        add("gui.quarry.out", "Ausgabe");
        add("gui.quarry.speed", "Geschwindigk.");
        add("gui.quarry.stop", "stop");
        add("gui.quarry.power.on", "An");
        add("gui.quarry.power.off", "Aus");
        add("gui.quarry.mode.default", "Standard");
        add("gui.quarry.mode.efficient", "Effizient");
        add("gui.quarry.mode.fortune", "Glück");
        add("gui.quarry.mode.silktouch", "Behutsamk.");
        add("gui.quarry.mode.void", "Löschen");
        add("gui.quarry.lock.private", "Privat");
        add("gui.quarry.lock.public", "Öffentlich");
        add("gui.quarry.lock.public.description", "Spieler können alles nutzen und konfigurieren");
        add("gui.quarry.lock.private.description", "Zugriff nur für dich");
        add("gui.quarry.lock.owner", "Besitzer: %s");
        add("gui.quarry.replace", "Eingabe Ersatzblock");
        add("gui.quarry.replace_1", "hier eingefügte Blöcke werden beim");
        add("gui.quarry.replace_2", "abbau eines Blocks als Ersatz platziert.");
        add("gui.quarry.replace_3", "Trichter/Pull Eingang via rechte seite! (Frontalansicht)");

        add("gui.quarry.message.quarry_from", "Quarry von");
        add("gui.quarry.message.is_locked", "ist auf privat gesetzt und gesperrt!");

        add("gui.quarry.admin", "Admin Zugriff!");
        add("gui.quarry.others", "Dies ist nicht deine Quarry!");
        add("gui.quarry.replacing", "Quarry will eliminate nearby fluid sources!");

        add("gui.quarry.consumption", "Verbrauch:");
        add("gui.quarry.coal", "1 Kohle:");
        add("gui.quarry.blocks", "Blöcke");
        add("gui.quarry.informations", "Informations");
        add("gui.quarry.maxrange", "Maximale Reichweite ist %s Blöcke. (Kubik)");
        add("gui.quarry.when_turned_off", "Im ausgeschalteten Zustand verbraucht");
        add("gui.quarry.will_consume", "die Quarry %s BurnTick(s) pro Sekunde.");
        add("gui.quarry.changing_speed", "Ändern der Geschwindigkeit beeinflusst");
        add("gui.quarry.affect_fuel", "auch den Brennstoffverbrauch!");
        add("gui.quarry.use_config", "Werte änderbar in der Config");
        add("gui.quarry.speed.80", "bei 80% Geschw.");
        add("gui.quarry.toggleholo", "klicke um die Hologramanzeige ein- oder auszuschalten.");

        add("gui.quarry.owner", "Besitzer:");
        add("gui.quarry.security", "Sicherheit:");
        add("gui.quarry.fueled", "Hat Brennstoff:");
        add("gui.quarry.yes", "Ja");
        add("gui.quarry.no", "Nein");

        add("gui.quarry.holo.quarry", "QUARRY");
        add("gui.quarry.holo.security", "Sicherheit");
        add("gui.quarry.holo.loop", "Neustarten");
        add("gui.quarry.holo.filter", "Filter");
        add("gui.quarry.holo.inout", "Ein-/Ausg.");
        add("gui.quarry.holo.skip", "Überspr. Luft");
        add("gui.quarry.holo.replace", "Erset. Flüssigkeiten");
        add("gui.quarry.holo.estimate", "Geschätze Zeit");
        add("gui.quarry.holo.fuel", "Brennstoff:");
        add("gui.quarry.holo.work", "Arbeit:");
        add("gui.quarry.holo.stop", "Stop");
        add("gui.quarry.holo.mining", "Abbauen");
        add("gui.quarry.holo.outofrange", "Außer Reichweite");
        add("gui.quarry.holo.invfull", "Inv. voll");
        add("gui.quarry.holo.skipping", "Übers. Luft");

        add("gui.quarry.loop.always", "Immer neustarten");
        add("gui.quarry.loop.restart", "startet nach fertigstellung erneut.");
        add("gui.quarry.loop.never", "Nicht neustarten");
        add("gui.quarry.loop.stop", "stoppt nach fertigstellung.");

        add("gui.quarry.filter.always", "Immer Filtern");
        add("gui.quarry.filter.filters", "nutzt den Filter der Auswahlkarte.");
        add("gui.quarry.filter.never", "Nicht Filtern");
        add("gui.quarry.filter.all", "baut alle Blöcke ab!");

        add("gui.quarry.skip.always", "Immer Luft überspringen");
        add("gui.quarry.skip.skipped", "Blöcke wie Luft werden nicht abgebaut sondern übersprungen.");
        add("gui.quarry.skip.skips", "Überspringt 32 Blöcke pro Operation maximal.");
        add("gui.quarry.skip.never", "Keine Luft überspringen");
        add("gui.quarry.skip.iterate", "Gehe alle Blöcke durch, auch Luft!");

        add("gui.quarry.replace.always", "Immer Flüssigkeiten ersetzen");
        add("gui.quarry.replace.always.description", "Ersetzt umgebende Flüssigkeiten mit Bruchstein.");
        add("gui.quarry.replace.never", "Keine Flüssigkeiten ersetzen");
        add("gui.quarry.replace.never.description", "Ignoriert umgebende Flüssigkeiten.");

        add("gui.quarry.output.dont", "Nicht Ausgeben oder Einziehen");
        add("gui.quarry.output.in_out_hoppers", "Ein- und Ausgabe möglich per Trichter.");
        add("gui.quarry.output.pull", "Nur Einziehen");
        add("gui.quarry.output.pulls_above", "Zieht Gegenstände von der oberen und rechten Seite hinein! (Frontalansicht)");
        add("gui.quarry.output.out_hoppers", "Ausgabe weiterhin möglich per Trichter.");
        add("gui.quarry.output.eject", "Nur Auswerfen");
        add("gui.quarry.output.eject_below", "Gibt Gegenstände in untengelegene Kisten!");
        add("gui.quarry.output.in_hoppers", "Eingabe weiterhin möglich per Trichter.");
        add("gui.quarry.output.both", "Ausgeben und Einziehen");

        add("gui.quarry.darkmode.dark", "Dunkler Modus");
        add("gui.quarry.darkmode.dark.switch", "klicke um zum hellen Modus zu wechseln.");
        add("gui.quarry.darkmode.white", "Heller Modus");
        add("gui.quarry.darkmode.white.switch", "klicke um zum dunklen Modus zu wechseln.");

        add("gui.quarry.reset", "Setze abgebaute Blöcke zurück");
        add("gui.quarry.reset.switch", "klicke um den Abbauzähler auf 0 zu setzen.");
        add("gui.quarry.reset.desc", "Dies bewirkt, dass die Quarry");
        add("gui.quarry.reset.desc_1", "den Bereich von neu abbaut.");

        add("gui.areacard.box", "Box (Voll)");
        add("gui.areacard.mined", "#Abgebaut");
        add("gui.areacard.from", "Von");
        add("gui.areacard.to", "Zu");
        add("gui.areacard.filters_active", "%s Filter sind aktiv!");
        add("gui.areacard.filters_enable", "Vergiss nicht die Filter in der Quarry zu aktivieren!");

        add("gui.areacard.around", "Blöcke um einen rum");
        add("gui.areacard.framing", "Umliegende Chunks");
        add("gui.areacard.illegal", "Illeg. Zustand");

        add("gui.areacard.save", "Speichern");
        add("gui.areacard.pos", "Pos");
        add("gui.areacard.radius", "Radius");
        add("gui.areacard.chunk", "Chunk");
        add("gui.areacard.pos_1", "Position 1");
        add("gui.areacard.pos_2", "Position 2");
        add("gui.areacard.filter", "Filter");

        add("gui.areacard.selection.filter", "Filter: ");
        add("gui.areacard.selection.filter.set", "Klicke mit Gegenstand in der Hand zum setzen");
        add("gui.areacard.selection.filter.unset", "Filter: Nicht gesetzt");
        add("gui.areacard.selection.filter.reset", "Klicke mit der bloßen Hand zum zurücksetzen");

        add("gui.areacard.selection.position", "Position");
        add("gui.areacard.selection.position.description", "Setze beide Positionen zu deinem Wunsch");

        add("gui.areacard.selection.radius", "Radius");
        add("gui.areacard.selection.radius.description", "Quadratischer Radius um deine aktuelle Position");

        add("gui.areacard.selection.chunk", "Chunk");
        add("gui.areacard.selection.chunk.description", "Quadratischer Radius in Chunks um deinen aktuellen Chunk");

        add("gui.areacard.selection.filter.description", "Setzte Filter um bestimmte Gegenstände loszuwerden");

        add("message.quarry.savedfirst", "Erste Position gespeichert! Wähle nun die zweite aus.");
        add("message.quarry.savedsecond", "Neue Einstellungen wurden in der Auswahlkarte gespeichert!");
    }
}
