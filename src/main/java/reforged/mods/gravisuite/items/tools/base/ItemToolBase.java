package reforged.mods.gravisuite.items.tools.base;

import net.minecraft.block.Block;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemTool;
import reforged.mods.gravisuite.GraviSuite;

public class ItemToolBase extends ItemTool {

    protected ItemToolBase(int id, String name, EnumToolMaterial material) {
        super(id, 0, material, new Block[0]);
        this.setUnlocalizedName(name);
        this.setMaxStackSize(1);
        this.setNoRepair();
        this.setCreativeTab(GraviSuite.graviTab);
    }
}
