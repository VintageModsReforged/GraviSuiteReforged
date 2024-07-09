package reforged.mods.gravisuite.items.tools.base;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.IElectricItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import reforged.mods.gravisuite.utils.Helpers;

import java.util.List;

public class ItemBaseElectricItem extends ItemBaseTool implements IElectricItem {

    public int TIER, TRANSFER, CAPACITY;

    public ItemBaseElectricItem(int id, String name, int tier, int transfer, int capacity, EnumToolMaterial material) {
        super(id, name, material, null);
        this.setMaxDamage(27);
        this.TIER = tier;
        this.TRANSFER = transfer;
        this.CAPACITY = capacity;
    }

    @SideOnly(Side.CLIENT)
    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean debugMode) {
        super.addInformation(stack, player, tooltip, debugMode);
        tooltip.add(Helpers.getCharge(stack) + "/" + this.getMaxCharge() + " EU @ Tier " + this.getTier());
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.uncommon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(int item, CreativeTabs tab, List items) {
        Helpers.addChargeVariants(this, items);
    }

    /**
     * {@link IElectricItem} start
     *
     * */

    @Override
    public boolean canProvideEnergy() {
        return false;
    }

    @Override
    public int getChargedItemId() {
        return this.itemID;
    }

    @Override
    public int getEmptyItemId() {
        return this.itemID;
    }

    @Override
    public int getMaxCharge() {
        return this.CAPACITY;
    }

    @Override
    public int getTier() {
        return this.TIER;
    }

    @Override
    public int getTransferLimit() {
        return this.TRANSFER;
    }

    /********************************/
}
