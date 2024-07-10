package reforged.mods.gravisuite.items.tools.base;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.IElectricItem;
import ic2.core.IC2;
import net.minecraft.client.renderer.texture.IconRegister;
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
import reforged.mods.gravisuite.utils.Refs;
import reforged.mods.gravisuite.utils.TextFormatter;

import java.util.List;
import java.util.Locale;

public class ItemToolElectric extends ItemToolBase implements IElectricItem {

    public int tier, transfer, capacity;
    public String name;

    protected ItemToolElectric(int id, String name, int tier, int transfer, int capacity, EnumToolMaterial material) {
        super(id, name, material);
        this.setMaxDamage(27);
        this.name = name;
        this.tier = tier;
        this.transfer = transfer;
        this.capacity = capacity;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.uncommon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister icons) {
        this.itemIcon = icons.registerIcon(Refs.id + ":" + this.name);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(int is, CreativeTabs tabs, List items) {
        Helpers.addChargeVariants(this, items);
    }

    @SuppressWarnings("unchecked")
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean isDebugMode) {
        tooltip.add(TextFormatter.AQUA.literal(Helpers.getCharge(stack) + "/" + this.getMaxCharge(stack) + " EU" + " @ Tier " + this.tier));
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, int blockID, int x, int y, int z, EntityLiving user) {
        return false;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
        if (!IC2.keyboard.isModeSwitchKeyDown(player) && !IC2.keyboard.isAltKeyDown(player) && !IC2.keyboard.isSneakKeyDown(player)) {
            ItemStack torchStack = null;
            int torchSlot = 0;
            for (int i = 0; i < player.inventory.mainInventory.length; i++) {
                ItemStack checkStack = player.inventory.mainInventory[i];
                if (checkStack != null) {
                    if (checkStack.getDisplayName().toLowerCase(Locale.ENGLISH).contains("torch") && !checkStack.getDisplayName().toLowerCase(Locale.ENGLISH).contains("redstone")) {
                        torchStack = checkStack;
                        torchSlot = i;
                        break;
                    }
                }
            }

            if (torchStack != null) {
                Item torchItem = torchStack.getItem();
                if (torchItem instanceof net.minecraft.item.ItemBlock) {
                    int oldMeta = torchStack.getItemDamage();
                    int oldSize = torchStack.stackSize;
                    boolean result = torchStack.tryPlaceItemIntoWorld(player, world, x, y, z, side, xOffset,
                            yOffset, zOffset);
                    if (player.capabilities.isCreativeMode) {
                        torchStack.setItemDamage(oldMeta);
                        torchStack.stackSize = oldSize;
                    } else if (torchStack.stackSize <= 0) {
                        ForgeEventFactory.onPlayerDestroyItem(player, torchStack);
                        player.inventory.mainInventory[torchSlot] = null;
                    }
                    if (result)
                        return true;
                }
            }
        }
        return super.onItemUse(stack, player, world, x, y, z, side, xOffset, yOffset, zOffset);
    }

    public boolean canOperate(ItemStack stack) {
        return false;
    }

    // IElectricItem start

    @Override
    public boolean canProvideEnergy(ItemStack itemStack) {
        return false;
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

    // end
}
