package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.FoodRequest
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IFoodRepository
import java.io.File
import java.util.*

class FoodService(
    private val foodRepo: IFoodRepository
) {

    // Ambil semua makanan
    suspend fun getAll(call: ApplicationCall) {

        val search = call.request.queryParameters["search"] ?: ""

        val foods = foodRepo.getAll(search)

        val response = DataResponse(
            "success",
            "Berhasil mengambil daftar makanan",
            mapOf("foods" to foods)
        )

        call.respond(response)
    }

    // Ambil makanan by id
    suspend fun getById(call: ApplicationCall) {

        val foodId = call.parameters["id"]
            ?: throw AppException(400, "Data makanan tidak valid!")

        val food = foodRepo.getById(foodId)
            ?: throw AppException(404, "Data makanan tidak tersedia!")

        val response = DataResponse(
            "success",
            "Berhasil mengambil data makanan",
            mapOf("food" to food)
        )

        call.respond(response)
    }

    // Upload gambar makanan
    suspend fun putImage(call: ApplicationCall) {

        val foodId = call.parameters["id"]
            ?: throw AppException(400, "Data makanan tidak valid!")

        val request = FoodRequest()

        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)

        multipartData.forEachPart { part ->

            when (part) {

                is PartData.FileItem -> {

                    val ext = part.originalFileName
                        ?.substringAfterLast('.', "")
                        ?.let { if (it.isNotEmpty()) ".$it" else "" }
                        ?: ""

                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/foods/$fileName"

                    withContext(Dispatchers.IO) {

                        val file = File(filePath)
                        file.parentFile.mkdirs()

                        part.streamProvider().use { input ->
                            file.outputStream().buffered().use { output ->
                                input.copyTo(output)
                            }
                        }

                        request.imageUrl = filePath
                    }
                }

                else -> {}
            }

            part.dispose()
        }

        if (request.imageUrl == null) {
            throw AppException(400, "Gambar makanan tidak tersedia!")
        }

        val oldFood = foodRepo.getById(foodId)
            ?: throw AppException(404, "Data makanan tidak tersedia!")

        request.name = oldFood.name
        request.description = oldFood.description
        request.price = oldFood.price
        request.quantity = oldFood.quantity
        request.category = oldFood.category
        request.isAvailable = oldFood.isAvailable

        val isUpdated = foodRepo.update(
            foodId,
            request.toEntity()
        )

        if (!isUpdated) {
            throw AppException(400, "Gagal memperbarui gambar makanan!")
        }

        if (oldFood.imageUrl != null) {
            val oldFile = File(oldFood.imageUrl!!)
            if (oldFile.exists()) {
                oldFile.delete()
            }
        }

        val response = DataResponse(
            "success",
            "Berhasil mengubah gambar makanan",
            null
        )

        call.respond(response)
    }

    // Tambah makanan
    suspend fun post(call: ApplicationCall) {

        val request = call.receive<FoodRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("name", "Nama makanan tidak boleh kosong")
        validator.required("description", "Deskripsi tidak boleh kosong")
        validator.required("price", "Harga tidak boleh kosong")
        validator.required("quantity", "Kuantitas tidak boleh kosong")
        validator.required("category", "Kategori tidak boleh kosong")
        validator.validate()

        val foodId = foodRepo.create(
            request.toEntity()
        )

        val response = DataResponse(
            "success",
            "Berhasil menambahkan menu makanan",
            mapOf("foodId" to foodId)
        )

        call.respond(response)
    }

    // Update makanan
    suspend fun put(call: ApplicationCall) {

        val foodId = call.parameters["id"]
            ?: throw AppException(400, "Data makanan tidak valid!")

        val request = call.receive<FoodRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("name", "Nama makanan tidak boleh kosong")
        validator.required("description", "Deskripsi tidak boleh kosong")
        validator.required("price", "Harga tidak boleh kosong")
        validator.required("quantity", "Kuantitas tidak boleh kosong")
        validator.required("category", "Kategori tidak boleh kosong")
        validator.validate()

        val oldFood = foodRepo.getById(foodId)
            ?: throw AppException(404, "Data makanan tidak tersedia!")

        request.imageUrl = oldFood.imageUrl

        val isUpdated = foodRepo.update(
            foodId,
            request.toEntity()
        )

        if (!isUpdated) {
            throw AppException(400, "Gagal memperbarui data makanan!")
        }

        val response = DataResponse(
            "success",
            "Berhasil mengubah data makanan",
            null
        )

        call.respond(response)
    }

    // Hapus makanan
    suspend fun delete(call: ApplicationCall) {

        val foodId = call.parameters["id"]
            ?: throw AppException(400, "Data makanan tidak valid!")

        val oldFood = foodRepo.getById(foodId)
            ?: throw AppException(404, "Data makanan tidak tersedia!")

        val isDeleted = foodRepo.delete(foodId)

        if (!isDeleted) {
            throw AppException(400, "Gagal menghapus data makanan!")
        }

        if (oldFood.imageUrl != null) {

            val oldFile = File(oldFood.imageUrl!!)

            if (oldFile.exists()) {
                oldFile.delete()
            }
        }

        val response = DataResponse(
            "success",
            "Berhasil menghapus menu makanan",
            null
        )

        call.respond(response)
    }

    // Ambil gambar makanan
    suspend fun getImage(call: ApplicationCall) {

        val foodId = call.parameters["id"]
            ?: throw AppException(400, "Data makanan tidak valid!")

        val food = foodRepo.getById(foodId)
            ?: return call.respond(HttpStatusCode.NotFound)

        if (food.imageUrl == null) {
            throw AppException(404, "Makanan belum memiliki gambar")
        }

        val file = File(food.imageUrl!!)

        if (!file.exists()) {
            throw AppException(404, "Gambar makanan tidak tersedia")
        }

        call.respondFile(file)
    }
}