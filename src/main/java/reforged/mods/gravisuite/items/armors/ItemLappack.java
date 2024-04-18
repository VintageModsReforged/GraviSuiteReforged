package reforged.mods.gravisuite.items.armors;

import reforged.mods.gravisuite.GraviSuiteMainConfig;
import reforged.mods.gravisuite.items.armors.base.ItemBaseEnergyPack;
import net.minecraft.item.EnumRarity;

public class ItemLappack {

    public static class ItemAdvancedLappack extends ItemBaseEnergyPack {

        public ItemAdvancedLappack() {
            super(GraviSuiteMainConfig.ADVANCED_LAPPACK_ID,0, "advanced_lappack", EnumRarity.uncommon, 2, 1000, 1000000);
        }
    }

    public static class ItemUltimateLappack extends ItemBaseEnergyPack {

        public ItemUltimateLappack() {
            super(GraviSuiteMainConfig.ULTIMATE_LAPPACK_ID, 1, "ultimate_lappack", EnumRarity.rare, 3, 20000, 10000000);
        }
    }
}
