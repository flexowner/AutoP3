package catgirlyharim.init.features

import catgirlyharim.init.CatgirlYharim.Companion.mc
import catgirlyharim.init.events.ReceivePacketEvent
import catgirlyharim.init.utils.ClientListener.scheduleTask
import catgirlyharim.init.utils.Utils.clickSlot
import catgirlyharim.init.utils.Utils.modMessage
import net.minecraft.item.Item
import net.minecraft.network.play.client.C0DPacketCloseWindow
import net.minecraft.network.play.server.S2DPacketOpenWindow
import net.minecraft.network.play.server.S2EPacketCloseWindow
import net.minecraft.network.play.server.S2FPacketSetSlot
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/* Need to make it work :3
object AutoMask{
    var items = arrayOfNulls<Item>(100)
    var cwid = -1
    var itemname = "Stone"
    var inGui = false
    var slotCount = 0
    var active = false

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        val message = event.message.unformattedText
        if (message.contains("test")) {
            active = !active
            modMessage(active)
        }
    }

    @SubscribeEvent
    fun onS2D(event: ReceivePacketEvent) {
        if (!active) return
        if (event.packet !is S2DPacketOpenWindow) return
        //if (!event.packet.windowTitle.unformattedText.contains("Your Equipment and Stats")) return
        //event.isCanceled = true
        cwid = event.packet.windowId
        slotCount = event.packet.slotCount
    }

    @SubscribeEvent
    fun onS2F(event: ReceivePacketEvent) {
        if (!active) return
        if (event.packet !is S2FPacketSetSlot) return
        var itemStack = event.packet.func_149174_e()
        val slot = event.packet.func_149173_d()
        val item: Item = itemStack.item
        items[slot] = item
        if(items.size != slotCount + 36) return
        val index = items.indexOfFirst { it?.unlocalizedName?.contains(itemname) == true }
        modMessage(index)
        clickSlot(index, cwid)
        active = false
        scheduleTask(4) {
            mc.netHandler.networkManager.sendPacket(C0DPacketCloseWindow())
        }
    }

    @SubscribeEvent
    fun onClose(event: ReceivePacketEvent) {
        if (event.packet is C0DPacketCloseWindow || event.packet is S2EPacketCloseWindow) {
            cwid = -1
        }
    }
}*/