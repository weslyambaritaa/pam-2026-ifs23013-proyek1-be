package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Food(
    var id: String = UUID.randomUUID().toString(),
    var name: String,
    var description: String,
    var price: Int,
    @SerialName("quantity") var quantity: Int = 0,
    var category: String,
    @SerialName("is_available") var isAvailable: Boolean = true, // 🔥 Kunci nama JSON
    var imageUrl: String? = null,

    @Contextual
    val createdAt: Instant = Clock.System.now(),

    @Contextual
    var updatedAt: Instant = Clock.System.now()
)