package reforged.mods.gravisuite.items.tools;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import mods.vintage.core.helpers.StackHelper;
import mods.vintage.core.platform.lang.Translator;
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
import reforged.mods.gravisuite.items.tools.base.ItemToolBase;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.KeyDescriptionHelper;
import reforged.mods.gravisuite.utils.Messages;

import java.util.List;

public class ItemVoider extends ItemToolBase {

    public ItemVoider() {
        super(GraviSuiteConfig.VOIDER_ID.get(), "voider", EnumToolMaterial.STONE);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SideOnly(Side.CLIENT)
    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean isDebugMode) {
        super.addInformation(stack, player, tooltip, isDebugMode);
        NBTTagCompound tag = StackHelper.getOrCreateTag(stack);
        NBTTagCompound filterStackTag = tag.getCompoundTag("FilterStack");
        ItemStack filterStack = ItemStack.loadItemStackFromNBT(filterStackTag);
        tooltip.add(Translator.GREEN.format("message.tool.voider.hotbar"));
        if (filterStack != null) {
            tooltip.add(Translator.GOLD.format("message.info.filter", Translator.AQUA.literal(filterStack.getDisplayName())));
        }
        if (GraviSuite.proxy.isSneakKeyDown()) {
            tooltip.add(KeyDescriptionHelper.buildKeyDescription(new KeyDescriptionHelper.Keys[] {KeyDescriptionHelper.Keys.SNEAK_KEY, KeyDescriptionHelper.Keys.RIGHT_CLICK}, KeyDescriptionHelper.KeyMode.BLOCK, Translator.YELLOW.format("message.info.filter.set")));
            tooltip.add(KeyDescriptionHelper.buildKeyDescription(KeyDescriptionHelper.Keys.MODE_KEY, KeyDescriptionHelper.Keys.RIGHT_CLICK, Translator.YELLOW.format("message.tool.voider.remove.all")));
        } else {
            tooltip.add(Helpers.pressForInfo(Messages.Translations.KEY_SNEAK.format()));
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        NBTTagCompound tag = StackHelper.getOrCreateTag(stack);
        ItemStack filterStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("FilterStack"));
        if (IC2.keyboard.isModeSwitchKeyDown(player)) {
            if (filterStack != null) {
                int removed = 0;
                for (int i = 0; i < player.inventory.mainInventory.length; i++) {
                    ItemStack slotStack = player.inventory.getStackInSlot(i);
                    if (slotStack != null) {
                        if (slotStack.isItemEqual(filterStack)) {
                            player.inventory.setInventorySlotContents(i, null);
                            removed++;
                        }
                    }
                }
                if (IC2.platform.isRendering()) {
                    if (removed > 0) {
                        IC2.platform.messagePlayer(player, Translator.GREEN.format("message.tool.voider.removed"));
                    } else {
                        IC2.platform.messagePlayer(player, Translator.RED.format("message.tool.voider.removed.none"));
                    }
                }
            }
        } else if (!player.isSneaking()) {
            player.openGui(GraviSuite.instance, 4, world, (int) player.posX, (int) player.posY, (int) player.posZ);
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
        NBTTagCompound tag = StackHelper.getOrCreateTag(stack);
        if (IC2.platform.isSimulating()) {
            if (player.isSneaking()) {
                tag.setTag("FilterStack", filterTag);
                IC2.platform.messagePlayer(player, Translator.GOLD.format("message.info.filter", Translator.AQUA.literal(blockStack.getDisplayName())));
                return true;
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
                    doWork = StackHelper.getOrCreateTag(hotbarStack).getCompoundTag("FilterStack") != null;
                    break;
                }
            }
        }
        if (voider != null && doWork) {
            ItemStack filterStack = ItemStack.loadItemStackFromNBT(StackHelper.getOrCreateTag(voider).getCompoundTag("FilterStack"));
            if (filterStack != null) {
                ItemStack drop = e.item.getEntityItem();
                if (drop.isItemEqual(filterStack)) {
                    e.item.setDead();
                    e.setCanceled(true);
                }
            }
        }
    }
}
