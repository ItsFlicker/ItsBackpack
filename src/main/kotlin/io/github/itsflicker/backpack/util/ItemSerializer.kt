package io.github.itsflicker.backpack.util

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONArray
import com.alibaba.fastjson2.JSONObject
import de.tr7zw.changeme.nbtapi.NBTContainer
import de.tr7zw.changeme.nbtapi.NBTItem
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.isNotAir
import java.util.*

object ItemSerializer {

    fun serialize(item: ItemStack): String {
        val str = NBTItem.convertItemtoNBT(item).toString()
        val bytes = str.encodeToByteArray()
        return Base64.getEncoder().encodeToString(bytes)
    }

    fun deserialize(base64: String): ItemStack? {
        val bytes = Base64.getDecoder().decode(base64)
        val str = String(bytes, Charsets.UTF_8)
        return NBTItem.convertNBTtoItem(NBTContainer(str))
    }

    fun serializeInventory(inv: Inventory): String {
        val json = JSONArray()
        inv.contents.forEachIndexed { slot, item ->
            if (item.isNotAir()) {
                json.add(JSONObject().apply {
                    put("slot", slot)
                    put("item", serialize(item))
                })
            }
        }
        return json.toString()
    }

    fun deserializeInventory(str: String, inv: Inventory) {
        val json = JSON.parseArray(str)
        json.forEach {
            val obj = it as JSONObject
            val slot = obj.getIntValue("slot")
            val item = obj.getString("item")
            inv.setItem(slot, deserialize(item))
        }
    }

}