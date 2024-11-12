package catgirlyharim.init.features

import catgirlyharim.init.CatgirlYharim.Companion.config
import catgirlyharim.init.CatgirlYharim.Companion.mc
import catgirlyharim.init.utils.ClientListener.scheduleTask
import catgirlyharim.init.utils.Utils.modMessage
import net.minecraft.block.Block
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

object Simulation {
    var veloSent = false

    @SubscribeEvent
    fun onWorldTick(event: TickEvent.WorldTickEvent) {
        if (veloSent) return
        if (event.phase !== TickEvent.Phase.START) return
        if (!config!!.activeSimulation) return
        if (mc.thePlayer == null || mc.theWorld == null) {
            modMessage("Client-side Minecraft instance or player/world is not available.")
            return
        }
        if (!mc.isSingleplayer) return
        if (config!!.speedSimulation) {
            mc.thePlayer.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.movementSpeed)?.baseValue = 0.50000000745
            mc.thePlayer.capabilities?.setPlayerWalkSpeed(0.5f)
        }
        if (config!!.lavaSimulation) {
            val playerPos = BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)
            val blockID = Block.getIdFromBlock(mc.theWorld.getBlockState(playerPos).block)

            if (blockID == 10 || blockID == 11) {
                mc.thePlayer.motionY = 3.5
                veloSent = true
                scheduleTask(19) {
                    veloSent = false
                }
            }
            if (blockID == 66) {
                mc.thePlayer.motionY = 7.0
                veloSent = true
                scheduleTask(19) {
                    veloSent = false
                }
            }
        }
    }
}
