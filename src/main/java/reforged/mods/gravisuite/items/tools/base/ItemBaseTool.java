package reforged.mods.gravisuite.items.tools.base;

import net.minecraft.block.Block;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemTool;
import org.jetbrains.annotations.Nullable;
import reforged.mods.gravisuite.GraviSuite;

public class ItemBaseTool extends ItemTool {

    public Integer meta;

    protected ItemBaseTool(int id, String name, EnumToolMaterial toolMaterial, @Nullable Integer meta) {
        super(id, 0, toolMaterial, new Block[0]);
        this.setItemName(name);
        this.setMaxStackSize(1);
        this.setNoRepair();
        this.setCreativeTab(GraviSuite.TAB);
        this.meta = meta;
    }

    @Override
    public String getTextureFile() {
        return GraviSuite.TEXTURE;
    }
}
