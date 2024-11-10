package catgirlyharim.init.features

import catgirlyharim.init.CatgirlYharim.Companion.mc
import catgirlyharim.init.events.ReceivePacketEvent
import catgirlyharim.init.utils.ClientListener.scheduleTask
import catgirlyharim.init.utils.Utils.clickSlot
import catgirlyharim.init.utils.WorldRenderUtils.renderText
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.network.play.client.C0DPacketCloseWindow
import net.minecraft.network.play.server.S2DPacketOpenWindow
import net.minecraft.network.play.server.S2EPacketCloseWindow
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object petKeyBinds {
    var cwid = -1
    var petCooldown = false
    var awaitingPet = false
    var index = 10

    @SubscribeEvent
    fun onPacket(event: ReceivePacketEvent) {
        if (event.packet !is S2DPacketOpenWindow || !awaitingPet) return
        if (!event.packet.windowTitle.formattedText.contains("Pet")) return
        awaitingPet = false
        cwid = event.packet.windowId
        event.isCanceled = true
        clickSlot(index, cwid)
    }

    fun equipPet(index: Int) {
        if (!petCooldown) return
        if (index < 10 || index > 54) return
        petKeyBinds.index = index
        awaitingPet = true
        petCooldown = true
        scheduleTask(9) {petCooldown = false}
    }

    @SubscribeEvent
    fun renderPet(event: RenderGameOverlayEvent) {
        if (!awaitingPet) return
        val sr = ScaledResolution(mc)
        val text = "§0[§6Yharim§0] §8»§r Equipping pet!"
        val width = sr.scaledWidth / 2 - mc.fontRendererObj.getStringWidth(text) / 2
        renderText(
            text = text,
            x = width,
            y = sr.scaledHeight / 2 + 10
        )
    }

    @SubscribeEvent
    fun onPackets(event: ReceivePacketEvent) {
        if (event.packet is S2EPacketCloseWindow || event.packet is C0DPacketCloseWindow) {
            cwid = -1
        }
    }
}
