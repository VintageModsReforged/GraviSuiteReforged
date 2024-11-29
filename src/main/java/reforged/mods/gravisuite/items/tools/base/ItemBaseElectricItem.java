package reforged.mods.gravisuite.items.tools.base;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.IElectricItem;
import mods.vintage.core.platform.lang.FormattedTranslator;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import reforged.mods.gravisuite.items.IToolTipProvider;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;

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

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, int blockID, int x, int y, int z, EntityLiving user) {
        return false;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLiving attacker, EntityLiving target) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean debugMode) {
        tooltip.add(FormattedTranslator.AQUA.format("message.info.energy", Helpers.getCharge(stack), this.getMaxCharge(), FormattedTranslator.WHITE.format("message.info.energy.tier", FormattedTranslator.YELLOW.literal(this.getTier() + ""))));
    }

    public void addKeyTooltips(List tooltip, IToolTipProvider provider) {
        if (Helpers.isShiftKeyDown()) {
            provider.addTooltip();
        } else {
            tooltip.add(Helpers.pressForInfo(Refs.SNEAK_KEY));
        }
    }

    @SideOnly(Side.CLIENT)
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
