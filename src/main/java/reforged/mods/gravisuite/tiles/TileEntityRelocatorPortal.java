package reforged.mods.gravisuite.tiles;

import com.google.common.collect.Lists;
import ic2.core.IC2;
import mods.vintage.core.helpers.BlockHelper;
import mods.vintage.core.helpers.pos.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import reforged.mods.gravisuite.blocks.BlockRelocatorPortal;
import reforged.mods.gravisuite.items.tools.relocator.TeleportPoint;
import reforged.mods.gravisuite.utils.Helpers;

import java.util.ArrayList;
import java.util.List;

public class TileEntityRelocatorPortal extends TileEntity {

    private int timerTicks;

    private final int maxTime;

    private final int maxCoolDownTime;

    public TeleportPoint parentTeleportPoint = null;

    private final List<EntityInfo> entityList;

    public TileEntityRelocatorPortal() {
        this.timerTicks = 0;
        this.maxTime = 500;
        this.maxCoolDownTime = 60;
        this.entityList = Lists.newArrayList();
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        this.timerTicks++;
        if (IC2.platform.isSimulating()) {
            updateEntityCoolDown();
            checkParentPortal();
        }
        if (this.timerTicks >= maxTime && IC2.platform.isSimulating()) {
            MinecraftServer minecraftServer = MinecraftServer.getServer();
            this.worldObj.setBlockToAir(this.xCoord, this.yCoord, this.zCoord);
            this.worldObj.markBlockForRenderUpdate(this.xCoord, this.yCoord, this.zCoord);
            this.worldObj.removeBlockTileEntity(this.xCoord, this.yCoord, this.zCoord);
            if (this.parentTeleportPoint != null) {
                WorldServer worldServer = minecraftServer.worldServerForDimension(this.parentTeleportPoint.DIMENSION_ID);
                worldServer.theChunkProviderServer.loadChunk(this.parentTeleportPoint.POS.getX() >> 4, this.parentTeleportPoint.POS.getZ() >> 4);
                Block block = BlockHelper.getBlock(worldServer, this.parentTeleportPoint.POS);
                if (block == null)
                    return;
                if (block instanceof BlockRelocatorPortal) {
                    BlockHelper.setBlockToAir(worldServer, this.parentTeleportPoint.POS);
                    BlockHelper.markBlockForRenderUpdate(worldServer, this.parentTeleportPoint.POS);
                    BlockHelper.removeBlockTileEntity(worldServer, this.parentTeleportPoint.POS);
                }
            }
        }
    }

    public void checkParentPortal() {
        if (this.parentTeleportPoint != null) {
            MinecraftServer minecraftServer = MinecraftServer.getServer();
            WorldServer worldServer = minecraftServer.worldServerForDimension(this.parentTeleportPoint.DIMENSION_ID);
            worldServer.theChunkProviderServer.loadChunk(this.parentTeleportPoint.POS.getX() >> 4, this.parentTeleportPoint.POS.getZ() >> 4);
            Block block = BlockHelper.getBlock(worldServer, this.parentTeleportPoint.POS);
            if (block == null)
                return;
            if (!(block instanceof BlockRelocatorPortal)) {
                this.worldObj.setBlockToAir(this.xCoord, this.yCoord, this.zCoord);
                this.worldObj.markBlockForRenderUpdate(this.xCoord, this.yCoord, this.zCoord);
                this.worldObj.removeBlockTileEntity(this.xCoord, this.yCoord, this.zCoord);
            }
        }
    }

    public void updateEntityCoolDown() {
        if (!this.entityList.isEmpty()) {
            for (EntityInfo entityInfo : this.entityList) {
                entityInfo.portalCooldown--;
                if (entityInfo.portalCooldown < 0)
                    entityInfo.portalCooldown = 0;
            }
        }
    }

    public int getCoolDownTime(Entity entity) {
        if (!this.entityList.isEmpty()) {
            for (EntityInfo entityInfo : this.entityList) {
                if (entityInfo.entityID.equalsIgnoreCase(entity.getPersistentID().toString()))
                    return entityInfo.portalCooldown;
            }
        }
        return 0;
    }

    public void addEntityToList(Entity entity) {
        boolean entityFound = false;
        if (!this.entityList.isEmpty()) {
            for (EntityInfo point : this.entityList) {
                if (point.entityID.equalsIgnoreCase(entity.getPersistentID().toString())) {
                    point.portalCooldown = this.maxCoolDownTime;
                    point.yaw = (entity.rotationYaw - 180.0F);
                    entityFound = true;
                }
            }
        }
        if (!entityFound) {
            EntityInfo newPoint = new EntityInfo();
            newPoint.entityID = entity.getPersistentID().toString();
            newPoint.yaw = (entity.rotationYaw - 180.0F);
            newPoint.portalCooldown = this.maxCoolDownTime;

            this.entityList.add(newPoint);
        }
    }

