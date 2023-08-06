package universalelectricity.prefab.implement;

import net.minecraft.entity.player.EntityPlayer;

public interface IToolConfigurator {
    boolean canWrench(EntityPlayer var1, int var2, int var3, int var4);

    void wrenchUsed(EntityPlayer var1, int var2, int var3, int var4);
}
