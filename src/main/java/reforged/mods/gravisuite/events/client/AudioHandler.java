package reforged.mods.gravisuite.events.client;

import cpw.mods.fml.common.TickType;
import ic2.core.audio.AudioSource;
import mods.vintage.core.platform.events.tick.TickEvents;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.audio.IAudioProvider;
import reforged.mods.gravisuite.utils.Refs;

import java.util.EnumSet;

public class AudioHandler extends TickEvents.PlayerTickEvent {

    public static final AudioHandler THIS = new AudioHandler();

    boolean USED;
    AudioSource AUDIO_SOURCE;

    public AudioHandler() {
        super(Refs.ID);
    }

    @Override
    public void tickStart(EnumSet<TickType> enumSet, Object... objects) {
        if (shouldTick(enumSet)) {
            EntityPlayer player = (EntityPlayer) objects[0];
            if (player != null) {
                boolean used = GraviSuite.PROXY.isFlying(player);
                ItemStack armorStack = player.getCurrentArmor(2);
                if (armorStack != null) {
                    if (armorStack.getItem() instanceof IAudioProvider) {
                        IAudioProvider provider = (IAudioProvider) armorStack.getItem();
                        if (USED != used) {
                            if (used) {
                                if (this.AUDIO_SOURCE == null) {
                                    this.AUDIO_SOURCE = provider.getAudio(player);
                                }
                                if (this.AUDIO_SOURCE != null) {
                                    this.AUDIO_SOURCE.play();
                                }
                            } else if (this.AUDIO_SOURCE != null) {
                                this.AUDIO_SOURCE.remove();
                                this.AUDIO_SOURCE = null;
                            }
                            USED = used;
                        }
                        if (this.AUDIO_SOURCE != null) {
                            this.AUDIO_SOURCE.updatePosition();
                        }
                    } else {
                        if (this.AUDIO_SOURCE != null) {
                            USED = used;
                            this.AUDIO_SOURCE.remove();
                            this.AUDIO_SOURCE = null;
                        }
                    }
                } else {
                    if (this.AUDIO_SOURCE != null) {
                        this.AUDIO_SOURCE.remove();
                        this.AUDIO_SOURCE = null;
                    }
                }
            }
        }
    }
}
