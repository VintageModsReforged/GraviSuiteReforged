package reforged.mods.gravisuite.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.utils.Refs;

import java.util.List;

public class ItemComponent extends Item {

    public String name;
    public ItemComponent(int id, String name) {
        super(id);
        this.name = name;
        this.setUnlocalizedName(name);
        this.setCreativeTab(GraviSuite.graviTab);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubItems(int id, CreativeTabs tab, List items) {
        items.add(new ItemStack(this));
    }

    @Override
    public void registerIcons(IconRegister icon) {
        this.itemIcon = icon.registerIcon(Refs.id + ":components/" + this.name);
    }
}
