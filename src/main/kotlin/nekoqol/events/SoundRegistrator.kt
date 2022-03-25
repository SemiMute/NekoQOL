package nekoqol.events

import net.minecraft.client.audio.SoundManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.sound.SoundEvent


class SoundRegistratorListener {
        var NYAA: SoundEvent? = null
        private fun addSoundsToRegistry(soundId: String): SoundEvent {
            val shotSoundLocation = ResourceLocation("nekoqol", soundId) as SoundManager
            val soundEvent = SoundEvent(shotSoundLocation)
            soundEvent.registryName(shotSoundLocation)
            return soundEvent
        }

        init {
            NYAA = addSoundsToRegistry("nyaa")
        }
}