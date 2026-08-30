package reforged.mods.gravisuite.items.tools.base;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.IElectricItem;
import ic2.core.IC2;
import mods.vintage.core.helpers.ElectricHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Messages;

import java.util.List;
import java.util.Locale;

public class ItemToolElectric extends ItemToolBase implements IElectricItem {

    public int TIER, TRANSFER, CAPACITY;

    public ItemToolElectric(int id, String name, int tier, int transfer, int capacity, EnumToolMaterial material) {
        super(id, name, material, null);
        this.setMaxDamage(27);
        this.TIER = tier;
        this.TRANSFER = transfer;
        this.CAPACITY = capacity;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.uncommon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(int item, CreativeTabs tab, List items) {
        ElectricHelper.addChargeVariants(this, items);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean debugMode) {
        addEnergyInfo(stack, tooltip);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void addEnergyInfo(ItemStack stack, List tooltip) {
        tooltip.add(Messages.energyValue(ElectricHelper.getCharge(stack), this.getMaxCharge(), this.TIER));
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, int blockID, int x, int y, int z, EntityLiving user) {
        return false;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLiving attacker, EntityLiving target) {
        return false;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
        if (!IC2.keyboard.isModeSwitchKeyDown(player) && !IC2.keyboard.isAltKeyDown(player) && !IC2.keyboard.isSneakKeyDown(player)) {
            for (int i = 0; i < player.inventory.mainInventory.length; i++) {
                ItemStack check = player.inventory.mainInventory[i];
                if (check != null) {
                    if(check.getItemName().toLowerCase(Locale.ROOT).contains("torch") && !check.getItemName().toLowerCase(Locale.ROOT).contains("redstone")) {
                        Item item = check.getItem();
                        if (item instanceof net.minecraft.item.ItemBlock) {
                            int oldMeta = check.getItemDamage();
                            int oldSize = check.stackSize;
                            boolean result = check.tryPlaceItemIntoWorld(player, world, x, y, z, side, xOffset,
                                    yOffset, zOffset);
                            if (player.capabilities.isCreativeMode) {
                                check.setItemDamage(oldMeta);
                                check.stackSize = oldSize;
                            } else if (check.stackSize <= 0) {
                                ForgeEventFactory.onPlayerDestroyItem(player, check);
                                player.inventory.mainInventory[i] = null;
                            }
                            if (result)
                                return true;
                        }
                    }
                }
            }
        }
        return super.onItemUse(stack, player, world, x, y, z, side, xOffset, yOffset, zOffset);
    }

    public boolean canOperate(ItemStack stack) {
        return false;
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
