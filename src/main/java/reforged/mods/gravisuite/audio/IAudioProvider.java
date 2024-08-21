package reforged.mods.gravisuite.audio;

import ic2.core.audio.AudioSource;
import net.minecraft.entity.player.EntityPlayer;

public interface IAudioProvider {
    AudioSource getAudio(EntityPlayer player);
}
