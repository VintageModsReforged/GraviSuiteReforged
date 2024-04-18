package reforged.mods.gravisuite.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.GraviSuiteConfig;
import reforged.mods.gravisuite.utils.Refs;

import java.util.List;

public class ItemComponent extends Item {

    public String[] names = new String[] { "superconductor_cover", "superconductor", "cooling_core", "gravi_engine", "magnetron", "vajra_core", "engine_booster" };
    public Icon[] icons = new Icon[7];

    public ItemComponent() {
        super(GraviSuiteConfig.COMPONENT_ID);
        this.setUnlocalizedName("component");
        this.setCreativeTab(GraviSuite.graviTab);
        this.setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + names[stack.getItemDamage()];
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubItems(int id, CreativeTabs tab, List items) {
        for (int i = 0; i < names.length; i++) {
            items.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public void registerIcons(IconRegister icon) {
        this.icons[0] = icon.registerIcon(Refs.id + ":components/superconductor_cover");
        this.icons[1] = icon.registerIcon(Refs.id + ":components/superconductor");
        this.icons[2] = icon.registerIcon(Refs.id + ":components/cooling_core");
        this.icons[3] = icon.registerIcon(Refs.id + ":components/gravi_engine");
        this.icons[4] = icon.registerIcon(Refs.id + ":components/magnetron");
        this.icons[5] = icon.registerIcon(Refs.id + ":components/vajra_core");
        this.icons[6] = icon.registerIcon(Refs.id + ":components/engine_booster");
    }

    @Override
    public Icon getIconFromDamage(int meta) {
        return this.icons[meta];
    }
}
