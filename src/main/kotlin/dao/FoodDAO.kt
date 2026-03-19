package org.delcom.dao

import org.delcom.tables.FoodTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import java.util.UUID

class FoodDAO(id: EntityID<UUID>) : Entity<UUID>(id) {

    companion object : EntityClass<UUID, FoodDAO>(FoodTable)

    var name by FoodTable.name
    var description by FoodTable.description
    var price by FoodTable.price
    var quantity by FoodTable.quantity
    var category by FoodTable.category
    var imageUrl by FoodTable.imageUrl
    var isAvailable by FoodTable.isAvailable
    var createdAt by FoodTable.createdAt
    var updatedAt by FoodTable.updatedAt
}