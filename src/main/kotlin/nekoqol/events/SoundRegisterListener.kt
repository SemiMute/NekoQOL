package nekoqol.events

import akka.io.Tcp
import net.minecraftforge.client.event.sound.SoundEvent

import net.minecraftforge.fml.common.eventhandler.EventPriority

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent




class SoundRegisterListener {
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    fun registerSoundEvents(event: SoundEvent) {
        //event.getRegistry().registerAll(SoundRegistrator.SOUND_1, SoundRegistrator.SOUND_2)
    }
}