package org.delcom.repositories

import org.delcom.dao.FoodDAO
import org.delcom.entities.Food
import org.delcom.helpers.suspendTransaction
import org.delcom.helpers.foodDAOToModel
import org.delcom.tables.FoodTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.*

class FoodRepository : IFoodRepository {

    override suspend fun getAll(search: String): List<Food> = suspendTransaction {

        if (search.isBlank()) {
            FoodDAO
                .all()
                .orderBy(FoodTable.createdAt to SortOrder.DESC)
                .map(::foodDAOToModel)
        } else {
            val keyword = "%${search.lowercase()}%"

            FoodDAO
                .find {
                    FoodTable.name.lowerCase() like keyword
                }
                .orderBy(FoodTable.name to SortOrder.ASC)
                .map(::foodDAOToModel)
        }
    }

    override suspend fun getById(foodId: String): Food? = suspendTransaction {
        FoodDAO
            .find {
                FoodTable.id eq UUID.fromString(foodId)
            }
            .limit(1)
            .map(::foodDAOToModel)
            .firstOrNull()
    }

    override suspend fun create(food: Food): String = suspendTransaction {

        val foodDAO = FoodDAO.new {
            name = food.name
            description = food.description
            price = food.price
            category = food.category
            imageUrl = food.imageUrl
            isAvailable = food.isAvailable
            createdAt = food.createdAt
            updatedAt = food.updatedAt
        }

        foodDAO.id.value.toString()
    }

    override suspend fun update(foodId: String, newFood: Food): Boolean = suspendTransaction {

        val foodDAO = FoodDAO
            .find {
                FoodTable.id eq UUID.fromString(foodId)
            }
            .limit(1)
            .firstOrNull()

        if (foodDAO != null) {
            foodDAO.name = newFood.name
            foodDAO.description = newFood.description
            foodDAO.price = newFood.price
            foodDAO.category = newFood.category
            foodDAO.imageUrl = newFood.imageUrl
            foodDAO.isAvailable = newFood.isAvailable
            foodDAO.updatedAt = newFood.updatedAt
            true
        } else {
            false
        }
    }

    override suspend fun delete(foodId: String): Boolean = suspendTransaction {

        val rowsDeleted = FoodTable.deleteWhere {
            FoodTable.id eq UUID.fromString(foodId)
        }

        rowsDeleted >= 1
    }
}