package io.github.itsflicker.backpack

import de.tr7zw.changeme.nbtapi.NBT
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.disablePlugin
import taboolib.common.platform.function.warning
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration

object ItsBackpack : Plugin() {

    @Config
    lateinit var conf: Configuration

    override fun onEnable() {
        if (!NBT.preloadApi()) {
            warning("NBT-API wasn't initialized properly, disabling the plugin")
            disablePlugin()
            return
        }
    }

}