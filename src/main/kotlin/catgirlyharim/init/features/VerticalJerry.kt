package catgirlyharim.init.features

import catgirlyharim.init.CatgirlYharim.Companion.config
import catgirlyharim.init.CatgirlYharim.Companion.mc
import catgirlyharim.init.events.ReceivePacketEvent
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object VerticalJerry {
    @SubscribeEvent
    fun onPacket(event: ReceivePacketEvent) {
        if (!config!!.jerry) return
        if (event.packet !is S12PacketEntityVelocity || event.packet.entityID != mc.thePlayer.entityId) return
        if (event.packet.motionY == 4800) {
            event.isCanceled = true
            mc.thePlayer.setVelocity(mc.thePlayer.motionX, 0.6, mc.thePlayer.motionZ)
        }
    }
}