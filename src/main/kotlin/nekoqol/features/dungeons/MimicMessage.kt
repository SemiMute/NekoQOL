package nekoqol.features.dungeons

import net.minecraft.entity.monster.EntityZombie
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import nekoqol.NekoQOL.Companion.config
import nekoqol.NekoQOL.Companion.inDungeons
import nekoqol.NekoQOL.Companion.mc
import net.minecraft.entity.monster.EntityCreeper

class MimicMessage {

    private var mimicKilled = false

    @SubscribeEvent
    fun onEntityDeath(event: LivingDeathEvent) {
        if (!config.mimicKillMessage || !inDungeons || event.entity !is EntityZombie || mimicKilled) return
        val entity = event.entity as EntityZombie
        if (entity.isChild && (0..3).none { entity.getCurrentArmor(it) != null }) {
            mimicKilled = true
            mc.thePlayer.sendChatMessage("/pc ${config.mimicMessage}")
        }
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load?) {
        mimicKilled = false
    }
}
