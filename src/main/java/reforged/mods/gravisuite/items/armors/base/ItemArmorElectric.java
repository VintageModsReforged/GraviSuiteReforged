package reforged.mods.gravisuite.items.armors.base;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IMetalArmor;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.IArmorTextureProvider;
import net.minecraftforge.common.ISpecialArmor;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;
import reforged.mods.gravisuite.utils.TextFormatter;

import java.util.List;

public class ItemArmorElectric extends ItemArmorBase implements IElectricItem, IArmorTextureProvider, ISpecialArmor, IMetalArmor {

    public int tier, transfer, capacity;
    public int energy_per_damage, damage_priority;
    public double base_absorption, damage_absorption;
    public EnumRarity rarity;
    public String name;

    public ItemArmorElectric(int id, String name, int tier, int transfer, int capacity, EnumRarity rarity) {
        super(id, name);
        this.setMaxDamage(27);
        this.name = name;
        this.rarity = rarity;
        this.tier = tier;
        this.transfer = transfer;
        this.capacity = capacity;

        this.damage_priority = 0;
        this.energy_per_damage = 0;
        this.base_absorption = 0.0D;
        this.damage_absorption = 0.0D;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(int id, CreativeTabs creativeTab, List items) {
        Helpers.addChargeVariants(this, items);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return this.rarity;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister register) {
        this.itemIcon = register.registerIcon(Refs.id + ":armor/" + this.name);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getArmorTextureFile(ItemStack itemStack) {
        return "/mods/gravisuite/textures/armors/" + this.name + ".png";
    }

    @SuppressWarnings("unchecked")
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean isDebugMode) {
        tooltip.add(TextFormatter.AQUA.format(Helpers.getCharge(stack) + "/" + this.getMaxCharge(stack) + " EU" + " @ Tier " + this.tier));
    }

    @Override
    public ArmorProperties getProperties(EntityLiving entityLiving, ItemStack armor, DamageSource damageSource, double damage, int slot) {
        double absorption = this.base_absorption * this.damage_absorption;
        int damageLimit = (int) (this.energy_per_damage > 0 ? 25.0D * ElectricItem.manager.getCharge(armor) / this.energy_per_damage : 0.0D);
        return new ArmorProperties(this.damage_priority, absorption, damageLimit);
    }

    @Override
    public int getArmorDisplay(EntityPlayer entityPlayer, ItemStack itemStack, int i) {
        return (int) Math.round(20.0D * this.base_absorption * this.damage_absorption);
    }

    @Override
    public void damageArmor(EntityLiving entity, ItemStack stack, DamageSource damageSource, int damage, int slot) {
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
