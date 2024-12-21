package catgirlyharim.init.utils

import catgirlyharim.init.CatgirlYharim.Companion.mc
import net.minecraft.block.Block
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

// DungeonLocation.currentRoomId should work
object DungeonLocation {
    private var roomX: Int = -1
    private var roomZ: Int = -1
    var currentRoomId: Int = 0;

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.END) return
        if (mc.thePlayer == null) return
        // need a check if in dungeon I think

        val prevRoomX: Int = roomX
        val prevRoomZ: Int = roomZ

        roomX = ((mc.thePlayer.posX + 200) / 32).toInt()
        roomZ = ((mc.thePlayer.posZ + 200) / 32).toInt()

        if (prevRoomX != roomX || prevRoomZ != roomZ) {
            val cx: Int = -185 + roomX * 32
            val cz: Int = -185 + roomZ * 32

            val blocks = mutableListOf<Int>()

            for (y in 140 downTo 12) {
                val id: Int = Block.getIdFromBlock(mc.theWorld.getBlockState(BlockPos(cx, y, cz)).block)
                if (id != 5 && id != 54) blocks.add(id)
            }
            currentRoomId = blocks.joinToString(separator = "") { it.toString() }.hashCode()
        }
    }

}