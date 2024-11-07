package catgirlyharim.features

import catgirlyharim.CatgirlYharim.Companion.mc
import catgirlyharim.config.MyConfig.editmode
import catgirlyharim.config.MyConfig.selectedRoute
import catgirlyharim.features.AutoP3.inp3
import catgirlyharim.features.AutoP3.rings
import catgirlyharim.utils.ClientListener.scheduleTask
import catgirlyharim.utils.Hclip.hclip
import catgirlyharim.utils.MovementUtils.jump
import catgirlyharim.utils.MovementUtils.stopMovement
import catgirlyharim.utils.MovementUtils.stopVelo
import catgirlyharim.utils.ServerRotateUtils.resetRotations
import catgirlyharim.utils.ServerRotateUtils.set
import catgirlyharim.utils.Utils.airClick
import catgirlyharim.utils.Utils.distanceToPlayer
import catgirlyharim.utils.Utils.getYawAndPitch
import catgirlyharim.utils.Utils.leftClick
import catgirlyharim.utils.Utils.modMessage
import catgirlyharim.utils.Utils.rotate
import catgirlyharim.utils.Utils.sendChat
import catgirlyharim.utils.Utils.swapFromName
import catgirlyharim.utils.WorldRenderUtils.drawSquareTwo
import catgirlyharim.utils.edgeJump.toggleEdging
import catgirlyharim.utils.lavaClip.toggleLavaClip
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatStyle
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color.*

import java.lang.Float.parseFloat
import kotlin.math.abs

data class PushParams(
    var type: String = "",
    var active: Boolean = true,
    var route: String = "",
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f,
    var yaw: Float = 0f,
    var pitch: Float = 0f,
    var height: Float = 1f,
    var width: Float = 1f,
    var lookX: Float = 0f,
    var lookY: Float = 0f,
    var lookZ: Float = 0f,
    var depth: Float = 0f,
    var stopping: Boolean = false,
    var looking: Boolean = false,
    var walking: Boolean = false,
    var silent: Boolean = false,
    var clientSide: Boolean = false,
    var delaying: Boolean = false,
    var delay: Int = 0,
    var commanding: Boolean = false,
    var cmd: String? = null
)

object AutoP3 {
    var inp3 = false
    var cooldowm = false
    var rings = mutableListOf<PushParams>()

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        val message = event.message.unformattedText
        if (message.contains("[BOSS] Storm: I should have known that I stood no chance.")) {
            inp3 = true
            //modMessage("P3 started!")
        }
        if (message.contains("[BOSS] Goldor: You have done it, you destroyed the factory…")) { // Change to necron death
            inp3 = false
            //modMessage("P3 ended!")
        }
    }

    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        if (!inp3) return
        rings.forEach { ring ->
            if (ring.route != selectedRoute) return
            val color = when (ring.type) {
                "look" -> pink
                "stop" -> red
                "boom" -> cyan
                "jump" -> gray
                "hclip" -> black
                "bonzo" -> white
                "vclip" -> yellow
                "block" -> blue
                "edge" -> pink
                else -> black
            }
            if (ring.active) {
                drawSquareTwo(ring.x.toDouble(), ring.y.toDouble(), ring.z.toDouble(), ring.width.toDouble(), ring.width.toDouble(), color, 3f, false, true)
                drawSquareTwo(ring.x.toDouble(), ring.y.toDouble() + ring.height / 2, ring.z.toDouble(), ring.width.toDouble(), ring.width.toDouble(), color, 3f, false, true)
                drawSquareTwo(ring.x.toDouble(), ring.y.toDouble() + ring.height, ring.z.toDouble(), ring.width.toDouble(), ring.width.toDouble(), color, 3f, false, true)

            }

            val playerX = mc.renderManager.viewerPosX
            val playerY = mc.renderManager.viewerPosY
            val playerZ = mc.renderManager.viewerPosZ
            val distanceX = abs(playerX - ring.x)
            val distanceY = (playerY - ring.y)
            val distanceZ = abs(playerZ - ring.z)
            if ((distanceX > (ring.width / 2) || (distanceY >= (ring.height - 0.5) || distanceY < 0) || distanceZ > (ring.width / 2))) {
                ring.active = true
            } else if (ring.active && !editmode){
                ring.active = false
                when (ring.type) {
                    "look" -> rotate(ring.yaw, ring.pitch)
                    "stop" -> {
                        stopVelo()
                        stopMovement()
                    }
                    "boom" -> {
                        rotate(ring.yaw, ring.pitch)
                        swapFromName("boom")
                        if(ring.delaying) {
                            scheduleTask(ring.delay) { leftClick()}
                        } else {
                            scheduleTask(4) { leftClick()}
                        }
                    }
                    "jump" -> jump()
                    "hclip" -> hclip(ring.yaw)
                    "bonzo" -> {
                        swapFromName("bonzo")
                        if (ring.silent) {
                            set(ring.yaw, ring.pitch)
                        } else {
                            rotate(ring.yaw, ring.pitch)
                        }
                        if (ring.delaying) {
                            scheduleTask(ring.delay) {
                                airClick()
                                resetRotations()
                            }
                            } else {
                            scheduleTask(1) {
                                airClick()
                                resetRotations()
                            }
                        }
                    }
                    "vclip" -> toggleLavaClip(ring.depth)
                    "block" -> {
                        var (yaw, pitch) = getYawAndPitch(ring.lookX, ring.lookY, ring.lookZ)
                        rotate(yaw, pitch)
                    }
                    "edge" -> toggleEdging()
                    else -> sendChat("1")
                }
            }
        }
    }
}

