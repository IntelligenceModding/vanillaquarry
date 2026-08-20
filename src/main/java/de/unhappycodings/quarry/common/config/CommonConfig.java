package de.unhappycodings.quarry.common.config;

public class CommonConfig {
    public static final ConfigValue<Integer> quarryIdleConsumption = new ConfigValue<>(1);

    public static final ConfigValue<Integer> quarryDefaultModeConsumption = new ConfigValue<>(50);
    public static final ConfigValue<Integer> quarryEfficientModeConsumption = new ConfigValue<>(40);
    public static final ConfigValue<Integer> quarryFortuneModeConsumption = new ConfigValue<>(100);
    public static final ConfigValue<Integer> quarrySilkTouchModeConsumption = new ConfigValue<>(100);
    public static final ConfigValue<Integer> quarryVoidModeConsumption = new ConfigValue<>(50);

    public static final ConfigValue<Double> quarrySpeedOneModifier = new ConfigValue<>(1.0);
    public static final ConfigValue<Double> quarrySpeedTwoModifier = new ConfigValue<>(1.2);
    public static final ConfigValue<Double> quarrySpeedThreeModifier = new ConfigValue<>(1.5);
    public static final ConfigValue<Double> quarrySpeedFourModifier = new ConfigValue<>(1.8);
    public static final ConfigValue<Double> quarrySpeedFiveModifier = new ConfigValue<>(2.3);
    public static final ConfigValue<Double> quarrySpeedSixModifier = new ConfigValue<>(2.9);
    public static final ConfigValue<Double> quarrySpeedSevenModifier = new ConfigValue<>(3.6);

    public static final ConfigValue<Integer> energyQuarryIdleConsumption = new ConfigValue<>(1);

    public static final ConfigValue<Integer> energyQuarryDefaultModeConsumption = new ConfigValue<>(100);
    public static final ConfigValue<Integer> energyQuarryEfficientModeConsumption = new ConfigValue<>(80);
    public static final ConfigValue<Integer> energyQuarryFortuneModeConsumption = new ConfigValue<>(200);
    public static final ConfigValue<Integer> energyQuarrySilkTouchModeConsumption = new ConfigValue<>(200);
    public static final ConfigValue<Integer> energyQuarryVoidModeConsumption = new ConfigValue<>(100);

    public static final ConfigValue<Double> energyQuarrySpeedOneModifier = new ConfigValue<>(1.0);
    public static final ConfigValue<Double> energyQuarrySpeedTwoModifier = new ConfigValue<>(1.2);
    public static final ConfigValue<Double> energyQuarrySpeedThreeModifier = new ConfigValue<>(1.5);
    public static final ConfigValue<Double> energyQuarrySpeedFourModifier = new ConfigValue<>(1.8);
    public static final ConfigValue<Double> energyQuarrySpeedFiveModifier = new ConfigValue<>(2.3);
    public static final ConfigValue<Double> energyQuarrySpeedSixModifier = new ConfigValue<>(2.9);
    public static final ConfigValue<Double> energyQuarrySpeedSevenModifier = new ConfigValue<>(3.6);

    public static final ConfigValue<Integer> energyQuarryEnergyCapacity = new ConfigValue<>(100000);
    public static final ConfigValue<Integer> energyQuarryMaxReceive = new ConfigValue<>(10000);
    public static final ConfigValue<Integer> quarryMineRadius = new ConfigValue<>(64);
}
