package reforged.mods.gravisuite.utils;

import ic2.core.IC2;
import ic2.core.util.KeyboardClient;
import mods.vintage.core.platform.lang.Translator;
import mods.vintage.core.platform.lang.component.MutableComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Keyboard;
import reforged.mods.gravisuite.keyboard.GraviSuiteKeyboardClient;

import java.lang.reflect.Field;

public final class KeyDescriptionHelper {

    public static String buildKeyDescription(Keys key, KeyMode mode, Object... args) {
        return buildKeyDescription(new Keys[]{key}, mode, args);
    }

    public static String buildKeyDescription(Keys key, Object... args) {
        return buildKeyDescription(new Keys[]{key}, KeyMode.NONE, args);
    }

    public static String buildKeyDescription(Keys key1, Keys key2, KeyMode mode, Object... args) {
        return buildKeyDescription(new Keys[]{key1, key2}, mode, args);
    }

    public static String buildKeyDescription(Keys key1, Keys key2, Object... args) {
        return buildKeyDescription(new Keys[]{key1, key2}, KeyMode.NONE, args);
    }

    public static String buildKeyDescription(Keys[] keys, KeyMode mode, Object... args) {
        Object[] fullArgs = concat(
                new Object[]{ combineKeys(keys) },
                new Object[] { Translator.YELLOW.literal(Translator.GRAY.format(mode.translationKey, args)) }
        );

        return Translator.RESET.format("message.key.description", fullArgs);
    }

    public static MutableComponent combineKeys(Keys... keys) {
        MutableComponent text = MutableComponent.empty();
        for (int i = 0; i < keys.length; i++) {
            if (i > 0) text.append(Translator.GRAY.format("message.key.joiner"));
            text.append(Translator.GOLD.literal(keys[i].getName()));
        }
        return text;
    }

    private static Object[] concat(Object[] first, Object[] second) {
        Object[] result = new Object[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public enum Keys {
        ALT_KEY(getPrivateKey("altKey", Keyboard.KEY_LMENU)),
        BOOST_KEY(getPrivateKey("boostKey", Keyboard.KEY_LCONTROL)),
        MODE_KEY(getPrivateKey("modeSwitchKey", Keyboard.KEY_M)),
        SIDE_INV_KEY(getPrivateKey("sideinventoryKey", Keyboard.KEY_C)),
        TOGGLE_KEY(GraviSuiteKeyboardClient.engine_toggle),
        MAGNET_TOGGLE_KEY(GraviSuiteKeyboardClient.magnet_toggle),
        JUMP_KEY(Minecraft.getMinecraft().gameSettings.keyBindJump),
        FORWARD_KEY(Minecraft.getMinecraft().gameSettings.keyBindForward),
        BACKWARD_KEY(Minecraft.getMinecraft().gameSettings.keyBindBack),
        SNEAK_KEY(Minecraft.getMinecraft().gameSettings.keyBindSneak),
        RIGHT_CLICK(Minecraft.getMinecraft().gameSettings.keyBindUseItem),
        BLOCK_LEFT_CLICK(Minecraft.getMinecraft().gameSettings.keyBindAttack);

        final KeyBinding binding;

        Keys(KeyBinding binding) {
            this.binding = binding;
        }

        public int getKeyCode() {
            return binding.keyCode;
        }

        public String getName() {
            int code = binding.keyCode;
            if (code == -100) {
                return "Left Click";
            } else if (code == -99) {
                return "Right Click";
            } else if (code == -98) {
                return "Middle Click";
            } else {

                String translation = Translator.RESET.format(binding.keyDescription);
                return StatCollector.func_94522_b(binding.keyDescription) ? translation : firstLetterUppercase(Keyboard.getKeyName(code));
            }
        }

        public boolean isDown() {
            return Keyboard.isKeyDown(binding.keyCode);
        }

        public static String firstLetterUppercase(String string) {
            if (string == null || string.isEmpty()) return string;
            return Character.toUpperCase(string.charAt(0)) + string.substring(1).toLowerCase();
        }

        public static KeyBinding getPrivateKey(String fieldName, int fallbackKeyCode) {
            try {
                Field f = KeyboardClient.class.getDeclaredField(fieldName);
                f.setAccessible(true);
                return (KeyBinding) f.get(IC2.keyboard);
            } catch (Exception e) {
                e.printStackTrace();
                return new KeyBinding("dummy_" + fieldName, fallbackKeyCode);
            }
        }
    }

    public enum KeyMode {
        NONE("message.key.to"),
        BLOCK("message.key.block"),
        CHANGE("message.key.change"),
        ENABLE("message.key.enable");

        final String translationKey;

        KeyMode(String key) {
            this.translationKey = key;
        }
    }
}

