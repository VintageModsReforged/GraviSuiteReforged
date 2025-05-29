package reforged.mods.gravisuite.utils;

public enum EnergyValues {
    MAGNET(1, 500, 10000),
    GRAVITOOL(2, 10000, 100000),
    RELOCATOR(3, 20000, 10000000),
    DIAMOND_DRILL(2, 500, 15000),
    IRIDIUM_DRILL(3, 1000, 100000),
    CHAINSAW(2, 500, 15000),
    VAJRA(2, 10000, 1000000),
    ADV_LAPPACK(2, 1000, 1000000),
    ULT_LAPPACK(2, 20000, 10000000),
    ADV_JETPACK(2, 1000, 1000000),
    ADV_NANO(2, 1000, 1000000),
    ADV_QUANT(2, 20000, 10000000);

    public int tier;
    public int transfer;
    public int maxCapacity;

    EnergyValues(int tier, int transfer, int maxCapacity) {
        this.tier = tier;
        this.transfer = transfer;
        this.maxCapacity = maxCapacity;
    }
}
