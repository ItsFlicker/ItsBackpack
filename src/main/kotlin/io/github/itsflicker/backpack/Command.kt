package io.github.itsflicker.backpack

import io.github.itsflicker.backpack.data.Backpack
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper
import taboolib.library.xseries.XItemStack
import taboolib.platform.util.onlinePlayers
import taboolib.platform.util.sendErrorMessage

@CommandHeader("itsbackpack", ["backpack"], permission = "itsbackpack.command")
object Command {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody(optional = true)
    val give = subCommand {
        dynamic("player") {
            suggest {
                onlinePlayers.map { it.name }
            }
            dynamic("backpack") {
                suggest {
                    ItsBackpack.conf.getConfigurationSection("backpacks")?.getKeys(false)?.toList()
                }
                execute<CommandSender> { sender, ctx, _ ->
                    val player = Bukkit.getPlayer(ctx["player"]) ?: return@execute
                    val item = Backpack.make(ctx["backpack"]) ?: return@execute sender.sendErrorMessage("背包获取失败!")
                    XItemStack.giveOrDrop(player, item)
                }
            }
        }
    }

}