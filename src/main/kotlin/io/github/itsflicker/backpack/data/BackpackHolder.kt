package io.github.itsflicker.backpack.data

import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

class BackpackHolder(
    val backpack: Backpack,
    val item: ItemStack,
    val isNew: Boolean,
    size: Int,
    title: String
) : InventoryHolder {

    private val inv = Bukkit.createInventory(this, size, title)

    override fun getInventory(): Inventory {
        return inv
    }

}