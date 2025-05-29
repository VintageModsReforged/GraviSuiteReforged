package reforged.mods.gravisuite.utils;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import reforged.mods.gravisuite.client.gui.GuiRelocatorAdd;
import reforged.mods.gravisuite.client.gui.GuiRelocatorMain;
import reforged.mods.gravisuite.client.gui.GuiVoider;

public class GraviSuiteGuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == 4) {
            return new VoiderContainer(player);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch (id) {
            case 1:
                return new GuiRelocatorAdd();
            case 2:
                return new GuiRelocatorMain(0);
            case 3:
                return new GuiRelocatorMain(1);
            case 4:
                return new GuiVoider(player);
        }
        return null;
    }
}
