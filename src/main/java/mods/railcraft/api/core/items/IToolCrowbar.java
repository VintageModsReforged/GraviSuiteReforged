package mods.railcraft.api.core.items;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IToolCrowbar {
    boolean canWhack(EntityPlayer var1, ItemStack var2, int var3, int var4, int var5);

    void onWhack(EntityPlayer var1, ItemStack var2, int var3, int var4, int var5);

    boolean canLink(EntityPlayer var1, ItemStack var2, EntityMinecart var3);

    void onLink(EntityPlayer var1, ItemStack var2, EntityMinecart var3);

    boolean canBoost(EntityPlayer var1, ItemStack var2, EntityMinecart var3);

    void onBoost(EntityPlayer var1, ItemStack var2, EntityMinecart var3);
}
