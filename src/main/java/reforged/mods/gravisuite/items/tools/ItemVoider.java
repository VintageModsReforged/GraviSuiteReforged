package reforged.mods.gravisuite.items.tools;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import mods.vintage.core.platform.lang.FormattedTranslator;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.GraviSuiteConfig;
import reforged.mods.gravisuite.items.tools.base.ItemToolElectric;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;

import java.util.List;

public class ItemVoider extends ItemToolElectric {

    public ItemVoider() {
        super(GraviSuiteConfig.VOIDER_ID, "voider", 1, 500, 10000, EnumToolMaterial.STONE);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SideOnly(Side.CLIENT)
    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean isDebugMode) {
        super.addInformation(stack, player, tooltip, isDebugMode);
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        NBTTagCompound filterStackTag = tag.getCompoundTag("FilterStack");
        ItemStack filterStack = ItemStack.loadItemStackFromNBT(filterStackTag);
        if (GraviSuite.proxy.isSneakKeyDown()) {
            tooltip.add(Helpers.clickFor("Right Click", "message.info.filter.set"));
        } else {
            tooltip.add(Helpers.pressForInfo(Refs.SNEAK_KEY));
        }
        if (filterStack != null) {
            tooltip.add(FormattedTranslator.GOLD.format("message.info.filter", FormattedTranslator.AQUA.literal(filterStack.getDisplayName())));
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        ItemStack filterStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("FilterStack"));
        if (IC2.platform.isSimulating()) {
            if (player.isSneaking()) {
                if (filterStack != null) {
                    for (int i = 0; i < player.inventory.mainInventory.length; i++) {
                        ItemStack slotStack = player.inventory.getStackInSlot(i);
                        if (slotStack != null) {
                            if (slotStack.isItemEqual(filterStack)) {
                                if (ElectricItem.manager.canUse(stack, 5)) {
                                    player.inventory.setInventorySlotContents(i, null);
                                    ElectricItem.manager.use(stack, slotStack.stackSize * 5, player);
                                }
                            }
                        }
                    }
                }
            }
        }
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
        int blockID = world.getBlockId(x, y, z);
        int blockMetadata = world.getBlockMetadata(x, y, z);
        Block block = Block.blocksList[blockID];
        ItemStack blockStack = new ItemStack(block, 1, blockMetadata);
        NBTTagCompound filterTag = blockStack.writeToNBT(new NBTTagCompound());
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        if (IC2.platform.isSimulating()) {
            if (!player.isSneaking()) {
                if (ElectricItem.manager.canUse(stack, 500)) {
                    tag.setTag("FilterStack", filterTag);
                    ElectricItem.manager.use(stack, 500, player);
                    return true;
                }
            }
        }
        return false;
    }

    @ForgeSubscribe
    public void onItemPickUpEvent(EntityItemPickupEvent e) {
        EntityPlayer player = e.entityPlayer;
        ItemStack voider = null;
        boolean doWork = false;
        for (int i = 0; i < 9; i++) {
            ItemStack hotbarStack = player.inventory.mainInventory[i];
            if (hotbarStack != null) {
                if (hotbarStack.getItem() instanceof ItemVoider) {
                    voider = hotbarStack;
                    doWork = Helpers.getOrCreateTag(hotbarStack).getCompoundTag("FilterStack") != null;
                    break;
                }
            }
        }
        if (voider != null && doWork) {
            ItemStack filterStack = ItemStack.loadItemStackFromNBT(Helpers.getOrCreateTag(voider).getCompoundTag("FilterStack"));
            if (filterStack != null) {
                ItemStack drop = e.item.getEntityItem();
                if (ElectricItem.manager.canUse(voider, 1)) {
                    if (drop.isItemEqual(filterStack)) {
                        ElectricItem.manager.use(voider, 1, player);
                        e.item.setDead();
                        e.setCanceled(true);
                    }
                }
            }
        }
    }
}
