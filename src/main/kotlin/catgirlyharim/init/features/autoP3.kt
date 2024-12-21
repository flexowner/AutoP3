package catgirlyharim.init.features

import catgirlyharim.init.CatgirlYharim.Companion.config
import catgirlyharim.init.CatgirlYharim.Companion.mc
import catgirlyharim.init.events.ReceivePacketEvent
import catgirlyharim.init.features.AutoP3.inp3
import catgirlyharim.init.features.RingManager.allrings
import catgirlyharim.init.features.RingManager.loadRings
import catgirlyharim.init.features.RingManager.rings
import catgirlyharim.init.features.RingManager.saveRings
import catgirlyharim.init.utils.ClientListener.scheduleTask
import catgirlyharim.init.utils.Hclip.hclip
import catgirlyharim.init.utils.MovementUtils.jump
import catgirlyharim.init.utils.MovementUtils.stopMovement
import catgirlyharim.init.utils.MovementUtils.stopVelo
import catgirlyharim.init.utils.MovementUtils.walk
import catgirlyharim.init.utils.ServerRotateUtils.resetRotations
import catgirlyharim.init.utils.ServerRotateUtils.set
import catgirlyharim.init.utils.Utils.airClick
import catgirlyharim.init.utils.Utils.distanceToPlayer
import catgirlyharim.init.utils.Utils.getYawAndPitch
import catgirlyharim.init.utils.Utils.hexToColor
import catgirlyharim.init.utils.Utils.leftClick
import catgirlyharim.init.utils.Utils.modMessage
import catgirlyharim.init.utils.Utils.rotate
import catgirlyharim.init.utils.Utils.sendChat
import catgirlyharim.init.utils.Utils.swapFromName
import catgirlyharim.init.utils.WorldRenderUtils.drawSquareTwo
import catgirlyharim.init.utils.edgeJump.toggleEdging
import catgirlyharim.init.utils.lavaClip.toggleLavaClip
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.network.play.server.S2DPacketOpenWindow
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatStyle
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color
import java.awt.Color.*
import java.io.File

import java.lang.Float.parseFloat
import kotlin.math.abs

object AutoP3 {
    var inp3 = false
    var cooldown = false
    var walkOnTermOpen = false

