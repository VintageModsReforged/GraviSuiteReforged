package reforged.mods.gravisuite.items.tools.base;

import ic2.core.IC2;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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
import reforged.mods.gravisuite.GraviSuiteMainConfig;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;
import reforged.mods.gravisuite.utils.pos.BlockPos;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ItemBaseExcavator extends ItemBaseTool {

    public static Set<Material> MATERIALS = new HashSet<Material>();

    static {
        MATERIALS.add(Material.grass);
        MATERIALS.add(Material.sand);
        MATERIALS.add(Material.snow);
        MATERIALS.add(Material.craftedSnow);
        MATERIALS.add(Material.ground);
    }

    public ItemBaseExcavator(int id, EnumToolMaterial enumToolMaterial, int meta, String name) {
        super(id, name + "_excavator", enumToolMaterial, meta);
        this.setCreativeTab(GraviSuite.TAB);
        MinecraftForge.setToolClass(this, "shovel", enumToolMaterial.getHarvestLevel());
        this.setIconIndex(Refs.EXCAVATOR_ID + this.meta);
    }

    @Override
    public float getStrVsBlock(ItemStack stack, Block block) {
        if (MATERIALS.contains(block.blockMaterial)) {
            return this.toolMaterial.getEfficiencyOnProperMaterial();
        } else {
            return 0.2f;
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
                BlockPos origin = new BlockPos(x, y, z);
                for (BlockPos pos : BlockPos.getAllInBoxMutable(origin.add(-xRange, -yRange, -zRange), origin.add(xRange, yRange, zRange))) {
                    Block adjBlock = Helpers.getBlock(world, pos);
                    int metadata = Helpers.getBlockMetadata(world, pos);
                    if (!world.isAirBlock(pos.getX(), pos.getY(), pos.getZ())) {
                        float strength = adjBlock.getBlockHardness(world, pos.getX(), pos.getY(), pos.getZ());
                        if (strength > 0f && strength / refStrength <= 10f) {
                            if (ForgeHooks.isToolEffective(stack, adjBlock, metadata) && harvestBlock(world, pos.getX(), pos.getY(), pos.getZ(), player)) {
                                mined++;
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
