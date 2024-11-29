package reforged.mods.gravisuite.events.client;

import cpw.mods.fml.common.TickType;
import mods.vintage.core.platform.events.tick.TickEvents;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.utils.Refs;

import java.util.EnumSet;

public class KeyboardHandler extends TickEvents.ClientTickEvent {

    public static final KeyboardHandler THIS = new KeyboardHandler();

    public KeyboardHandler() {
        super(Refs.id);
    }

    @Override
    public void tickStart(EnumSet<TickType> enumSet, Object... objects) {
        if (shouldTick(enumSet)) {
            GraviSuite.keyboard.sendKeyUpdate();
        }
    }
}
