package reforged.mods.gravisuite.events.client;

import cpw.mods.fml.common.TickType;
import mods.vintage.core.platform.events.tick.TickEvents;
import net.minecraft.entity.player.EntityPlayer;
import reforged.mods.gravisuite.audio.IAudioTicker;
import reforged.mods.gravisuite.audio.tickers.ArmorAudioTicker;
import reforged.mods.gravisuite.audio.tickers.ChainsawAudioTicker;
import reforged.mods.gravisuite.utils.Refs;

import java.util.*;

public class AudioHandler extends TickEvents.PlayerTickEvent {

    public static final AudioHandler THIS = new AudioHandler();
    private final Map<String, List<IAudioTicker>> playerTickers = new HashMap<String, List<IAudioTicker>>();

    public AudioHandler() {
        super(Refs.ID);
    }

    @Override
    public void tickStart(EnumSet<TickType> enumSet, Object... objects) {
        if (shouldTick(enumSet)) {
            EntityPlayer player = (EntityPlayer) objects[0];
            if (player != null) {
                String playerName = player.username;

                if (!playerTickers.containsKey(playerName)) {
                    playerTickers.put(playerName, new ArrayList<IAudioTicker>());
                }

                List<IAudioTicker> tickers = playerTickers.get(playerName);

                addTickerIfAbsent(tickers, new ChainsawAudioTicker(player));
                addTickerIfAbsent(tickers, new ArmorAudioTicker(player));
            }
        }
    }

    @Override
    public void tickEnd(EnumSet<TickType> enumSet, Object... objects) {
        if (shouldTick(enumSet)) {
            EntityPlayer player = (EntityPlayer) objects[0];
            if (player != null) {
                List<IAudioTicker> tickers = this.playerTickers.get(player.username);
                if (tickers != null && !tickers.isEmpty()) {
                    for (IAudioTicker ticker : tickers) {
                        ticker.onClientTick();
                    }
                }
            }
        }
    }

    private void addTickerIfAbsent(List<IAudioTicker> tickers, IAudioTicker ticker) {
        boolean hasTicker = false;
        for (IAudioTicker existingTicker : tickers) {
            if (existingTicker.getClass().equals(ticker.getClass())) {
                hasTicker = true;
                break;
            }
        }
        if (!hasTicker) {
            tickers.add(ticker);
        }
    }

    public Map<String, List<IAudioTicker>> getPlayerTickers() {
        return this.playerTickers;
    }
}
