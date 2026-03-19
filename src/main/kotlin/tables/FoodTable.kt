package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object FoodTable : UUIDTable("foods") {
    val name = varchar("name", 100)
    val description = text("description")
    val price = integer("price")
    val quantity = integer("quantity")
    val category = varchar("category", 50)
    val imageUrl = text("image_url").nullable()
    val available = bool("available").default(true)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}