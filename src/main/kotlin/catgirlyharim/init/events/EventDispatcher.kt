package catgirlyharim.init.events

import catgirlyharim.init.utils.Utils.postAndCatch
import net.minecraft.network.play.server.S2DPacketOpenWindow
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object EventDispatcher {

    val termNames = listOf(
        Regex("^Click in order!$"),
        Regex("^Select all the (.+?) items!$"),
        Regex("^What starts with: '(.+?)'\\?$"),
        Regex("^Change all to same color!$"),
        Regex("^Correct all the panes!$"),
        Regex("^Click the button on time!$")
    )

    @SubscribeEvent
    fun onS2D(event: ReceivePacketEvent) = with(event.packet) {
        if (event.packet !is S2DPacketOpenWindow) return
        val title = event.packet.windowTitle.unformattedText
        if (termNames.any{regex -> regex.matches(title)}) {
            TermOpenEvent.open(event.packet).postAndCatch()
        }
    }
}