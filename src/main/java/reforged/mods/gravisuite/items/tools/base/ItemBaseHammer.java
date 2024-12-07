package reforged.mods.gravisuite.items.tools.base;

import ic2.core.IC2;
import mods.vintage.core.helpers.BlockHelper;
import mods.vintage.core.helpers.ToolHelper;
import mods.vintage.core.helpers.pos.BlockPos;
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
import reforged.mods.gravisuite.GraviSuiteData;
import reforged.mods.gravisuite.GraviSuiteMainConfig;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ItemBaseHammer extends ItemBaseTool {

    public Set<Block> mineableBlocks = new HashSet<Block>();
    public Set<Material> mineableBlockMaterials = new HashSet<Material>();

    public ItemBaseHammer(int id, EnumToolMaterial enumToolMaterial, int meta, String name) {
        super(id, name + "_hammer", enumToolMaterial, meta);
        this.setCreativeTab(GraviSuite.TAB);
        MinecraftForge.setToolClass(this, "pickaxe", enumToolMaterial.getHarvestLevel());
        this.setIconIndex(Refs.HAMMERS_ID + this.meta);
        init();
    }

    @Override
    public float getStrVsBlock(ItemStack stack, Block block) {
        if (this.mineableBlocks.contains(block) || this.mineableBlockMaterials.contains(block.blockMaterial)) {
            return this.toolMaterial.getEfficiencyOnProperMaterial();
        }
        return 0.2F;
    }

    @Override
    public boolean canHarvestBlock(Block block) {
        EnumToolMaterial mat = this.toolMaterial;
        boolean effectiveOn = this.mineableBlocks.contains(block) || this.mineableBlockMaterials.contains(block.blockMaterial);
        if (mat.equals(EnumToolMaterial.WOOD)) {
            return pickaxeWood.canHarvestBlock(block) && effectiveOn;
        } else if (mat.equals(EnumToolMaterial.STONE)) {
            return pickaxeStone.canHarvestBlock(block) && effectiveOn;
        } else if (mat.equals(EnumToolMaterial.IRON) || mat.equals(GraviSuiteData.GEMS_MATERIAL)) {
            return pickaxeSteel.canHarvestBlock(block) && effectiveOn;
        } else {
            return effectiveOn;
        }
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        if (IC2.platform.isSimulating()) {
            int mined = 0;
            World world = player.worldObj;
            Block block = BlockHelper.getBlock(world, x, y, z);
            if (!canHarvestBlock(block))
                return false;
            int radius = player.isSneaking() ? 0 : 1;
            float refStrength = block.getBlockHardness(world, x, y, z);
            if (refStrength != 0.0D) {
                BlockPos origin = new BlockPos(x, y, z);
                for (BlockPos pos : ToolHelper.getAOE(player, origin, radius)) {
                    Block adjBlock = BlockHelper.getBlock(world, pos);
                    if (!BlockHelper.isAir(world, pos)) {
                        float strength = adjBlock.getBlockHardness(world, pos.getX(), pos.getY(), pos.getZ());
                        if (strength > 0f && strength / refStrength <= 8f) {
                            if ((getStrVsBlock(stack, adjBlock, world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ())) > 0.0F && canHarvestBlock(adjBlock)) && ToolHelper.harvestBlock(world, pos.getX(), pos.getY(), pos.getZ(), player)) {
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


    public void init() {
        this.mineableBlocks.add(Block.glass);
        this.mineableBlocks.add(Block.thinGlass);
        this.mineableBlocks.add(Block.glowStone);
        this.mineableBlocks.add(Block.silverfish);
        this.mineableBlocks.add(Block.cobblestone);
        this.mineableBlocks.add(Block.stoneSingleSlab);
        this.mineableBlocks.add(Block.stoneDoubleSlab);
        this.mineableBlocks.add(Block.stairCompactCobblestone);
        this.mineableBlocks.add(Block.stone);
        this.mineableBlocks.add(Block.sandStone);
        this.mineableBlocks.add(Block.stairsSandStone);
        this.mineableBlocks.add(Block.cobblestoneMossy);
        this.mineableBlocks.add(Block.oreIron);
        this.mineableBlocks.add(Block.blockSteel);
        this.mineableBlocks.add(Block.oreCoal);
        this.mineableBlocks.add(Block.blockGold);
        this.mineableBlocks.add(Block.oreGold);
        this.mineableBlocks.add(Block.oreDiamond);
        this.mineableBlocks.add(Block.blockDiamond);
        this.mineableBlocks.add(Block.ice);
        this.mineableBlocks.add(Block.netherrack);
        this.mineableBlocks.add(Block.oreLapis);
        this.mineableBlocks.add(Block.blockLapis);
        this.mineableBlocks.add(Block.oreRedstone);
        this.mineableBlocks.add(Block.oreRedstoneGlowing);
        this.mineableBlocks.add(Block.brick);
        this.mineableBlocks.add(Block.stairsBrick);
        this.mineableBlocks.add(Block.stoneBrick);
        this.mineableBlocks.add(Block.stairsStoneBrickSmooth);
        this.mineableBlocks.add(Block.netherBrick);
        this.mineableBlocks.add(Block.stairsNetherBrick);
        this.mineableBlocks.add(Block.obsidian);
        this.mineableBlocks.add(Block.anvil);

        this.mineableBlockMaterials.add(Material.anvil);
        this.mineableBlockMaterials.add(Material.circuits);
        this.mineableBlockMaterials.add(Material.dragonEgg);
        this.mineableBlockMaterials.add(Material.glass);
        this.mineableBlockMaterials.add(Material.iron);
        this.mineableBlockMaterials.add(Material.piston);
        this.mineableBlockMaterials.add(Material.redstoneLight);
        this.mineableBlockMaterials.add(Material.rock);
    }
}