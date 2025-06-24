package reforged.mods.gravisuite.items.armors.base;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IMetalArmor;
import mods.vintage.core.platform.lang.FormattedTranslator;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;
import reforged.mods.gravisuite.GraviSuiteData;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;

import java.util.List;

public class ItemArmorElectric extends ItemArmorBase implements IElectricItem, ISpecialArmor, IMetalArmor {

    public int tier, transfer, capacity;
    public int energy_per_damage, damage_priority;
    public double base_absorption, damage_absorption;
    public String name;

    public ItemArmorElectric(int id, String name, int tier, int transfer, int capacity) {
        this(id, name, GraviSuiteData.GRAVI_MATERIAL, tier, transfer, capacity);
    }

    public ItemArmorElectric(int id, String name, EnumArmorMaterial armorMaterial, int tier, int transfer, int capacity) {
        super(id, name, armorMaterial);
        this.setMaxDamage(27);
        this.name = name;
        this.tier = tier;
        this.transfer = transfer;
        this.capacity = capacity;

        this.damage_priority = 0;
        this.energy_per_damage = 0;
        this.base_absorption = 0.0D;
        this.damage_absorption = 0.0D;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.uncommon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(int id, CreativeTabs creativeTab, List items) {
        Helpers.addChargeVariants(this, items);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister register) {
        this.itemIcon = register.registerIcon(Refs.id + ":armor/" + this.name);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return "gravisuite:textures/armors/" + this.name + ".png";
    }

    @SuppressWarnings("unchecked")
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean isDebugMode) {
        tooltip.add(FormattedTranslator.AQUA.format("message.info.energy", Helpers.getCharge(stack), this.getMaxCharge(stack), FormattedTranslator.WHITE.format("message.info.energy.tier", FormattedTranslator.YELLOW.literal(this.tier + ""))));
    }

    @Override
    public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        if (source.isUnblockable()) {
            return new ArmorProperties(0, 0.0F, 0);
        } else {
            double absorptionRatio = this.base_absorption * this.damage_absorption;
            int energyPerDamage = this.energy_per_damage;
            int damageLimit = energyPerDamage > 0 ? 25 * ElectricItem.manager.getCharge(armor) / energyPerDamage : 0;
            return new ArmorProperties(0, absorptionRatio, damageLimit);
        }
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        return ElectricItem.manager.getCharge(armor) >= this.energy_per_damage ? (int) Math.round((double) 20.0F * this.base_absorption * this.damage_absorption) : 0;
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource damageSource, int damage, int slot) {
        ElectricItem.manager.discharge(stack, damage * this.energy_per_damage, this.tier, true, false);
    }

    @Override
    public boolean canProvideEnergy(ItemStack itemStack) {
        return true;
    }

    @Override
    public int getChargedItemId(ItemStack itemStack) {
        return this.itemID;
    }

    @Override
    public int getEmptyItemId(ItemStack itemStack) {
        return this.itemID;
    }

    @Override
    public int getMaxCharge(ItemStack itemStack) {
        return this.capacity;
    }

    @Override
    public int getTier(ItemStack itemStack) {
        return this.tier;
    }

    @Override
    public int getTransferLimit(ItemStack itemStack) {
        return this.transfer;
    }

    @Override
    public boolean isMetalArmor(ItemStack itemStack, EntityPlayer entityPlayer) {
        return true;
    }
}
