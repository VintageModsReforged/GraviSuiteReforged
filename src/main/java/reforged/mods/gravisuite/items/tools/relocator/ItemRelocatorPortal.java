package reforged.mods.gravisuite.items.tools.relocator;

import net.minecraft.item.ItemBlock;

public class ItemRelocatorPortal extends ItemBlock {

    public ItemRelocatorPortal(int id) {
        super(id);
        this.setMaxDamage(0);
        this.setHasSubtypes(false);
        this.setItemName("block.relocator_portal");
    }
}
