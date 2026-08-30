package reforged.mods.gravisuite.items.armors.base;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.ElectricItem;
import ic2.api.IElectricItem;
import ic2.api.IMetalArmor;
import mods.vintage.core.helpers.ElectricHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.IArmorTextureProvider;
import net.minecraftforge.common.ISpecialArmor;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.GraviSuiteData;
import reforged.mods.gravisuite.utils.Messages;
import reforged.mods.gravisuite.utils.Refs;

import java.util.List;

public class ItemArmorElectric extends ItemArmorBase implements IElectricItem, IArmorTextureProvider, ISpecialArmor, IMetalArmor {

    public int TIER, TRANSFER, CAPACITY, META;
    public int ENERGY_PER_DAMAGE, DAMAGE_PRIORITY;
    public double BASE_ABSORPTION, DAMAGE_ABSORPTION;
    public String NAME;

    public ItemArmorElectric(int id, int meta, String name, int tier, int transfer, int capacity) {
        this(id, GraviSuiteData.GRAVI_MATERIAL, name, meta, tier, transfer, capacity);
    }

    public ItemArmorElectric(int id, EnumArmorMaterial armorMaterial, String name, int meta, int tier, int transfer, int capacity) {
        super(id, name, armorMaterial);
        this.setItemName(name);
        this.setCreativeTab(GraviSuite.TAB);
        this.setMaxDamage(27);
        this.iconIndex = Refs.ARMOR_PACK_ID + meta;
        this.NAME = name;
        this.TIER = tier;
        this.TRANSFER = transfer;
        this.CAPACITY = capacity;
        this.META = meta;

        this.DAMAGE_PRIORITY = 0;
        this.ENERGY_PER_DAMAGE = 0;
        this.BASE_ABSORPTION = 0.0D;
        this.DAMAGE_ABSORPTION = 0.0D;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack par1ItemStack) {
        return EnumRarity.uncommon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(int id, CreativeTabs tab, List items) {
        ElectricHelper.addChargeVariants(this, items);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getArmorTextureFile(ItemStack itemStack) {
        return "/mods/gravisuite/textures/armors/" + this.NAME + ".png";
    }

    @SuppressWarnings("unchecked")
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean isDebugMode) {
        tooltip.add(Messages.energyValue(ElectricHelper.getCharge(stack), this.getMaxCharge(), this.TIER));
    }

    @Override
    public String getTextureFile() {
        return GraviSuite.TEXTURE;
    }

    @Override
    public ArmorProperties getProperties(EntityLiving player, ItemStack armor, DamageSource source, double damage, int slot) {
        if (source.isUnblockable()) {
            return new ArmorProperties(0, 0.0F, 0);
        } else {
            double absorptionRatio = this.BASE_ABSORPTION * this.DAMAGE_ABSORPTION;
            int energyPerDamage = this.ENERGY_PER_DAMAGE;
            int damageLimit = energyPerDamage > 0 ? 25 * ElectricHelper.getCharge(armor) / energyPerDamage : 0;
            return new ArmorProperties(0, absorptionRatio, damageLimit);
        }
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        return ElectricHelper.getCharge(armor) >= this.ENERGY_PER_DAMAGE ? (int) Math.round((double) 20.0F * this.BASE_ABSORPTION * this.DAMAGE_ABSORPTION) : 0;
    }

    @Override
    public void damageArmor(EntityLiving entity, ItemStack stack, DamageSource damageSource, int damage, int slot) {
        ElectricItem.discharge(stack, damage * this.ENERGY_PER_DAMAGE, this.TIER, true, false);
    }

    @Override
    public boolean isMetalArmor(ItemStack itemStack, EntityPlayer entityPlayer) {
        return true;
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
