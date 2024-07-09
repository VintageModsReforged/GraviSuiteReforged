package reforged.mods.gravisuite;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import reforged.mods.gravisuite.utils.Refs;

public class GraviSuiteTab extends CreativeTabs {

    public GraviSuiteTab() {
        super(Refs.ID);
        LanguageRegistry.instance().addStringLocalization("itemGroup." + Refs.ID, Refs.NAME);
    }

    @Override
    public Item getTabIconItem() {
        return GraviSuiteData.ADVANCED_QUANT;
    }
}
