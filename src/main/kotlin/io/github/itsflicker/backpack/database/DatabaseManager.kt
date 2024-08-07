package io.github.itsflicker.backpack.database

import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

object DatabaseManager {

    lateinit var database: DatabaseSQL

    @Awake(LifeCycle.ENABLE)
    fun init() {
        database = DatabaseSQL()
    }

}