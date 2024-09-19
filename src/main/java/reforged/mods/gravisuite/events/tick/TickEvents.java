package reforged.mods.gravisuite.events.tick;

import cpw.mods.fml.common.TickType;

public class TickEvents {

    public static class WorldTickEvent extends BaseTickEvent {
        public WorldTickEvent() {
            super(TickType.WORLD);
        }
    }

    public static class RenderTickEvent extends BaseTickEvent {
        public RenderTickEvent() {
            super(TickType.RENDER);
        }
    }

    public static class WorldLoadTickEvent extends BaseTickEvent {
        public WorldLoadTickEvent() {
            super(TickType.WORLDLOAD);
        }
    }

    public static class ClientTickEvent extends BaseTickEvent {
        public ClientTickEvent() {
            super(TickType.CLIENT);
        }
    }

    public static class ServerTickEvent extends BaseTickEvent {
        public ServerTickEvent() {
            super(TickType.SERVER);
        }
    }

    public static class PlayerTickEvent extends BaseTickEvent {
        public PlayerTickEvent() {
            super(TickType.PLAYER);
        }
    }
}
