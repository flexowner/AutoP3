package catgirlyharim.features

import catgirlyharim.CatgirlYharim.Companion.mc
import catgirlyharim.config.MyConfig.activeSimulation
import catgirlyharim.config.MyConfig.lavaSimulation
import catgirlyharim.config.MyConfig.speedSimulation
import catgirlyharim.utils.Utils.modMessage
import com.sun.security.ntlm.Client
import net.minecraft.block.Block
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

object Simulation {
    @SubscribeEvent
    fun onWorldTick(event: TickEvent.WorldTickEvent) {
        // Ensure the event is handled only on the client side
        if (event.phase !== TickEvent.Phase.START) return
        if (!activeSimulation) return
        if (mc.thePlayer == null || mc.theWorld == null) {
            modMessage("Client-side Minecraft instance or player/world is not available.")
            return
        }
        if (!mc.isSingleplayer) return
        if (speedSimulation) {
            mc.thePlayer.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.movementSpeed)?.baseValue = 0.50000000745
            mc.thePlayer.capabilities?.setPlayerWalkSpeed(0.5f)
        }
        if (lavaSimulation) {
            val playerPos = BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)
            val blockID = Block.getIdFromBlock(mc.theWorld.getBlockState(playerPos).block)

            if (blockID == 10 || blockID == 11) {
                mc.thePlayer.motionY = 3.5
            }
            if (blockID == 66) {
                mc.thePlayer.motionY = 7.0
            }
        }
    }
}
