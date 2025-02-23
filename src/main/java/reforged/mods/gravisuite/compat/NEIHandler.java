package reforged.mods.gravisuite.compat;

import cpw.mods.fml.common.Loader;
import mods.vintage.core.helpers.nei.NEIHelper;
import reforged.mods.gravisuite.GraviSuiteData;

public class NEIHandler {

    public static void init() {
        if (Loader.isModLoaded("NotEnoughItems")) {
            NEIHelper.addCategory("IC2.Addons.GraviSuite",
                    GraviSuiteData.superconductor_cover.itemID,
                    GraviSuiteData.superconductor.itemID,
                    GraviSuiteData.cooling_core.itemID,
                    GraviSuiteData.gravi_engine.itemID,
                    GraviSuiteData.magnetron.itemID,
                    GraviSuiteData.vajra_core.itemID,
                    GraviSuiteData.engine_booster.itemID,
                    GraviSuiteData.advanced_diamond_drill.itemID,
                    GraviSuiteData.advanced_iridium_drill.itemID,
                    GraviSuiteData.advanced_chainsaw.itemID,
                    GraviSuiteData.vajra.itemID,
                    GraviSuiteData.magnet.itemID,
                    GraviSuiteData.gravitool.itemID,
                    GraviSuiteData.voider.itemID,
                    GraviSuiteData.advanced_lappack.itemID,
                    GraviSuiteData.ultimate_lappack.itemID,
                    GraviSuiteData.advanced_jetpack.itemID,
                    GraviSuiteData.advanced_nano.itemID,
                    GraviSuiteData.advanced_quant.itemID
            );
        }
    }
}
