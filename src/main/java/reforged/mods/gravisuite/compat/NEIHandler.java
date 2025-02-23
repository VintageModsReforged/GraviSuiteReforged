package reforged.mods.gravisuite.compat;

import cpw.mods.fml.common.Loader;
import mods.vintage.core.helpers.nei.NEIHelper;
import reforged.mods.gravisuite.GraviSuiteData;

public class NEIHandler {

    public static void init() {
        if (Loader.isModLoaded("NotEnoughItems")) {
            NEIHelper.addCategory("IC2.Addons.GraviSuite",
                    GraviSuiteData.SUPERCONDUCTOR_COVER.itemID,
                    GraviSuiteData.SUPERCONDUCTOR.itemID,
                    GraviSuiteData.COOLING_CORE.itemID,
                    GraviSuiteData.GRAVI_ENGINE.itemID,
                    GraviSuiteData.MAGNETRON.itemID,
                    GraviSuiteData.VAJRA_CORE.itemID,
                    GraviSuiteData.ENGINE_BOOSTER.itemID,
                    GraviSuiteData.ADVANCED_DRILL.itemID,
                    GraviSuiteData.ADVANCED_CHAINSAW.itemID,
                    GraviSuiteData.VAJRA.itemID,
                    GraviSuiteData.MAGNET.itemID,
                    GraviSuiteData.GRAVI_TOOL.itemID,
                    GraviSuiteData.ADVANCED_LAPPACK.itemID,
                    GraviSuiteData.ULTIMATE_LAPPACK.itemID,
                    GraviSuiteData.ADVANCED_JETPACK.itemID,
                    GraviSuiteData.ADVANCED_NANO.itemID,
                    GraviSuiteData.ADVANCED_QUANT.itemID
            );
        }
    }
}