    @SubscribeEvent
    fun onLoad(event: WorldEvent.Unload) {
        inp3 = false
    }

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        val message = event.message.unformattedText
        if (message.contains("[BOSS] Maxor")) {
            if (config!!.onBossStart) config!!.selectedRoute = config!!.BossStartRoute
            loadRings()
            inp3 = true
        }
        if (message.contains("[BOSS] Storm: I should have known that I stood no chance.")) {
            inp3 = true
            config!!.autoP3Active = true
            if (config!!.onP3Start) config!!.selectedRoute = config!!.P3StartRoute
            loadRings()
        }
        if (message.contains("[BOSS] Necron: All this, for nothing...")) {
            inp3 = false
        }
    }

    @SubscribeEvent
    fun onRenderRing(event: RenderWorldLastEvent) {
        if (!config!!.autoP3Active || !inp3) return
        rings.forEach{ring ->
            if (!ring.active) return@forEach
            val color = when (ring.type) {
                    "look" -> config!!.lookColor.toJavaColor()
                    "stop" -> config!!.stopColor.toJavaColor()
                    "boom" -> config!!.boomColor.toJavaColor()
                    "jump" -> config!!.jumpColor.toJavaColor()
                    "hclip" -> config!!.hclipColor.toJavaColor()
                    "bonzo" -> config!!.bonzoColor.toJavaColor()
                    "vclip" -> config!!.vclipColor.toJavaColor()
                    "block" -> config!!.blockColor.toJavaColor()
                    "edge" -> config!!.edgeColor.toJavaColor()
                    "walk" -> config!!.walkColor.toJavaColor()
                    "wait" -> config!!.waitColor.toJavaColor()
                    "term" -> config!!.termColor.toJavaColor()
                    else -> config!!.walkColor.toJavaColor()
                }
                drawSquareTwo(ring.x, ring.y + 0.05, ring.z, ring.width.toDouble(), ring.width.toDouble(), color, 4f, phase = false, relocate = true)
                drawSquareTwo(ring.x, ring.y + ring.height / 2, ring.z, ring.width.toDouble(), ring.width.toDouble(),color, 4f, phase = false, relocate = true)
                drawSquareTwo(ring.x, ring.y + ring.height, ring.z, ring.width.toDouble(), ring.width.toDouble(), color, 4f, phase = false, relocate = true)
        }
    }

    var shouldWait = true

    @SubscribeEvent
    fun onRenderWorld(event: RenderWorldLastEvent) {
        if (!config!!.autoP3Active || !inp3 || config!!.editmode) return
        val playerX = mc.renderManager.viewerPosX
        val playerY = mc.renderManager.viewerPosY
        val playerZ = mc.renderManager.viewerPosZ
        rings.forEach { ring ->
            val distanceX = abs(playerX - ring.x)
            val distanceY = (playerY - ring.y)
            val distanceZ = abs(playerZ - ring.z)

            if ((distanceX > (ring.width / 2) || (distanceY >= (ring.height) || distanceY < 0) || distanceZ > (ring.width / 2))) {
                ring.active = true
            }

            if (!ring.active || config!!.editmode || cooldown) return@forEach

            if (distanceX < (ring.width / 2) && distanceY < (ring.height) && distanceY >= 0 && distanceZ < (ring.width / 2)) {
                ring.active = false
                when (true) {
                    ring.looking -> rotate(ring.yaw, ring.pitch)
                    ring.walking -> walk()
                    ring.stopping -> {
                        mc.thePlayer.setVelocity(0.0, mc.thePlayer.motionY, 0.0)
                        stopVelo()
                    }
                    ring.waiting -> {
                        if (shouldWait) {
                            shouldWait = false
                            scheduleTask(ring.wait!!) {
                                ring.active = true
                            }
                            scheduleTask(ring.wait!! + 2) {
                                shouldWait = true
                            }
                            return
                        }
                    }
                    else -> {}
                }

                when (ring.type) {
                    "walk" -> {
                        modMessage("Walking")
                        walk()
                    }
                    "look" -> {
                        modMessage("Looking")
                        rotate(ring.yaw, ring.pitch)
                    }
                    "stop" -> {
                        modMessage("Stopping")
                        stopMovement()
                        stopVelo()
                    }
                    "boom" ->  {
                        modMessage("Exploding")
                        rotate(ring.yaw, ring.pitch)
                        swapFromName("boom")
                        if(ring.delaying == true) {
                            scheduleTask(ring.delay!!) { leftClick()}
                        } else {
                            scheduleTask(4) { leftClick()}
                        }
                    }
                    "jump" -> {
                        jump()
                        modMessage("Jumping")
                    }
                    "hclip" -> {
                        hclip(ring.yaw)
                        if (ring.walking == true) {
                            scheduleTask(1) {
                                walk()
                            }
                        }
                        modMessage("Hclipping")
                    }
                    "bonzo" -> {
                        swapFromName("bonzo")
                        if (ring.silent == true) {
                            set(ring.yaw, ring.pitch)
                        } else {
                            rotate(ring.yaw, ring.pitch)
                        }
                        if (ring.delaying == true) {
                            scheduleTask(ring.delay!!) {
                                airClick()
                                resetRotations()
                            }
                        } else {
                            scheduleTask(1) {
                                airClick()
                                resetRotations()
                            }
                        }
                        modMessage("Bonzoing")
                    }
                    "vclip" -> {
                        ring.depth?.let { toggleLavaClip(it) }
                        modMessage("Clipping ${ring.depth} blocks down")
                    }
                    "block" -> {
                        val (yaw, pitch) = getYawAndPitch(ring.lookX!!, ring.lookY!!, ring.lookZ!!)
                        rotate(yaw, pitch)
                        modMessage("Rotating")
                    }
                    "edge" -> {
                        toggleEdging()
                        modMessage("Edging")
                    }
                    "term" -> {
                        modMessage("Waiting for term")
                        walkOnTermOpen = true
                    }
                    else -> sendChat("Invalid ring: ${ring.type}")
            }
            }
        }
    }

    @SubscribeEvent
    fun onTermOpen(event: ReceivePacketEvent) {
        if (!walkOnTermOpen) return
        if (event.packet !is S2DPacketOpenWindow) return
        walk()
        walkOnTermOpen = false
        modMessage("Term found")
    }


