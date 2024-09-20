package reforged.mods.gravisuite.items.armors.base;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.IElectricItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.GraviSuiteData;
import reforged.mods.gravisuite.items.armors.IHasOverlay;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;
import reforged.mods.gravisuite.utils.TextFormatter;

import java.util.List;

public class ItemBaseEnergyPack extends ItemArmor implements IElectricItem, IHasOverlay {

    public int TIER, TRANSFER, CAPACITY, META;
    public String NAME;

    public ItemBaseEnergyPack(int id, int meta, String name, int tier, int transfer, int capacity) {
        super(id, GraviSuiteData.GRAVI_MATERIAL, GraviSuite.PROXY.addArmor("gravisuite/" + name), 1);
        this.setItemName(name);
        this.setCreativeTab(GraviSuite.TAB);
        this.setMaxDamage(27);
        this.iconIndex = Refs.ARMOR_PACK_ID + meta;
        this.NAME = name;
        this.TIER = tier;
        this.TRANSFER = transfer;
        this.CAPACITY = capacity;
        this.META = meta;
    }

    @SideOnly(Side.CLIENT)
    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean debugMode) {
        super.addInformation(stack, player, tooltip, debugMode);
        tooltip.add(TextFormatter.AQUA.literal(Helpers.getCharge(stack) + "/" + this.getMaxCharge() + " EU @ Tier " + this.getTier()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack par1ItemStack) {
        return EnumRarity.uncommon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(int id, CreativeTabs tab, List items) {
        Helpers.addChargeVariants(this, items);
    }

    @Override
    public String getTextureFile() {
        return GraviSuite.TEXTURE;
    }

    /**
     * {@link IElectricItem} start
     *
     * */

    @Override
    public boolean canProvideEnergy() {
        return true;
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
