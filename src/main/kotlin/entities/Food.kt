package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Food(
    var id: String = UUID.randomUUID().toString(),

    var name: String,
    var description: String,
    var price: Int,
    var category: String, // makanan, minuman, snack
    var isAvailable: Boolean = true,
    var imageUrl: String? = null,

    @Contextual
    val createdAt: Instant = Clock.System.now(),

    @Contextual
    var updatedAt: Instant = Clock.System.now()
)