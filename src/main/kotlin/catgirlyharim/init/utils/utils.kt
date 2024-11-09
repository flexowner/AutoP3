package catgirlyharim.init.utils

import catgirlyharim.init.CatgirlYharim.Companion.config
import catgirlyharim.init.CatgirlYharim.Companion.mc
import catgirlyharim.init.utils.MovementUtils.restartMovement
import catgirlyharim.init.events.MovementUpdateEvent
import catgirlyharim.init.events.ReceivePacketEvent
import catgirlyharim.init.utils.MovementUtils.jump
import catgirlyharim.init.utils.MovementUtils.stopMovement
import catgirlyharim.init.utils.Utils.relativeClip
import catgirlyharim.init.utils.WorldRenderUtils.renderText
import net.minecraft.block.Block
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.settings.KeyBinding
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.util.BlockPos
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatStyle
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.*

object Utils {

    //chat

    fun sendChat(message: String) {
        mc.thePlayer.sendChatMessage(message)
    }

    fun runOnMCThread(run: () -> Unit) {
        if (!mc.isCallingFromMinecraftThread) mc.addScheduledTask(run) else run()
    }

    fun modMessage(message: Any?, prefix: String = "§0[§6Yharim§0] §8»§r ", chatStyle: ChatStyle? = null) {
        val chatComponent = ChatComponentText("$prefix$message")
        chatStyle?.let { chatComponent.setChatStyle(it) } // Set chat style using setChatStyle method
        runOnMCThread { mc.thePlayer?.addChatMessage(chatComponent) }
    }

    //player

    fun rotate(yaw: Float, pitch: Float) {
        mc.thePlayer.rotationYaw = yaw
        mc.thePlayer.rotationPitch = pitch
    }

    fun rightClick() {
        KeyBinding.onTick(mc.gameSettings.keyBindUseItem.keyCode)
    }

    fun leftClick() {
        KeyBinding.onTick(mc.gameSettings.keyBindAttack.keyCode)
    }

    fun swapFromName(name: String): Boolean {
        for (i in 0..8) {
            val stack: ItemStack? = mc.thePlayer.inventory.getStackInSlot(i)
            val itemName = stack?.displayName
            if (itemName != null) {
                if (itemName.contains(name, ignoreCase = true)) {
                    mc.thePlayer.inventory.currentItem = i
                    return true
                }
            }
        }
        sendChat("$name not found.")
        return false
    }

    fun airClick() {
        mc.netHandler.networkManager.sendPacket(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
    }

    fun relativeClip(x: Float, y: Float, z: Float) {
        mc.thePlayer.setPosition(mc.thePlayer.posX + x,mc.thePlayer.posY + y,mc.thePlayer.posZ + z)
    }

    fun getYawAndPitch(x: Double, y:Double, z:Double): Pair<Float, Float> {
        val dx = x - mc.thePlayer.posX   // Difference in x
        val dy = y - mc.thePlayer.posY  // Difference in y
        val dz = z - mc.thePlayer.posZ   // Difference in z

        val horizontalDistance = sqrt(dx * dx + dz * dz )

        val yaw = Math.toDegrees(atan2(-dx, dz))
        val pitch = -Math.toDegrees(atan2(dy, horizontalDistance))

        val normalizedYaw = if (yaw < 0) yaw + 360 else yaw

        return Pair(normalizedYaw.toFloat(), pitch.toFloat())
    }

    fun distanceToPlayer(x: Double, y: Double, z: Double): Double {
        return sqrt((mc.renderManager.viewerPosX - x) * (mc.renderManager.viewerPosX - x) +
                    (mc.renderManager.viewerPosY - y) * (mc.renderManager.viewerPosY - y) +
                    (mc.renderManager.viewerPosZ - z) * (mc.renderManager.viewerPosZ - z)
        )
    }

}

object Hclip {
    val forward = mc.gameSettings.keyBindForward.keyCode
    var pendingHclip = false
    var yawtouse: Float? = null

    fun hclip(yaw: Float) {
        val forward = mc.gameSettings.keyBindForward.keyCode
        stopMovement()
        yawtouse = yaw
        if (mc.thePlayer.onGround) {
            jump()
        }
        mc.thePlayer.setVelocity(0.0, mc.thePlayer.motionY, 0.0)
        pendingHclip = true
        //restartMovement()
    }

    @SubscribeEvent
    fun onMovementUpdate(event: MovementUpdateEvent.Pre) {
        if (pendingHclip) {
            val speed = mc.thePlayer.capabilities.walkSpeed * config!!.hclipDistance
            val radians = yawtouse!! * Math.PI / 180
            val x = -sin(radians) * speed
            val z = cos(radians) * speed

            mc.thePlayer.motionX = x
            mc.thePlayer.motionZ = z
            restartMovement()

            pendingHclip = false
        }
    }
}

object lavaClip {
    var lavaclipping = false
    var veloReceived = true
    var lavaClipDistance: Float = 40f

    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        if (!lavaclipping || !mc.thePlayer.isInLava) return
            lavaclipping = false
        relativeClip(0f, -lavaClipDistance, 0f)
    }

    @SubscribeEvent
    fun onPacket(event: ReceivePacketEvent) {
        if (veloReceived) return
        if (event.packet !is S12PacketEntityVelocity) return
        if (event.packet.entityID != mc.thePlayer.entityId) return
        if (event.packet.motionY == 28000) {
            event.isCanceled = true
            veloReceived = true
    }
    }

    @SubscribeEvent
    fun onOverlay(event: RenderGameOverlayEvent.Post) {
        if (event.type != RenderGameOverlayEvent.ElementType.HOTBAR || !lavaclipping || mc.ingameGUI == null) return
        val sr = ScaledResolution(mc)
        val text = "Lava clipping $lavaClipDistance"
        val width = sr.scaledWidth / 2 - mc.fontRendererObj.getStringWidth(text) / 2
        renderText(
            text = text,
            x = width,
            y = sr.scaledHeight / 2 + 10
        )
    }

    fun toggleLavaClip(distance: Float) {
        lavaClipDistance = round(distance)
        if (!lavaclipping) {
            lavaclipping = true
            veloReceived = false
        } else {
            lavaclipping = false
            veloReceived = true
        }
    }
}

object edgeJump{
    var edging = false
    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        if (!edging) return
        val blockID = Block.getIdFromBlock(mc.theWorld.getBlockState(BlockPos(mc.thePlayer.posX,mc.thePlayer.posY- 1, mc.thePlayer.posZ)).block)
        if (blockID == 0) {
            jump()
            edging = false
        }
    }

    fun toggleEdging() {
        if (!edging) {
            edging = true
        } else {
            edging = false
        }
    }
}