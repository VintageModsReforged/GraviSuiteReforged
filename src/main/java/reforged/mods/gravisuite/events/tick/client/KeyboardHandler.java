package reforged.mods.gravisuite.events.tick.client;

import cpw.mods.fml.common.TickType;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.events.tick.base.TickEvents;

import java.util.EnumSet;

public class KeyboardHandler extends TickEvents.ClientTickEvent {

    public static final KeyboardHandler THIS = new KeyboardHandler();

    @Override
    public void tickStart(EnumSet<TickType> enumSet, Object... objects) {
        if (shouldTick(enumSet)) {
            GraviSuite.keyboard.sendKeyUpdate();
        }
    }
}
