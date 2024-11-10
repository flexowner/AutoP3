package catgirlyharim.init.features

import catgirlyharim.init.CatgirlYharim.Companion.config
import catgirlyharim.init.events.PacketSentEvent
import catgirlyharim.init.utils.ClientListener.scheduleTask
import catgirlyharim.init.utils.Utils.relativeClip
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object StormClip{
    var clipped = false
    @SubscribeEvent
    fun onPacket(event: PacketSentEvent) {
        if (!config!!.stormClipActive || event.packet !is S08PacketPlayerPosLook || clipped) return
        if (event.packet.x == 73.5 && event.packet.y == 221.5 && event.packet.z == 14.5) {
            clipped = true
            scheduleTask(1) {relativeClip(0f, config!!.stormClipDistance, 0f)}
            scheduleTask(19) {clipped = false}
        }
    }
}