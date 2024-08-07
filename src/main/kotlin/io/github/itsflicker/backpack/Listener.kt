package io.github.itsflicker.backpack

import io.github.itsflicker.backpack.data.Backpack
import io.github.itsflicker.backpack.data.BackpackHolder
import io.github.itsflicker.backpack.data.UUIDDataType
import io.github.itsflicker.backpack.database.DatabaseManager
import io.github.itsflicker.backpack.util.*
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync
import taboolib.common5.Baffle
import taboolib.platform.util.*
import java.util.*
import java.util.concurrent.TimeUnit

object Listener {

    val baffle = Baffle.of(1, TimeUnit.SECONDS)

    @SubscribeEvent
    fun onInteract(e: PlayerInteractEvent) {
        if (e.hand != EquipmentSlot.HAND || !e.isRightClick()) {
            return
        }
        val player = e.player
        val item = e.item ?: return
        if (!item.isBackpack()) return
        if (!baffle.hasNext(player.name)) {
            player.sendWarnMessage("你打开背包的速度太快了!")
            return
        }
        if (player.openInventory.topInventory.type != InventoryType.CRAFTING) {
            return
        }
        val size = item.getBackpackSize()
        if (size == -1) {
            player.openInventory(player.enderChest)
            return
        } else if (size < 1) {
            return
        }
        val isNew = !item.itemMeta!!.has("id", UUIDDataType)
        if (isNew) {
            item.modifyMeta<ItemMeta> {
                set("id", UUIDDataType, UUID.randomUUID())
                set("owner", UUIDDataType, player.uniqueId)
            }
        }
        val oldOpenId = item.itemMeta!!["openid", UUIDDataType]
        val packId = item.itemMeta!!["id", UUIDDataType]!!
        val openId = UUID.randomUUID()
        item.itemMeta!!["openid", UUIDDataType] = openId
        e.isCancelled = true
        submitAsync {
            val database = DatabaseManager.database
            val content = database.getPackById(packId, oldOpenId)
            if (content == null && !isNew) {
                player.sendErrorMessage("背包不存在或已在别处打开!")
                return@submitAsync
            }
            if (!isNew) {
                database.setOpenId(packId, openId)
            }
            val backpack = Backpack(packId, content ?: "[]")
            submit {
                val holder = BackpackHolder(backpack, item, isNew, size * 9, getTitle(item))
                val inv = holder.inventory
                ItemSerializer.deserializeInventory(backpack.content, inv)
                player.openInventory(inv)
            }
        }
    }

    @SubscribeEvent
    fun onClose(e: InventoryCloseEvent) {
        val inv = e.inventory
        val holder = inv.holder
        if (holder !is BackpackHolder) {
            return
        }
        val backpack = holder.backpack
        backpack.save(inv)
        holder.item.modifyMeta<ItemMeta> {
            remove("openid")
        }
        submitAsync {
            val database = DatabaseManager.database
            if (holder.isNew) {
                val owner = e.player.uniqueId
                database.createBackpack(backpack, owner)
            } else {
                database.setOpenId(backpack.id, null)
                database.save(backpack)
            }
        }
    }

    @SubscribeEvent
    fun onClick(e: InventoryClickEvent) {
        val holder = e.inventory.holder
        if (holder !is BackpackHolder) {
            return
        }
        if (e.click == ClickType.NUMBER_KEY) {
            val inv = e.clickedInventory ?: return
            if (inv.type != InventoryType.PLAYER
                && isDeny(e.whoClicked.inventory.getItem(e.hotbarButton))) {
                e.isCancelled = true
            }

        } else if (e.click == ClickType.SWAP_OFFHAND) {
            val inv = e.clickedInventory ?: return
            if (inv.type != InventoryType.PLAYER) {
                val item = e.whoClicked.inventory.itemInOffHand
                if (isDeny(item)) {
                    e.isCancelled = true
                }
            } else {
                val item = e.currentItem ?: return
                if (item.isSimilar(holder.item)) {
                    e.isCancelled = true
                }
            }
        } else if (isDeny(e.currentItem)) {
            e.isCancelled = true
        }
    }

    @SubscribeEvent
    fun onDrop(e: PlayerDropItemEvent) {
        val holder = e.player.openInventory.topInventory.holder
        if (holder is BackpackHolder) {
            val item = e.itemDrop.itemStack
            if (item.isSimilar(holder.item)) {
                e.isCancelled = true
            }
        }
    }

    private fun isDeny(item: ItemStack?): Boolean {
        return if (item.isAir()) {
            false
        } else if (item.type.toString().endsWith("SHULKER_BOX")) {
            true
        } else {
            item.isBackpack()
        }
    }

    private fun getTitle(item: ItemStack): String {
        return if (item.itemMeta?.hasDisplayName() == true) {
            item.itemMeta!!.displayName
        } else {
            "背包"
        }
    }

}