package org.delcom.data

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import org.delcom.entities.Food

@Serializable
data class FoodRequest(
    var name: String = "",
    var description: String = "",
    var price: Int = 0,
    var quantity: Int = 0, // Pastikan ini ada!
    var category: String = "",
    var imageUrl: String? = null,
    var isAvailable: Boolean = true // Pastikan ini ada!
){

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "description" to description,
            "price" to price,
            "quantity" to quantity,
            "category" to category,
            "imageUrl" to imageUrl,
            "isAvailable" to isAvailable
        )
    }

    fun toEntity(): Food {
        return Food(
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            category = category,
            imageUrl = imageUrl,
            isAvailable = isAvailable,
            updatedAt = Clock.System.now()
        )
    }
}