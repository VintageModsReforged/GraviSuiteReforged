package reforged.mods.gravisuite;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import reforged.mods.gravisuite.utils.Refs;

public class GraviSuiteTab extends CreativeTabs {

    public GraviSuiteTab() {
        super(Refs.id);
        LanguageRegistry.instance().addStringLocalization("itemGroup." + Refs.id, Refs.name);
    }

    @Override
    public Item getTabIconItem() {
        return GraviSuiteData.advanced_quant;
    }
}
