package reforged.mods.gravisuite.blocks;

import ic2.core.IC2;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.tiles.TileEntityRelocatorPortal;

import java.util.Random;

public class BlockRelocatorPortal extends BlockContainer {

    public BlockRelocatorPortal(int id) {
        super(id, Material.portal);
        this.setLightValue(1f);
    }

    @Override
    public String getTextureFile() {
        return GraviSuite.TEXTURE;
    }

    @Override
    public int getBlockTextureFromSide(int par1) {
        return 96;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public int damageDropped(int i) {
        return 0;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return GraviSuite.blockRelocatorPortalRenderID;
    }

    @Override
    public boolean isBlockSolid(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return false;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (IC2.platform.isSimulating()) {
            TileEntity blockEntity = world.getBlockTileEntity(x, y, z);
            if (blockEntity instanceof TileEntityRelocatorPortal) {
                TileEntityRelocatorPortal portal = (TileEntityRelocatorPortal) blockEntity;
                int timer = portal.getCoolDownTime(entity);
                if (timer == 0) {
                    portal.teleportEntity(entity);
                }
            }
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int blockId, int meta) {
        world.removeBlockTileEntity(x, y, z);
        super.breakBlock(world, x, y, z, blockId, meta);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
        return null;
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityRelocatorPortal();
    }
}
