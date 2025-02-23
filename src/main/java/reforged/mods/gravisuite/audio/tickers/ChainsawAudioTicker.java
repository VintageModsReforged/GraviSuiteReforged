package reforged.mods.gravisuite.audio.tickers;

import ic2.core.IC2;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import reforged.mods.gravisuite.audio.IAudioTicker;
import reforged.mods.gravisuite.items.tools.ItemAdvancedChainsaw;

public class ChainsawAudioTicker implements IAudioTicker {

    EntityPlayer PLAYER;
    byte USED = 0;
    AudioSource AUDIO;

    public ChainsawAudioTicker(EntityPlayer player) {
        this.PLAYER = player;
    }

    @Override
    public void onClientTick() {
        ItemStack heldStack = this.PLAYER.getHeldItem();
        if (USED == 0) {
            if (heldStack != null && heldStack.getItem() instanceof ItemAdvancedChainsaw) {
                this.USED = 1;
                if (this.AUDIO == null) {
                    this.AUDIO = IC2.audioManager.createSource(this.PLAYER, PositionSpec.Hand, "Tools/Chainsaw/ChainsawIdle.ogg", true, false, IC2.audioManager.defaultVolume);
                }
                if (this.AUDIO != null) {
                    this.AUDIO.play();
                }
            }
        } else {
            if (heldStack == null || !(heldStack.getItem() instanceof ItemAdvancedChainsaw)) {
                this.USED = 0;
                if (this.AUDIO != null) {
                    this.AUDIO.remove();
                    this.AUDIO = null;
                }
                IC2.audioManager.playOnce(this.PLAYER, PositionSpec.Hand, "Tools/Chainsaw/ChainsawStop.ogg", true, IC2.audioManager.defaultVolume);
            }
        }
    }
}
