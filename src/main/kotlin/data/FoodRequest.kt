package org.delcom.data

import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.delcom.entities.Food

@Serializable
data class FoodRequest(
    var name: String = "",
    var description: String = "",
    var price: Int = 0,
    var quantity: Int = 0,
    var category: String = "",
    var imageUrl: String? = null,

    // 🔥 SAMAKAN DI SINI
    @SerialName("is_available")
    var available: Boolean = true
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "description" to description,
            "price" to price,
            "quantity" to quantity,
            "category" to category,
            "imageUrl" to imageUrl,
            "is_available" to available // Samakan nama key-nya
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
            available = available,
            updatedAt = Clock.System.now()
        )
    }
}