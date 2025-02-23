package reforged.mods.gravisuite.audio.tickers;

import ic2.core.audio.AudioSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.audio.IAudioProvider;
import reforged.mods.gravisuite.audio.IAudioTicker;

public class ArmorAudioTicker implements IAudioTicker {

    EntityPlayer PLAYER;
    boolean USED;
    AudioSource AUDIO;

    public ArmorAudioTicker(EntityPlayer player) {
        this.PLAYER = player;
    }

    @Override
    public void onClientTick() {
        boolean used = GraviSuite.proxy.isFlying(this.PLAYER);
        ItemStack armorStack = PLAYER.getCurrentArmor(2);
        if (armorStack != null) {
            if (armorStack.getItem() instanceof IAudioProvider) {
                IAudioProvider audioProvider = (IAudioProvider) armorStack.getItem();
                if (this.USED != used) {
                    if (used) {
                        if (this.AUDIO == null) {
                            this.AUDIO = audioProvider.getAudio(this.PLAYER);
                        }
                        if (this.AUDIO != null) {
                            this.AUDIO.play();
                        }
                    } else if (this.AUDIO != null) {
                        this.AUDIO.remove();
                        this.AUDIO = null;
                    }
                    this.USED = used;
                }
                if (this.AUDIO != null) {
                    this.AUDIO.updatePosition();
                }
            } else {
                if (this.AUDIO != null) {
                    this.USED = used;
                    this.AUDIO.remove();;
                    this.AUDIO = null;
                }
            }
        } else {
            if (this.AUDIO != null) {
                this.AUDIO.remove();
                this.AUDIO = null;
            }
        }
    }
}