    public void teleportEntity(Entity paramEntity) {
        if (this.parentTeleportPoint != null) {
            MinecraftServer minecraftServer = MinecraftServer.getServer();
            WorldServer worldServer = minecraftServer.worldServerForDimension(this.parentTeleportPoint.DIMENSION_ID);
            worldServer.theChunkProviderServer.loadChunk(this.parentTeleportPoint.POS.getX() >> 4, this.parentTeleportPoint.POS.getZ() >> 4);
            TileEntity tileEntity = BlockHelper.getBlockTileEntity(worldServer, this.parentTeleportPoint.POS);
            if (tileEntity instanceof TileEntityRelocatorPortal)
                ((TileEntityRelocatorPortal) tileEntity).addEntityToList(paramEntity);
            double d = this.parentTeleportPoint.YAW;
            if (!this.entityList.isEmpty()) {
                for (EntityInfo entityInfo : this.entityList) {
                    if (entityInfo.entityID.equalsIgnoreCase(paramEntity.getPersistentID().toString()))
                        d = entityInfo.yaw;
                }
            }
            TeleportPoint teleportPoint = new TeleportPoint();
            teleportPoint.DIMENSION_ID = this.parentTeleportPoint.DIMENSION_ID;
            teleportPoint.POS = this.parentTeleportPoint.POS;
            teleportPoint.YAW = d;
            teleportPoint.PITCH = paramEntity.rotationPitch;
            Helpers.teleportEntity(paramEntity, teleportPoint);
        }
    }

    public void setParentPortal(TeleportPoint paramTeleportPoint) {
        if (this.parentTeleportPoint != null) {
            MinecraftServer minecraftServer = MinecraftServer.getServer();
            WorldServer worldServer = minecraftServer.worldServerForDimension(this.parentTeleportPoint.DIMENSION_ID);
            worldServer.theChunkProviderServer.loadChunk(this.parentTeleportPoint.POS.getX() >> 4, this.parentTeleportPoint.POS.getZ() >> 4);
            Block block = BlockHelper.getBlock(worldServer, this.parentTeleportPoint.POS);
            if (block == null)
                return;
            if (block instanceof BlockRelocatorPortal) {
                BlockHelper.setBlockToAir(worldServer, this.parentTeleportPoint.POS);
                BlockHelper.markBlockForRenderUpdate(worldServer, this.parentTeleportPoint.POS);
                BlockHelper.removeBlockTileEntity(worldServer, this.parentTeleportPoint.POS);
            }
        }
        this.parentTeleportPoint = paramTeleportPoint;
        resetTimer();
    }

    public void resetTimer() {
        this.timerTicks = 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound paramNBTTagCompound) {
        super.readFromNBT(paramNBTTagCompound);
        boolean bool = paramNBTTagCompound.getBoolean("parentPortal");
        if (bool) {
            if (this.parentTeleportPoint == null)
                this.parentTeleportPoint = new TeleportPoint();
            this.parentTeleportPoint.DIMENSION_ID = paramNBTTagCompound.getInteger("parentDimId");
            double x = paramNBTTagCompound.getDouble("parentPosX");
            double y = paramNBTTagCompound.getDouble("parentPosY");
            double z = paramNBTTagCompound.getDouble("parentPosZ");
            this.parentTeleportPoint.POS = new BlockPos(x, y, z);
        }
        this.timerTicks = paramNBTTagCompound.getInteger("timerTicks");
        NBTTagList nBTTagList = paramNBTTagCompound.getTagList("entityList");
        ArrayList<EntityInfo> arrayList = Lists.newArrayList();
        for (byte b = 0; b < nBTTagList.tagCount(); b++) {
            EntityInfo entityInfo = new EntityInfo();
            NBTTagCompound nBTTagCompound = (NBTTagCompound) nBTTagList.tagAt(b);
            entityInfo.entityID = nBTTagCompound.getString("entityID");
            entityInfo.yaw = nBTTagCompound.getDouble("entityYaw");
            entityInfo.portalCooldown = nBTTagCompound.getInteger("entityCoolDown");
            arrayList.add(entityInfo);
        }
        if (!arrayList.isEmpty()) {
            this.entityList.clear();
            this.entityList.addAll(arrayList);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound paramNBTTagCompound) {
        super.writeToNBT(paramNBTTagCompound);
        if (this.parentTeleportPoint != null) {
            paramNBTTagCompound.setBoolean("parentPortal", true);
            paramNBTTagCompound.setInteger("parentDimId", this.parentTeleportPoint.DIMENSION_ID);
            paramNBTTagCompound.setDouble("parentPosX", this.parentTeleportPoint.POS.getX());
            paramNBTTagCompound.setDouble("parentPosY", this.parentTeleportPoint.POS.getY());
            paramNBTTagCompound.setDouble("parentPosZ", this.parentTeleportPoint.POS.getZ());
        } else {
            paramNBTTagCompound.setBoolean("parentPortal", false);
        }
        paramNBTTagCompound.setInteger("timerTicks", this.timerTicks);
        NBTTagList nBTTagList = new NBTTagList();
        for (EntityInfo entityInfo : this.entityList) {
            NBTTagCompound nBTTagCompound = new NBTTagCompound();
            nBTTagCompound.setString("entityID", entityInfo.entityID);
            nBTTagCompound.setDouble("entityYaw", entityInfo.yaw);
            nBTTagCompound.setInteger("entityCoolDown", entityInfo.portalCooldown);
            nBTTagList.appendTag(nBTTagCompound);
        }
        paramNBTTagCompound.setTag("entityList", nBTTagList);
    }

    public static class EntityInfo {
        public String entityID;
        public double yaw;
        public int portalCooldown;
    }
}
