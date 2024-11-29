package reforged.mods.gravisuite.keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.vintage.core.helpers.KeyHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;

@SideOnly(Side.CLIENT)
public class GraviSuiteKeyboardClient extends GraviSuiteKeyboard {

    public static KeyBinding engine_toggle = new KeyBinding(Refs.KEY_TOGGLE_DESC, Keyboard.KEY_F);
    public static KeyBinding magnet_toggle = new KeyBinding(Refs.KEY_MAGNET_TOGGLE_DESC, Keyboard.KEY_G);

    private int lastKeyState = 0;

    public GraviSuiteKeyboardClient() {
        KeyHelper.registerKeybindings(engine_toggle);
        KeyHelper.registerKeybindings(magnet_toggle);
    }

    @Override
    public void sendKeyUpdate() {
        int currentKeyState = (engine_toggle.pressed ? 1 : 0) << 0 | (magnet_toggle.pressed ? 1 : 0) << 1;
        if (currentKeyState != this.lastKeyState) {
            GraviSuite.network.sendKeyStateUpdate(currentKeyState);
            processKeyUpdate(Minecraft.getMinecraft().thePlayer, currentKeyState);
            this.lastKeyState = currentKeyState;
        }
    }
}
