package nekoqol.features.qol

import gg.essential.elementa.svg.data.Rotation
import ibxm.Player
import nekoqol.NekoQOL.Companion.mc
import nekoqol.utils.Utils
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.GuiIngameMenu
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.Vec3
import kotlin.math.cos
import kotlin.math.sin

import net.minecraft.util.*
import kotlin.math.atan2
import kotlin.math.sqrt

class SpiralMacro {

}
object Helper {
    var working = false

    fun facePos(blockPos: BlockPos) {
        facePos(Vec3(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble()))
    }

    fun facePos(vec: Vec3) {
        if(mc.currentScreen == null || mc.currentScreen is GuiIngameMenu || mc.currentScreen is GuiChat) {
            if(!working) {
                Thread {
                    try {
                        working = true

                        val diffX = vec.xCoord - mc.thePlayer.posX
                        val diffY = vec.yCoord - mc.thePlayer.posY
                        val diffZ = vec.zCoord - mc.thePlayer.posZ

                        val dist = sqrt(diffX * diffX + diffZ * diffZ)
                        var pitch = -atan2(dist, diffY)
                        var yaw = atan2(diffZ, diffX)

                        pitch = to180(((pitch * 180.0) / Math.PI + 90.0) * - 1.0 - mc.thePlayer.rotationPitch)
                        yaw = to180((yaw * 180.0) / Math.PI - 90.0 - mc.thePlayer.rotationYaw)

                        for (i in 0 until  50) {
                            mc.thePlayer.rotationYaw += (yaw / 50.0).toFloat()
                            mc.thePlayer.rotationPitch += (pitch / 50.0).toFloat()
                            Thread.sleep(10)
                        }
                        working = false
                    } catch (e : Exception) {
                        return@Thread
                    }
                }.start()
            }
        }
    }

    fun to180(a: Double): Double {
        var angle = a
        angle %= 360.0
        while(angle > 180.0) {
            angle -= 360.0
        }
        while(angle < -180.0) {
            angle += 360.0
        }
        return angle
    }

}