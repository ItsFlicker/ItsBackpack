package io.github.itsflicker.backpack.util

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType
import taboolib.platform.util.bukkitPlugin

operator fun <T, Z> PersistentDataHolder.get(key: String, type: PersistentDataType<T, Z>): Z? {
    return persistentDataContainer.get(NamespacedKey(bukkitPlugin, key), type)
}

operator fun <T, Z : Any> PersistentDataHolder.set(key: String, type: PersistentDataType<T, Z>, value: Z) {
    persistentDataContainer.set(NamespacedKey(bukkitPlugin, key), type, value)
}

fun <T, Z : Any> PersistentDataHolder.has(key: String, type: PersistentDataType<T, Z>): Boolean {
    return persistentDataContainer.has(NamespacedKey(bukkitPlugin, key), type)
}

fun PersistentDataHolder.remove(key: String) {
    return persistentDataContainer.remove(NamespacedKey(bukkitPlugin, key))
}