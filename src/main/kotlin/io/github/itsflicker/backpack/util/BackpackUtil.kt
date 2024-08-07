package io.github.itsflicker.backpack.util

import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

fun ItemStack.isBackpack(): Boolean {
    return itemMeta?.has("size", PersistentDataType.INTEGER) == true
}

fun ItemStack.getBackpackSize(): Int {
    return itemMeta?.get("size", PersistentDataType.INTEGER) ?: 0
}