object P3Command : CommandBase() {
    override fun getCommandName(): String {
        return "ring"
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
                if (!arrayListOf("walk", "look", "stop", "bonzo", "boom", "hclip", "block", "edge", "vclip", "jump", "term"
                    ).contains((type))
                ) {
                    modMessage("Invalid ring!")
                    return
                }
                val toPush = Ring(type, active = true, route = config!!.selectedRoute, x = Math.round(mc.renderManager.viewerPosX * 2) / 2.0, y = Math.round(mc.renderManager.viewerPosY * 2) / 2.0, z = Math.round(mc.renderManager.viewerPosZ * 2) / 2.0, yaw = mc.renderManager.playerViewY, pitch = mc.renderManager.playerViewX, height = 1f, width = 1f, lookX = 1000.0, lookY = 1000.0, lookZ = 1000.0, depth = 1000f, stopping = false, looking = false, walking = false, silent = false, delaying = false, delay = 1000)
                if (args.size > 3 && type == "block") {
                    toPush.lookX = parseDouble(args[2])
                    toPush.lookY = parseDouble(args[3])
                    toPush.lookZ = parseDouble(args[4])
                }
                if (type == "vclip" && args.size > 2) {
                    val depth = parseFloat(args[2])
                    toPush.depth = depth
                }
                args.drop(2).forEachIndexed { _, arg ->
                    when {
                        arg.startsWith("h") -> toPush.height = arg.slice(1 until arg.length).toFloat()
                        arg.startsWith("w")  && !arg.startsWith("wait:") && arg != "walk" -> toPush.width = arg.slice(1 until arg.length).toFloat()
                        arg == "stop" -> toPush.stopping = true
                        arg == "look" -> toPush.looking = true
                        arg == "walk" -> toPush.walking = true
                        arg == "silent" -> toPush.silent = true
                        arg.startsWith("delay:") -> {
                            val delay = arg.slice(6 until arg.length).toInt()
                            toPush.delaying = true
                            toPush.delay = delay
                        }
                        arg.startsWith("wait:") -> {
                            val wait = arg.slice(5 until arg.length).toInt()
                            toPush.waiting = true
                            toPush.wait = wait
                        }
                    }
                }
                allrings.add(toPush)
                cooldown = true
                modMessage("$type added!")
                saveRings()
                loadRings()
                scheduleTask(19) {
                    cooldown = false
                }
            }
            "edit" -> {
                if (config!!.editmode) {
                    config!!.editmode = false
                    modMessage("Editmode off!")
                } else {
                    config!!.editmode = true
                    modMessage("Editmode on!")
                }
            }
            "start" -> {
                inp3 = true
                config!!.autoP3Active = true
                modMessage("P3 started!")
                loadRings()
            }
            "stop" -> {
                inp3 = false
                modMessage("P3 stopped!")
            }
            "remove" -> {
                val range = args.getOrNull(1)?.toDoubleOrNull() ?: 2.0 // Default range to 2 if not provided
                allrings = allrings.filter { ring ->
                    // Filter rings based on the route and distance criteria
                    if (ring.route != config!!.selectedRoute) return@filter true
                    val distance = distanceToPlayer(ring.x, ring.y, ring.z)
                    distance >= range
                }.toMutableList()
                saveRings()
                loadRings()
            }
            "undo" -> {
                allrings.removeLast()
                saveRings()
                loadRings()
            }
            "clear" -> {
                val prefix = "§0[§6Yharim§0] §8»§r "
                sender?.addChatMessage(ChatComponentText("$prefix Are you sure?")
                    .apply {
                        chatStyle = ChatStyle().apply {
                            chatClickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/p3 clearconfirm")
                            chatHoverEvent = HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                ChatComponentText("$prefix Click to clear ALL routes!")
                            )
                        }
                    }
                )
            }
            "clearroute" -> {
                val prefix = "§0[§6Yharim§0] §8»§r "
                sender?.addChatMessage(ChatComponentText("$prefix Are you sure?")
                    .apply {
                        chatStyle = ChatStyle().apply {
                            chatClickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/p3 clearrouteconfirm")
                            chatHoverEvent = HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                ChatComponentText("$prefix Click to clear CURRENT route!")
                            )
                        }
                    }
                )
            }
            "clearrouteconfirm" -> {
                allrings = allrings.filter { ring ->
                    // Filter rings based on the route and distance criteria
                    ring.route != config!!.selectedRoute
                }.toMutableList()
                saveRings()
                loadRings()
            }
            "clearconfirm" -> {
                allrings = mutableListOf()
                modMessage("Cleared route")
                saveRings()
                loadRings()
            }
            "loadroute" -> {
                val route = args[1]
                config!!.selectedRoute = route
                modMessage("Loaded route $route")
                loadRings()
            }
            "on" -> {
                config!!.autoP3Active = true
                modMessage("AutoP3 on!")
            }
            "off" -> {
                config!!.autoP3Active = false
                modMessage("AutoP3 off!")
            }
            "save" -> saveRings()
            "load" -> loadRings()
            else -> modMessage("Invalid argument!")
        }
    }
}
}