object P3Command : CommandBase() {
    override fun getCommandName(): String {
        return "p3"
    }

    override fun getCommandAliases(): List<String> {
        return listOf()
    }

    override fun getCommandUsage(sender: ICommandSender?): String {
        return "/$commandName"
    }

    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

    override fun processCommand(sender: ICommandSender?, args: Array<String>) {
        if (args.isEmpty()) {
            modMessage("No argument specified!")
            return
        }

        when (args[0]) {
            "add" -> {
                val type = args[1]
                val active = true
                val route = selectedRoute
                val x = Math.round(mc.renderManager.viewerPosX * 2) / 2.0
                val y = Math.round(mc.renderManager.viewerPosY * 2) / 2.0
                val z = Math.round(mc.renderManager.viewerPosZ * 2) / 2.0
                val yaw = mc.renderManager.playerViewY
                val pitch = mc.renderManager.playerViewX
                var lookX = 0f
                var lookY = 0f
                var lookZ = 0f
                if (args.size > 3 && type == "block") {
                    lookX = parseFloat(args[2])
                    lookY = parseFloat(args[3])
                    lookZ = parseFloat(args[4])
                }
                var depth = 0f
                if (type == "vclip" && args.size > 2) {
                    depth = parseFloat(args[2])
                }
                val h = 1f
                val w = 1f
                var toPush = PushParams(type = type, active = active, route = route, x = x.toFloat(), y = y.toFloat(), z = z.toFloat(), height = h, width = w, yaw = yaw, pitch = pitch)
                if (type == "block") {
                    toPush.lookX = lookX
                    toPush.lookY = lookY
                    toPush.lookZ = lookZ
                }
                if (type == "vclip") {
                    toPush.depth = depth
                }

                args.drop(2).forEachIndexed { index, arg ->
                    when {
                        arg.startsWith("h") -> toPush.height = arg.slice(1 until arg.length).toFloat()
                        arg.startsWith("w") && arg != "walk" -> toPush.width = arg.slice(1 until arg.length).toFloat()
                        arg == "stop" -> toPush.stopping = true
                        arg == "look" -> toPush.looking = true
                        arg == "walk" -> toPush.walking = true
                        arg == "silent" -> toPush.silent = true
                        /*arg == "client" -> toPush.clientSide = true*/
                        arg.startsWith("delay:") -> {
                            val delay = arg.slice(6 until arg.length).toInt()
                            toPush.delaying = true
                            toPush.delay = delay
                        }
                        /*arg.startsWith("cmd:") -> {
                            val cmd = args.drop(index + 2).joinToString(" ").slice(4 until args.joinToString(" ").length)
                            toPush.commanding = true
                            toPush.cmd = cmd
                        }*/
                    }
                }
                rings.add(toPush)
            }
            "edit" -> {
                if (editmode) {
                    editmode = false
                    modMessage("Editmode off!")
                } else {
                    editmode = true
                    modMessage("Editmode on!")
                }
            }
            "start" -> {
                inp3 = true
                modMessage("P3 started!")
            }
            "stop" -> {
                inp3 = false
                modMessage("P3 stopped!")
            }
            "remove" -> {
                val range = args.getOrNull(1)?.toDoubleOrNull() ?: 2.0 // Default range to 2 if not provided
                AutoP3.rings = rings.filter { ring ->
                    // Filter rings based on the route and distance criteria
                    if (ring.route != selectedRoute) return@filter true
                    val distance = distanceToPlayer(ring.x, ring.y, ring.z)
                    distance >= range
                }.toMutableList()
            }
            "undo" -> {
                AutoP3.rings.removeLast()
            }
            "clear" -> {
                val prefix: String = "§0[§6Yharim§0] §8»§r "
                sender?.addChatMessage(ChatComponentText("$prefix Are you sure?")
                    .apply {
                        chatStyle = ChatStyle().apply {
                            chatClickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/p3 clearconfirm")
                            chatHoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText("$prefix Click to clear ALL routes!"))
                        }
                    })
            }
            "clearconfirm" -> {
                AutoP3.rings = mutableListOf<PushParams>()
                modMessage("Cleared route")
            }
            "load" -> {
                val route = args[1]
                selectedRoute = route
                modMessage("Loaded route $route")
            }
        }
    }

}

