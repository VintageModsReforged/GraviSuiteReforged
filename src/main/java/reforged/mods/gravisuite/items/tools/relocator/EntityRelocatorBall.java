package reforged.mods.gravisuite.items.tools.relocator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import mods.vintage.core.helpers.BlockHelper;
import mods.vintage.core.helpers.pos.BlockPos;
import mods.vintage.core.platform.lang.Translator;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import reforged.mods.gravisuite.GraviSuiteConfig;
import reforged.mods.gravisuite.blocks.BlockRelocatorPortal;
import reforged.mods.gravisuite.items.armors.ItemAdvancedQuant;
import reforged.mods.gravisuite.tiles.TileEntityRelocatorPortal;
import reforged.mods.gravisuite.utils.Helpers;

public class EntityRelocatorBall extends EntityThrowable {

    private EntityLivingBase ownerEntity;

    private double startX;

    private double startY;

    private double startZ;

    private double maxRange;

    private double speedPerTick;

    private int dischargeArmorValue;

    private byte actionType;

    private TeleportPoint targetTpPoint;

    public static final byte RELOCATOR_TELEPORT = 0;

    public static final byte RELOCATOR_PORTAL = 1;

    public EntityRelocatorBall(World world, EntityLivingBase entity, TeleportPoint point, byte actionType) {
        super(world, entity);
        this.ownerEntity = entity;
        this.startX = this.posX;
        this.startY = this.posY;
        this.startZ = this.posZ;
        this.maxRange = 32.0D;
        this.speedPerTick = 1.33D;
        this.targetTpPoint = point;
        this.actionType = actionType;
        this.dischargeArmorValue = 500000;
        this.dataWatcher.updateObject(30, this.actionType);
    }

    public EntityRelocatorBall(World world) {
        super(world);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(30, this.actionType);
        this.dataWatcher.setObjectWatched(30);
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public float getShadowSize() {
        return 0.0F;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.worldObj.isRemote) {
            double distance = getDistance(this.startX, this.startY, this.startZ);
            if (distance >= this.maxRange || this.ticksExisted > this.maxRange / this.speedPerTick) {
                setDead();
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setByte("actionType", this.actionType);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.actionType = tag.getByte("actionType");
    }

    public byte getActionType() {
        return this.dataWatcher.getWatchableObjectByte(30);
    }

    @Override
    protected void onImpact(MovingObjectPosition mop) {
        if (mop.entityHit != null) {
            if (this.actionType == RELOCATOR_TELEPORT && this.targetTpPoint != null) {
                if (mop.entityHit instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) mop.entityHit;
                    ItemStack stack = player.getCurrentArmor(2);
                    if (stack != null && stack.getItem() instanceof ItemAdvancedQuant) {
                        if (ElectricItem.manager.getCharge(stack) < this.dischargeArmorValue) {
                            Helpers.teleportEntity(mop.entityHit, this.targetTpPoint);
                        } else if (IC2.platform.isSimulating()) {
                            IC2.platform.messagePlayer(player, Translator.RESET.format("message.tool.relocator.teleport.to"));
                            ElectricItem.manager.discharge(stack, this.dischargeArmorValue, Integer.MAX_VALUE, true, false);
                        }
                    } else {
                        Helpers.teleportEntity(mop.entityHit, this.targetTpPoint);
                    }
                } else {
                    Helpers.teleportEntity(mop.entityHit, this.targetTpPoint);
                }
            }
        } else if (this.actionType == RELOCATOR_PORTAL) {
            int x = mop.blockX;
            int y = mop.blockY;
            int z = mop.blockZ;
            switch (mop.sideHit) {
                case 0:
                    y--;
                    break;
                case 1:
                    y++;
                    break;
                case 2:
                    z--;
                    break;
                case 3:
                    z++;
                    break;
                case 4:
                    x--;
                    break;
                case 5:
                    x++;
                    break;
            }
            if (IC2.platform.isSimulating()) {
                this.worldObj.setBlockToAir(x, y, z);
                this.worldObj.setBlock(x, y, z, GraviSuiteConfig.RELOCATOR_PORTAL_BLOCK_ID.get());
                this.worldObj.markBlockForUpdate(x, y, z);
                MinecraftServer minecraftServer = MinecraftServer.getServer();
                WorldServer worldServer = minecraftServer.worldServerForDimension(this.targetTpPoint.DIMENSION_ID);
                worldServer.theChunkProviderServer.loadChunk(this.targetTpPoint.POS.getX() >> 4, this.targetTpPoint.POS.getZ() >> 4);
                Block block = BlockHelper.getBlock(worldServer, this.targetTpPoint.POS);
                if (!(block instanceof BlockRelocatorPortal)) {
                    BlockHelper.setBlock(worldServer, this.targetTpPoint.POS, GraviSuiteConfig.RELOCATOR_PORTAL_BLOCK_ID.get());
                    BlockHelper.markBlockForUpdate(worldServer, this.targetTpPoint.POS);
                }
                TileEntity tileEntity1 = BlockHelper.getBlockTileEntity(worldServer, this.targetTpPoint.POS);
                if (tileEntity1 instanceof TileEntityRelocatorPortal) {
                    TeleportPoint teleportPoint = new TeleportPoint();
                    teleportPoint.DIMENSION_ID = this.worldObj.provider.dimensionId;
                    teleportPoint.POS = new BlockPos(x, y, z);
                    ((TileEntityRelocatorPortal) tileEntity1).setParentPortal(teleportPoint);
                }
                TileEntity tileEntity2 = this.worldObj.getBlockTileEntity(x, y, z);
                if (tileEntity1 instanceof TileEntityRelocatorPortal)
                    ((TileEntityRelocatorPortal) tileEntity2).setParentPortal(this.targetTpPoint);
            }
        }
        if (!worldObj.isRemote) {
            setDead();
        }
    }
}
