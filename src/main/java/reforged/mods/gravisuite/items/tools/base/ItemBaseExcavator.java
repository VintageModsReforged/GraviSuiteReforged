package reforged.mods.gravisuite.items.tools.base;

import ic2.core.IC2;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.GraviSuiteData;
import reforged.mods.gravisuite.GraviSuiteMainConfig;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;

import java.util.Locale;

public class ItemBaseExcavator extends ItemBaseTool {

    public ItemBaseExcavator(int id, EnumToolMaterial enumToolMaterial, int meta, String name) {
        super(id, name + "_excavator", enumToolMaterial, meta);
        this.setCreativeTab(GraviSuite.TAB);
        MinecraftForge.setToolClass(this, "shovel", enumToolMaterial.getHarvestLevel());
        this.setIconIndex(Refs.EXCAVATOR_ID + this.meta);
    }

    @Override
    public float getStrVsBlock(ItemStack stack, Block block) {
        return this.toolMaterial.getEfficiencyOnProperMaterial();
    }

    @Override
    public boolean canHarvestBlock(Block block) {
        EnumToolMaterial mat = this.toolMaterial;
        if (mat.equals(EnumToolMaterial.WOOD)) {
            return shovelWood.canHarvestBlock(block);
        } else if (mat.equals(EnumToolMaterial.STONE)) {
            return shovelStone.canHarvestBlock(block);
        } else if (mat.equals(EnumToolMaterial.IRON) || mat.equals(GraviSuiteData.GEMS_MATERIAL)) {
            return shovelSteel.canHarvestBlock(block);
        } else {
            return shovelDiamond.canHarvestBlock(block);
        }
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        if (IC2.platform.isSimulating()) {
            int mined = 0;
            World world = player.worldObj;
            Block block = Block.blocksList[world.getBlockId(x, y, z)];
            int radius = player.isSneaking() ? 0 : 1;
            float refStrength = block.getBlockHardness(world, x, y, z);
            if (refStrength != 0.0D) {
                int adjBlockId;
                float strength;
                MovingObjectPosition mop = Helpers.raytraceFromEntity(world, player, true, 4.5D);
                if (mop == null) { // cancel 3x3 when rayTrace fails
                    return false;
                }
                int xRange = radius, yRange = radius, zRange = radius;
                switch (mop.sideHit) {
                    case 0:
                    case 1:
                        yRange = 0;
                        break;
                    case 2:
                    case 3:
                        zRange = 0;
                        break;
                    case 4:
                    case 5:
                        xRange = 0;
                        break;
                }

                for (int xPos = x - xRange; xPos <= x + xRange; xPos++) {
                    for (int yPos = y - yRange; yPos <= y + yRange; yPos++) {
                        for (int zPos = z - zRange; zPos <= z + zRange; zPos++) {
                            adjBlockId = world.getBlockId(xPos, yPos, zPos);
                            Block adjBlock = Block.blocksList[adjBlockId];
                            if (adjBlockId != 0) {
                                strength = adjBlock.getBlockHardness(world, xPos, yPos, zPos);
                                if (strength > 0f && strength / refStrength <= 10f) {
                                    if ((ForgeHooks.isToolEffective(stack, adjBlock, world.getBlockMetadata(xPos, yPos, zPos)) || canHarvestBlock(adjBlock)) && harvestBlock(world, xPos, yPos, zPos, player)) {
                                        mined++;
                                    }
                                }
                            }
                        }
                    }
                }
                if (mined > 0) {
                    stack.damageItem((int) (mined * GraviSuiteMainConfig.DURABILITY_FACTOR), player);
                }
            } else {
                return super.onBlockStartBreak(stack, x, y, z, player);
            }
        }
        return false;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, int id, int x, int y, int z, EntityLiving entity) {
        return false;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            ItemStack check = player.inventory.mainInventory[i];
            if (check != null) {
                if(check.getDisplayName().toLowerCase(Locale.ENGLISH).contains("torch")) {
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
        return super.onItemUse(stack, player, world, x, y, z, side, xOffset, yOffset, zOffset);
    }
}
