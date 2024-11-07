package catgirlyharim.utils

import catgirlyharim.CatgirlYharim.Companion.mc
import catgirlyharim.utils.MovementUtils.restartMovement
import catgirlyharim.config.MyConfig.hclipDistance
import catgirlyharim.events.MovementUpdateEvent
import catgirlyharim.utils.MovementUtils.stopMovement
import net.minecraft.client.settings.KeyBinding
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.cos
import kotlin.math.sin

object Utils {

    //chat

    fun sendChat(message: String) {
        mc.thePlayer?.sendChatMessage(message)
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
}

object Hclip {
    val forward = mc.gameSettings.keyBindForward.keyCode
    var pendingHclip = false
    var yawtouse: Float? = null

    fun hclip(yaw: Float) {
        val forward = mc.gameSettings.keyBindForward.keyCode
        stopMovement()
        yawtouse = yaw
        mc.thePlayer.setVelocity(0.0, mc.thePlayer.motionY, 0.0)
        pendingHclip = true
        restartMovement()
    }

    @SubscribeEvent
    fun onMovementUpdate(event: MovementUpdateEvent.Pre) {
        if (pendingHclip) {
            val speed = mc.thePlayer.capabilities.walkSpeed * hclipDistance
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

