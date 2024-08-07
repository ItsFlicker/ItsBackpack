package io.github.itsflicker.backpack.database

import io.github.itsflicker.backpack.ItsBackpack
import io.github.itsflicker.backpack.data.Backpack
import taboolib.module.database.ColumnOptionSQL
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.Table
import taboolib.module.database.getHost
import java.util.*

class DatabaseSQL {

    val host = ItsBackpack.conf.getHost("database")

    val name: String
        get() = ItsBackpack.conf.getString("database.table", "backpack")!!

    val table = Table("${name}_data", host) {
        add { id() }
        add("packid") {
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(ColumnOptionSQL.UNIQUE_KEY)
            }
        }
        add("owner") {
            type(ColumnTypeSQL.VARCHAR, 36)
        }
        add("openid") {
            type(ColumnTypeSQL.VARCHAR, 36)
        }
        add("data") {
            type(ColumnTypeSQL.MEDIUMTEXT)
        }
    }

    val dataSource = host.createDataSource()

    init {
        table.workspace(dataSource) { createTable() }.run()
    }

    fun getPackById(id: UUID, openId: UUID?): String? {
        return table.select(dataSource) {
            where {
                "packid" eq id.toString()
                "openid" eq openId?.toString()
            }
        }.firstOrNull { getString("data") }
    }

    fun setOpenId(id: UUID, openId: UUID?) {
        table.update(dataSource) {
            where("packid" eq id.toString())
            set("openid", openId?.toString())
        }
    }

    fun save(backpack: Backpack) {
        table.update(dataSource) {
            where("packid" eq backpack.id.toString())
            set("data", backpack.content)
        }
    }

    fun createBackpack(backpack: Backpack, owner: UUID) {
        table.insert(dataSource, "packid", "owner", "data") {
            value(backpack.id.toString(), owner.toString(), backpack.content)
        }
    }

}