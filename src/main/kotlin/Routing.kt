package org.delcom

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.delcom.data.AppException
import org.delcom.data.ErrorResponse
import org.delcom.helpers.JWTConstants
import org.delcom.helpers.parseMessageToMap
import org.delcom.services.TodoService
import org.delcom.services.AuthService
import org.delcom.services.UserService
import org.delcom.services.FoodService
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    val todoService: TodoService by inject()
    val authService: AuthService by inject()
    val userService: UserService by inject()
    val foodService: FoodService by inject()

    install(StatusPages) {

        // Tangkap AppException
        exception<AppException> { call, cause ->
            val dataMap: Map<String, List<String>> = parseMessageToMap(cause.message)

            call.respond(
                status = HttpStatusCode.fromValue(cause.code),
                message = ErrorResponse(
                    status = "fail",
                    message = if (dataMap.isEmpty()) cause.message else "Data yang dikirimkan tidak valid!",
                    data = if (dataMap.isEmpty()) null else dataMap.toString()
                )
            )
        }

        // Tangkap semua error lainnya
        exception<Throwable> { call, cause ->
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = ErrorResponse(
                    status = "error",
                    message = cause.message ?: "Unknown error",
                    data = ""
                )
            )
        }
    }

    routing {

        get("/") {
            call.respondText("API telah berjalan. Dibuat oleh Wesly Wanda.")
        }

        // ========================
        // AUTH ROUTES
        // ========================
        route("/auth") {

            post("/login") {
                authService.postLogin(call)
            }

            post("/register") {
                authService.postRegister(call)
            }

            post("/refresh-token") {
                authService.postRefreshToken(call)
            }

            post("/logout") {
                authService.postLogout(call)
            }
        }

        authenticate(JWTConstants.NAME) {

            // ========================
            // USER ROUTES
            // ========================
            route("/users") {

                get("/me") {
                    userService.getMe(call)
                }

                put("/me") {
                    userService.putMe(call)
                }

                put("/me/password") {
                    userService.putMyPassword(call)
                }

                put("/me/photo") {
                    userService.putMyPhoto(call)
                }
            }

            // ========================
            // TODO ROUTES
            // ========================
            route("/todos") {

                get {
                    todoService.getAll(call)
                }

                post {
                    todoService.post(call)
                }

                get("/{id}") {
                    todoService.getById(call)
                }

                put("/{id}") {
                    todoService.put(call)
                }

                put("/{id}/cover") {
                    todoService.putCover(call)
                }

                delete("/{id}") {
                    todoService.delete(call)
                }
            }

            // ========================
            // FOOD ROUTES
            // ========================
            route("/foods") {

                get {
                    foodService.getAll(call)
                }

                post {
                    foodService.post(call)
                }

                get("/{id}") {
                    foodService.getById(call)
                }

                put("/{id}") {
                    foodService.put(call)
                }

                put("/{id}/image") {
                    foodService.putImage(call)
                }

                delete("/{id}") {
                    foodService.delete(call)
                }
            }
        }

        // ========================
        // IMAGE ROUTES
        // ========================
        route("/images") {

            get("users/{id}") {
                userService.getPhoto(call)
            }

            get("todos/{id}") {
                todoService.getCover(call)
            }

            get("foods/{id}") {
                foodService.getImage(call)
            }
        }
    }
}