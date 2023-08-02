package reforged.mods.gravisuite.items.tools.base;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemTool;
import net.minecraft.network.packet.Packet14BlockDig;
import net.minecraft.network.packet.Packet53BlockChange;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import reforged.mods.gravisuite.GraviSuite;

public class ItemToolBase extends ItemTool {

    protected ItemToolBase(int id, String name, EnumToolMaterial material) {
        super(id, 0, material, new Block[0]);
        this.setUnlocalizedName(name);
        this.setMaxStackSize(1);
        this.setNoRepair();
        this.setCreativeTab(GraviSuite.graviTab);
    }

    public boolean harvestBlock(World world, int x, int y, int z, EntityPlayer player) {
        if (world.isAirBlock(x, y, z)) {
            return false;
        } else {
            EntityPlayerMP playerMP = null;
            if (player instanceof EntityPlayerMP) {
                playerMP = (EntityPlayerMP)player;
            }

            Block block = Block.blocksList[world.getBlockId(x, y, z)];
            int blockMetadata = world.getBlockMetadata(x, y, z);
            if (!ForgeHooks.canHarvestBlock(block, player, 0)) {
                return false;
            } else if (!ForgeHooks.canHarvestBlock(block, player, blockMetadata)) {
                return false;
            } else {
                if (player.capabilities.isCreativeMode) {
                    if (!world.isRemote) {
                        block.onBlockHarvested(world, x, y, z, blockMetadata, player);
                    } else {
                        world.playAuxSFX(2001, x, y, z, world.getBlockId(x, y, z) | blockMetadata << 12);
                    }

                    if (block.removeBlockByPlayer(world, player, x, y, z)) {
                        block.onBlockDestroyedByPlayer(world, x, y, z, blockMetadata);
                    }

                    if (!world.isRemote) {
                        assert playerMP != null;
                        playerMP.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange());
                    } else {
                        Minecraft.getMinecraft().getNetHandler().addToSendQueue(new Packet14BlockDig());
                    }

                } else {
                    world.playAuxSFXAtEntity(player, 2001, x, y, z, world.getBlockId(x, y, z) | blockMetadata << 12);
                    if (!world.isRemote) {
                        block.onBlockHarvested(world, x, y, z, blockMetadata, player);
                        if (block.removeBlockByPlayer(world, player, x, y, z)) {
                            block.onBlockDestroyedByPlayer(world, x, y, z, blockMetadata);
                            block.harvestBlock(world, player, x, y, z, blockMetadata);
                        }
                        assert playerMP != null;
                        playerMP.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange());
                    } else {
                        if (block.removeBlockByPlayer(world, player, x, y, z)) {
                            block.onBlockDestroyedByPlayer(world, x, y, z, blockMetadata);
                        }
                        Minecraft.getMinecraft().getNetHandler().addToSendQueue(new Packet14BlockDig());
                    }
                }
                return true;
            }
        }
    }
}
