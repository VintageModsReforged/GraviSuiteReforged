package reforged.mods.gravisuite.events.tick;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import reforged.mods.gravisuite.utils.Refs;

import java.util.EnumSet;

public class BaseTickEvent implements ITickHandler {

    TickType TYPE;

    public BaseTickEvent(TickType type) {
        this.TYPE = type;
    }

    @Override
    public void tickStart(EnumSet<TickType> enumSet, Object... objects) {}

    @Override
    public void tickEnd(EnumSet<TickType> enumSet, Object... objects) {}

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(this.TYPE);
    }

    @Override
    public String getLabel() {
        return Refs.ID;
    }

    public boolean shouldTick(EnumSet<TickType> type) {
        return type.contains(this.TYPE);
    }
}
