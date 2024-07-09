package reforged.mods.gravisuite.keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;

public class GraviSuiteKeyboardClient extends GraviSuiteKeyboard {

    public static KeyBinding engine_toggle = new KeyBinding(Refs.KEY_TOGGLE_DESC, Keyboard.KEY_F);
    public static KeyBinding magnet_toggle = new KeyBinding(Refs.KEY_MAGNET_TOGGLE_DESC, Keyboard.KEY_G);

    private int lastKeyState = 0;

    public GraviSuiteKeyboardClient() {
        // Register Keybinds using 1.7.10 method
        Minecraft.getMinecraft().gameSettings.keyBindings = Helpers.add(Minecraft.getMinecraft().gameSettings.keyBindings, engine_toggle);
        Minecraft.getMinecraft().gameSettings.keyBindings = Helpers.add(Minecraft.getMinecraft().gameSettings.keyBindings, magnet_toggle);
    }

    @Override
    public void sendKeyUpdate() {
        int currentKeyState = (engine_toggle.pressed ? 1 : 0) << 0 | (magnet_toggle.pressed ? 1 : 0) << 1;
        if (currentKeyState != this.lastKeyState) {
            GraviSuite.NETWORK.sendKeyStateUpdate(currentKeyState);
            processKeyUpdate(Minecraft.getMinecraft().thePlayer, currentKeyState);
            this.lastKeyState = currentKeyState;
        }
    }
}
