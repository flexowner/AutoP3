package catgirlyharim.init

import catgirlyharim.init.config.MyConfig
import catgirlyharim.init.features.AutoP3
import catgirlyharim.init.features.BossEsp
import catgirlyharim.init.features.PearlClip
import catgirlyharim.init.features.PearlClipCommand
import catgirlyharim.init.features.RingManager
import catgirlyharim.init.features.Simulation
import catgirlyharim.init.features.StormClip
import catgirlyharim.init.features.TerminalEsp
import catgirlyharim.init.features.petKeyBinds
import catgirlyharim.init.utils.*
import net.minecraft.client.Minecraft
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent

class CgyCommands : CommandBase() {
    override fun getCommandName(): String {
        return "CatgirlYharimAddons"
    }

    override fun getCommandAliases(): List<String> {
        return listOf(
            "cgyforge"
        )
    }

    override fun getCommandUsage(sender: ICommandSender?): String {
        return "/$commandName"
    }

    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

    override fun processCommand(sender: ICommandSender?, args: Array<out String>?) {
        CatgirlYharim.config?.openGui()
    }
}

@Mod(
    modid = "cgy",
    name = "CatgirlYharimAddons",
    version = "1.0.0",
    clientSideOnly = true
)

class CatgirlYharim {
    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        config = MyConfig()
        listOf(
            CgyCommands(),
            AutoP3.P3Command,
            PearlClipCommand()
        ).forEach {
            ClientCommandHandler.instance.registerCommand((it))
        }

        listOf(
            this,
            Hclip,
            AutoP3,
            ServerRotateUtils,
            lavaClip,
            edgeJump,
            Simulation,
            HUDRenderUtils,
            MovementUtils,
            WorldRenderUtils,
            ClientListener,
            Utils,
            RingManager,
            PearlClip,
            StormClip,
            petKeyBinds,
            BossEsp,
            TerminalEsp,
        ).forEach(MinecraftForge.EVENT_BUS::register)

    }

    companion object {
        val mc: Minecraft = Minecraft.getMinecraft()
        var config: MyConfig? = null
    }
}
