package reforged.mods.gravisuite.keyboard;

import mods.vintage.core.helpers.KeyHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.utils.Messages;

public class GraviSuiteKeyboardClient extends GraviSuiteKeyboard {

    public static KeyBinding engine_toggle = new KeyBinding(Messages.Translations.KEY_TOGGLE_DESC.getKey(), Keyboard.KEY_F);
    public static KeyBinding magnet_toggle = new KeyBinding(Messages.Translations.KEY_MAGNET_TOGGLE_DESC.getKey(), Keyboard.KEY_G);

    private int lastKeyState = 0;

    public GraviSuiteKeyboardClient() {
        KeyHelper.registerKeybindings(engine_toggle);
        KeyHelper.registerKeybindings(magnet_toggle);
    }

    @Override
    public void sendKeyUpdate() {
        int currentKeyState = (engine_toggle.pressed ? 1 : 0) | (magnet_toggle.pressed ? 1 : 0) << 1;
        if (currentKeyState != this.lastKeyState) {
            GraviSuite.network.sendKeyStateUpdate(currentKeyState);
            processKeyUpdate(Minecraft.getMinecraft().thePlayer, currentKeyState);
            this.lastKeyState = currentKeyState;
        }
    }
}
