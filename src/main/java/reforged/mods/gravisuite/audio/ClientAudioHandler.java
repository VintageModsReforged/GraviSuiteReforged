package reforged.mods.gravisuite.audio;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import ic2.core.audio.AudioSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.utils.Refs;

import java.util.EnumSet;

public class ClientAudioHandler implements ITickHandler {

    boolean USED;
    AudioSource AUDIO_SOURCE;

    @Override
    public void tickStart(EnumSet<TickType> enumSet, Object... objects) {
        if (enumSet.contains(TickType.PLAYER)) {
            EntityPlayer player = (EntityPlayer) objects[0];
            if (player != null) {
                boolean used = GraviSuite.proxy.isFlying(player);
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

    @Override
    public void tickEnd(EnumSet<TickType> enumSet, Object... objects) {}

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.PLAYER);
    }

    @Override
    public String getLabel() {
        return Refs.id;
    }
}
