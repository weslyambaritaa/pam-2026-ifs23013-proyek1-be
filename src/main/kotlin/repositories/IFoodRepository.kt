package org.delcom.repositories

import org.delcom.entities.Food

interface IFoodRepository {
    suspend fun getAll(search: String): List<Food>
    suspend fun getById(foodId: String): Food?
    suspend fun create(food: Food): String
    suspend fun update(foodId: String, newFood: Food): Boolean
    suspend fun delete(foodId: String): Boolean
}