object RingManager {
    var rings: MutableList<Ring> = mutableListOf()
    var allrings: MutableList<Ring> = mutableListOf()
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val file = File("config/catgirlyharim/rings.json")

    fun loadRings() {
        if (file.exists()) {
            allrings = gson.fromJson(file.readText(), object : TypeToken<List<Ring>>() {}.type)
            rings = allrings.filter { it.route == config!!.selectedRoute }.toMutableList()
        }
        file.parentFile.mkdirs()
        file.writeText("[]")
    }

    fun saveRings() {
        val filteredRings = allrings.map { ring ->
            ring.copy(
                delay = ring.delay.takeUnless { it == 1000 },
                lookX = ring.lookX.takeUnless { it == 1000.0 },
                lookY = ring.lookY.takeUnless { it == 1000.0 },
                lookZ = ring.lookZ.takeUnless { it == 1000.0 },
                depth = ring.depth.takeUnless { it == 1000f },
                wait = ring.wait.takeUnless { it == 1000 },
                waiting = ring.waiting.takeUnless { it == false },
                delaying = ring.delaying.takeUnless { it == false },
                stopping = ring.stopping.takeUnless { it == false },
                walking = ring.walking.takeUnless { it == false },
                looking = ring.looking.takeUnless { it == false },
                silent = ring.silent.takeUnless { it == false },
            )
        }
        file.writeText(gson.toJson(filteredRings))
    }

    @SubscribeEvent
    fun onLoad(event: WorldEvent.Load) {
        loadRings()
    }

    @SubscribeEvent
    fun onUnload(event: WorldEvent.Unload) {
        inp3 = false
    }
}
data class Ring(
    /*Hi :3*/
    val type: String,
    var active: Boolean,
    var route: String,
    var x: Double,
    var y: Double,
    var z: Double,
    var yaw: Float,
    var pitch: Float,
    var height: Float,
    var width: Float,
    var lookX: Double? = null,
    var lookY: Double? = null,
    var lookZ: Double? = null,
    var depth: Float? = null,
    var stopping: Boolean? = null,
    var looking: Boolean? = null,
    var walking: Boolean? = null,
    var silent: Boolean? = null,
    var delaying: Boolean? = null,
    var delay: Int? = null,
    var waiting: Boolean? = null,
    var wait: Int? = null
)