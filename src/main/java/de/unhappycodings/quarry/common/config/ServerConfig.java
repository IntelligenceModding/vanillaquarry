package de.unhappycodings.quarry.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {
    public static ModConfigSpec serverConfig;

    public static ModConfigSpec.ConfigValue<Boolean> enableFEQuarry;
    public static ModConfigSpec.ConfigValue<Integer> feQuarryEnergyCapacity;
    public static ModConfigSpec.ConfigValue<Integer> feQuarryMaxReceive;

    static {
        ModConfigSpec.Builder serverBuilder = new ModConfigSpec.Builder();

        init(serverBuilder);
        serverConfig = serverBuilder.build();
    }

    private static void init(ModConfigSpec.Builder serverBuilder) {
        serverBuilder.push("FE Quarry");
        enableFEQuarry = serverBuilder.comment("Set to false to disable the FE Quarry block. Disabled FE Quarries will not run or open.").define("enable_fe_quarry", false);
        feQuarryEnergyCapacity = serverBuilder.comment("Internal FE buffer of the FE Quarry").define("fe_quarry_energy_capacity", 100000);
        feQuarryMaxReceive = serverBuilder.comment("Maximum FE accepted per insertion").define("fe_quarry_max_receive", 10000);
        serverBuilder.pop();
    }
}
