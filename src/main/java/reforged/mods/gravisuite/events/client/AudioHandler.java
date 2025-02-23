package reforged.mods.gravisuite.events.client;

import cpw.mods.fml.common.TickType;
import mods.vintage.core.platform.events.tick.TickEvents;
import reforged.mods.gravisuite.audio.IAudioTicker;
import reforged.mods.gravisuite.proxy.ClientProxy;
import reforged.mods.gravisuite.utils.Refs;

import java.util.EnumSet;
import java.util.List;

public class AudioHandler extends TickEvents.PlayerTickEvent {

    public static final AudioHandler THIS = new AudioHandler();

    public AudioHandler() {
        super(Refs.id);
    }

    @Override
    public void tickEnd(EnumSet<TickType> enumSet, Object... objects) {
        if (shouldTick(enumSet)) {
            List<IAudioTicker> tickers = ClientProxy.TICKERS;
            for (IAudioTicker ticker : tickers) {
                ticker.onClientTick();
            }
        }
    }
}
