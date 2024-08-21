package reforged.mods.gravisuite.keyboard;

import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.Map;

public class GraviSuiteKeyboard {

    private final Map<EntityPlayer, Boolean> engineToggleKeyState = new HashMap<EntityPlayer, Boolean>();
    private final Map<EntityPlayer, Boolean> magnetToggleKeyState = new HashMap<EntityPlayer, Boolean>();

    public boolean isEngineToggleKeyDown(EntityPlayer player) {
        return engineToggleKeyState.containsKey(player) ? engineToggleKeyState.get(player) : false;
    }

    public boolean isMagnetToggleKeyDown(EntityPlayer player) {
        return magnetToggleKeyState.containsKey(player) ? magnetToggleKeyState.get(player) : false;
    }

    public void sendKeyUpdate() {}

    public void processKeyUpdate(EntityPlayer player, int keyState) {
        this.engineToggleKeyState.put(player, (keyState & 1) != 0);
        this.magnetToggleKeyState.put(player, (keyState & 2) != 0);
    }
}
