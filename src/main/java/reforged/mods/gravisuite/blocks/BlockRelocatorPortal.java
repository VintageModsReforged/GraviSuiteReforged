package reforged.mods.gravisuite.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.tiles.TileEntityRelocatorPortal;
import reforged.mods.gravisuite.utils.Refs;

import java.util.Random;

public class BlockRelocatorPortal extends BlockContainer {

    private Icon ICON;

    public BlockRelocatorPortal(int id) {
        super(id, Material.portal);
        this.setLightValue(1f);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister registry) {
        this.ICON = registry.registerIcon(Refs.id + ":relocator_portal");
    }

    @Override
    public Icon getIcon(int par1, int par2) {
        return this.ICON;
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
