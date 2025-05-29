package reforged.mods.gravisuite.utils;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeleporterDummy extends Teleporter {

    public TeleporterDummy(WorldServer worldServer) {
        super(worldServer);
    }

    @Override
    public void placeInPortal(Entity entity, double x, double y, double z, float yaw) {
        entity.setLocationAndAngles(x, y, z, yaw, entity.rotationPitch);
        entity.motionX = 0.0D;
        entity.motionY = 0.0D;
        entity.motionZ = 0.0D;
    }

    @Override
    public boolean placeInExistingPortal(Entity entity, double x, double y, double z, float yaw) {
        this.placeInPortal(entity, x, y, z, yaw);
        return true;
    }
}
