package reforged.mods.gravisuite.utils;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import reforged.mods.gravisuite.items.tools.relocator.gui.GuiRelocatorAdd;
import reforged.mods.gravisuite.items.tools.relocator.gui.GuiRelocatorMain;

public class GraviSuiteGuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
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
        }
        return null;
    }
}
