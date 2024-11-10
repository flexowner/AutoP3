package catgirlyharim.init.features

import catgirlyharim.init.CatgirlYharim
import catgirlyharim.init.CatgirlYharim.Companion.mc
import catgirlyharim.init.events.ReceivePacketEvent
import catgirlyharim.init.features.PearlClip.pearlclip
import catgirlyharim.init.utils.ClientListener.scheduleTask
import catgirlyharim.init.utils.ServerRotateUtils.resetRotations
import catgirlyharim.init.utils.ServerRotateUtils.set
import catgirlyharim.init.utils.Utils.airClick
import catgirlyharim.init.utils.Utils.modMessage
import catgirlyharim.init.utils.Utils.relativeClip
import catgirlyharim.init.utils.Utils.swapFromName
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object PearlClip{
    var active = false
    var distance = 40f
    @SubscribeEvent
    fun onPacket(event: ReceivePacketEvent) {
        if (!active || event.packet !is S08PacketPlayerPosLook) return
        scheduleTask(0) {
            relativeClip(0f, -distance, 0f)
            active = false
        }
    }

    fun pearlclip(distanceY: Float){
        distance = distanceY.toFloat()
        active = true
        swapFromName("Ender Pearl")
        set(mc.thePlayer.rotationYaw, 90f)
        scheduleTask(1) {
            airClick()
            resetRotations()
        }
    }
}

class PearlClipCommand : CommandBase() {
    override fun getCommandName(): String {
        return "pearlclip"
    }

    override fun getCommandAliases(): List<String> {
        return listOf(
         "ilikepearlclippingverymuch"
        )
    }

    override fun getCommandUsage(sender: ICommandSender?): String {
        return "/$commandName"
    }

    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

    override fun processCommand(sender: ICommandSender?, args: Array<out String>?) {
        pearlclip(args!![0].toFloat())
        modMessage("Clipping ${args[0].toFloat()} blocks down!")
    }
}

