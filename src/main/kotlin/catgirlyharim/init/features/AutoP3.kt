package catgirlyharim.init.features

import catgirlyharim.init.CatgirlYharim.Companion.config
import catgirlyharim.init.CatgirlYharim.Companion.mc
import catgirlyharim.init.events.TermOpenEvent
import catgirlyharim.init.features.AutoP3.cooldown
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
import catgirlyharim.init.utils.Utils.leftClick
import catgirlyharim.init.utils.Utils.modMessage
import catgirlyharim.init.utils.Utils.rotate
import catgirlyharim.init.utils.Utils.swapFromName
import catgirlyharim.init.utils.WorldRenderUtils.drawSquareTwo
import catgirlyharim.init.utils.edgeJump.toggleEdging
import catgirlyharim.init.utils.lavaClip.toggleLavaClip
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatStyle
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.io.File
import java.lang.Float.parseFloat
import kotlin.math.abs

object AutoP3 {
    var inp3 = false
    var cooldown = false
    var termOpened = false

    @SubscribeEvent
    fun onUnload(event: WorldEvent.Unload) {
        inp3 = false
    }

    @SubscribeEvent
    fun onLoad(event: WorldEvent.Load) {
        loadRings()
    }

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        val msg = event.message.unformattedText

        if (msg.contains("[BOSS] Maxor")) {
            if (config!!.onBossStart) config!!.selectedRoute = config!!.BossStartRoute
            loadRings()
            inp3 = true
        }
        if (msg.contains("[BOSS] Storm: I should have known that I stood no chance.")) {
            inp3 = true
            config!!.autoP3Active = true
            if (config!!.onP3Start) config!!.selectedRoute = config!!.P3StartRoute
            loadRings()
        }
        if (msg.contains("[BOSS] Necron: All this, for nothing...")) {
            inp3 = false
        }
    }

    @SubscribeEvent
    fun termOpen(event: TermOpenEvent) {
        termOpened = true
        scheduleTask(2) {
            termOpened = false
        }
    }

    @SubscribeEvent
    fun render(event: RenderWorldLastEvent) {
        if (!config!!.autoP3Active || !AutoP3.inp3) return
        rings.forEach{ring ->
            if(!ring.active) return@forEach
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

    fun inRing(ring: Ring): Boolean {
        val viewerPos = mc.renderManager
        val distanceX = abs(viewerPos.viewerPosX - ring.x)
        val distanceY = abs(viewerPos.viewerPosY - ring.y)
        val distanceZ = abs(viewerPos.viewerPosZ - ring.z)

        return distanceX < (ring.width / 2) &&
                distanceY < ring.height &&
                distanceY >= -0.5 &&
                distanceZ < (ring.width / 2);
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        if (config!!.editmode || !inp3 || cooldown) return
        rings.forEach{ring ->
            if (inRing(ring)) {
                if (!ring.active) return
                if (ring.term == true && !termOpened) return
                ring.active = false
                GlobalScope.launch {
                    executeAction(ring)
                }
            } else {
                ring.active = true
            }
        }
    }

private suspend fun executeAction(ring: Ring) {
    val actionDelay: Int = if (ring.delaying == true) ring.delay!! else 0
    delay(actionDelay.toLong())
    when (true) {
        ring.looking -> rotate(ring.yaw, ring.pitch)
        ring.stopping -> mc.thePlayer.setVelocity(0.0, mc.thePlayer.motionY, 0.0)
        ring.walking -> walk()
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
        "boom" -> {
            modMessage("Exploding")
            rotate(ring.yaw, ring.pitch)
            swapFromName("boom")
            scheduleTask(4) { leftClick()}
        }
        "jump" -> {
            modMessage("Jumping")
            jump()
        }
        "hclip" -> {
            modMessage("Hclipping")
            hclip(ring.yaw)
            if (ring.walking == true) {
                scheduleTask(1) {
                    walk()
                }
            }
        }
        "bonzo" -> {
            modMessage("Bonzoing")
            swapFromName("bonzo")
            if (ring.silent == true) {
                set(ring.yaw, ring.pitch)
            } else {
                rotate(ring.yaw, ring.pitch)
            }
            scheduleTask(1) {
                airClick()
                resetRotations()
            }
        }
        "vclip" -> {
            modMessage("Clipping ${ring.depth} blocks down")
            ring.depth?.let { toggleLavaClip(it) }
        }
        "block" -> {
            modMessage("Rotating")
            val (yaw, pitch) = getYawAndPitch(ring.lookX!!, ring.lookY!!, ring.lookZ!!)
            rotate(yaw, pitch)
        }
        "edge" -> {
            modMessage("Edging")
            toggleEdging()
        }
    }
}
}

object AutoP3Commands : CommandBase() {
    override fun getCommandName(): String {
        return "ring"
    }

    override fun getCommandAliases(): List<String> {
        return listOf()
    }

    override fun getCommandUsage(sender: ICommandSender?): String {
        return "/${AutoP3Commands.commandName}"
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
                if (!arrayListOf("walk", "look", "stop", "bonzo", "boom", "hclip", "block", "edge", "vclip", "jump",
                    ).contains((type))
                ) {
                    modMessage("Invalid ring!")
                    return
                }
                val toPush = Ring(type, active = true, route = config!!.selectedRoute, x = Math.round(mc.renderManager.viewerPosX * 2) / 2.0, y = Math.round(mc.renderManager.viewerPosY * 2) / 2.0, z = Math.round(mc.renderManager.viewerPosZ * 2) / 2.0, yaw = mc.renderManager.playerViewY, pitch = mc.renderManager.playerViewX, height = 1f, width = 1f)
                if (type == "block") {
                    toPush.lookX = mc.thePlayer.rayTrace(40.0, 1f).hitVec.xCoord
                    toPush.lookY = mc.thePlayer.rayTrace(40.0, 1f).hitVec.yCoord
                    toPush.lookZ = mc.thePlayer.rayTrace(40.0, 1f).hitVec.zCoord
                }
                if (type == "vclip") {
                    if (args.size > 2) {
                        val depth = parseFloat(args[2])
                        toPush.depth = depth
                    } else {
                        modMessage("No distance specified!")
                    }
                }
                args.drop(2).forEachIndexed { _, arg ->
                    when {
                        arg.startsWith("h") -> toPush.height = arg.slice(1 until arg.length).toFloat()
                        (arg.startsWith("w") && arg != "walk") -> toPush.width = arg.slice(1 until arg.length).toFloat()
                        arg == "stop" -> toPush.stopping = true
                        arg == "look" -> toPush.looking = true
                        arg == "walk" -> toPush.walking = true
                        arg == "silent" -> toPush.silent = true
                        arg == "term" -> toPush.term = true
                        arg.startsWith("delay:") -> {
                            val delay = arg.slice(6 until arg.length).toInt()
                            toPush.delaying = true
                            toPush.delay = delay
                        }
                    }
                }
                allrings.add(toPush)
                cooldown = true
                modMessage("$type added!")
                saveRings()
                loadRings()
                scheduleTask(19) {
                    AutoP3.cooldown = false
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
                modMessage("Rings within ${range} blocks removed")
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
                modMessage("Ring undone")
                allrings.removeLast()
                saveRings()
                loadRings()
            }
            "clear" -> {
                val prefix = "§0[§6AutoP3§0] §8»§r "
                sender?.addChatMessage(ChatComponentText("${prefix}Are you sure?")
                    .apply {
                        chatStyle = ChatStyle().apply {
                            chatClickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ring clearconfirm")
                            chatHoverEvent = HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                ChatComponentText("$prefix Click to clear ALL routes!")
                            )
                        }
                    }
                )
            }
            "clearroute" -> {
                val prefix = "§0[§6AutoP3§0] §8»§r "
                sender?.addChatMessage(ChatComponentText("${prefix}Are you sure?")
                    .apply {
                        chatStyle = ChatStyle().apply {
                            chatClickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ring clearrouteconfirm")
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
                saveRings()
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

object RingManager {
    var rings: MutableList<Ring> = mutableListOf()
    var allrings: MutableList<Ring> = mutableListOf()
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val file = File("config/catgirlyharim/rings.json")

    fun loadRings() {
        if (file.exists()) {
            allrings = gson.fromJson(file.readText(), object : TypeToken<List<Ring>>() {}.type)
            rings = allrings.filter { it.route == config!!.selectedRoute }.toMutableList()
        } else {
            file.parentFile.mkdirs()
            file.writeText("[]")
        }
    }

    fun saveRings() {
        file.writeText(gson.toJson(allrings))
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
    var term: Boolean? = null
)