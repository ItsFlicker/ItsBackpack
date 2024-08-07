package io.github.itsflicker.backpack.data

import de.tr7zw.changeme.nbtapi.NBT
import dev.lone.itemsadder.api.CustomStack
import io.github.itsflicker.backpack.ItsBackpack
import io.github.itsflicker.backpack.util.ItemSerializer
import io.github.itsflicker.backpack.util.set
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import taboolib.platform.util.modifyMeta
import java.util.*

data class Backpack(
    val id: UUID,
    var content: String
) {

    fun save(inv: Inventory) {
        content = ItemSerializer.serializeInventory(inv)
    }

    companion object {

        fun make(type: String): ItemStack? {
            val source = ItsBackpack.conf.getString("backpacks.$type.source") ?: return null
            val item = CustomStack.getInstance(source)?.itemStack?.clone() ?: return null
            val size = if (ItsBackpack.conf.getBoolean("backpacks.$type.ender", false)) {
                -1
            } else {
                ItsBackpack.conf.getInt("backpacks.$type.size", 3)
            }
            NBT.modifyComponents(item) { nbt ->
                nbt.mergeCompound(NBT.parseNBT("{\"minecraft:attribute_modifiers\":{\"modifiers\":[],\"show_in_tooltip\":false}}"))
                nbt.setInteger("minecraft:dyed_color", 14781475)
            }
            item.modifyMeta<ItemMeta> {
                set("size", PersistentDataType.INTEGER, size)
            }
            return item
        }

    }

}
