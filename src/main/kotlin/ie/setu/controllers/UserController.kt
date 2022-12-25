package ie.setu.controllers

import ie.setu.domain.*
import ie.setu.domain.repository.UserDAO
import ie.setu.utils.jsonToObject
import io.javalin.http.Context
import io.javalin.http.HttpResponseException
import io.javalin.plugin.openapi.annotations.*
import org.eclipse.jetty.http.HttpStatus
import java.util.*
import ie.setu.utils.Cipher
import io.javalin.http.UnauthorizedResponse

object UserController {

    private val userDao = UserDAO()
    private val base64Encoder = Base64.getEncoder()

    @OpenApi(
        summary = "Get all users",
        operationId = "getAllUsers",
        tags = ["User"],
        path = "/api/users",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<User>::class)])]
    )
    fun getAllUsers(ctx: Context) {
        val users = userDao.getAll()
        if (users.size != 0) {
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(users)
    }

    @OpenApi(
        summary = "Get user by ID",
        operationId = "getUserById",
        tags = ["User"],
        path = "/api/users/{user-id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("200", [OpenApiContent(User::class)])]
    )
    fun getUserByUserId(ctx: Context) {
        val user = userDao.findById(ctx.pathParam("user-id").toInt())
        if (user != null) {
            ctx.json(user)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }

    fun getUserByToken(ctx: Context) {
        val email = ctx.attribute<String>("email")
        val user = email?.let { userDao.findByEmail(it) }
        if (user != null) {
            ctx.json(user)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }

    @OpenApi(
        summary = "Add User",
        operationId = "addUser",
        tags = ["User"],
        path = "/api/users",
        method = HttpMethod.POST,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("200")]
    )
    fun registerUser(ctx: Context) {
        val user : User = jsonToObject(ctx.body())
        userDao.findByEmail(user.email).takeIf { it != null }?.apply {
            throw HttpResponseException(
                HttpStatus.BAD_REQUEST_400,
                "Email already registered!")
        }
        val userId = userDao.create(user.copy(password = String(base64Encoder.encode(Cipher.encrypt(user.password)))))
        if (userId != null && userId != 0) {
            user.id = userId
            user.token = user.copy(token = userDao.generateJwtToken(user)).token.toString()
            ctx.json(user)
            ctx.status(201)
        }
        else{
            ctx.status(400)
        }
    }
    fun loginUser(ctx: Context){
        val user : User = jsonToObject(ctx.body())
        val userfound = userDao.findByEmail(user.email)
        if (userfound?.password == String(base64Encoder.encode(Cipher.encrypt(user?.password)))) {
            val token = mapOf("key" to user.copy(token = userDao.generateJwtToken(user)).token.toString())
            ctx.json(token)
        }else throw UnauthorizedResponse("email or password invalid!")
    }
    @OpenApi(
        summary = "Get user by Email",
        operationId = "getUserByEmail",
        tags = ["User"],
        path = "/api/users/email/{email}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("email", Int::class, "The user email")],
        responses  = [OpenApiResponse("200", [OpenApiContent(User::class)])]
    )
    fun getUserByEmail(ctx: Context) {
        val user = userDao.findByEmail(ctx.pathParam("email"))
        if (user != null) {
            ctx.json(user)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }

    @OpenApi(
        summary = "Delete user by ID",
        operationId = "deleteUserById",
        tags = ["User"],
        path = "/api/users/{user-id}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun deleteUser(ctx: Context){
        var st = 0
        if (ctx.pathParamMap().get("user-id") == null){
            val email = ctx.attribute<String>("email")
            if (email != null)
                st = userDao.deletebyemail(email = email)
        }
        else
            st= userDao.delete(ctx.pathParam("user-id").toInt())

        if (st != 0){
            ctx.json(mapOf("message" to "DELETED"))
            ctx.status(200)}
        else{
            ctx.json(mapOf("message" to "DELETED"))
            ctx.status(400)}
    }

    @OpenApi(
        summary = "Update user by ID",
        operationId = "updateUserById",
        tags = ["User"],
        path = "/api/users/{user-id}",
        method = HttpMethod.PATCH,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun updateUser(ctx: Context){
        val foundUser : User = jsonToObject(ctx.body())
        var st = 0
        if (ctx.pathParamMap().get("user-id") == null){
            val email = ctx.attribute<String>("email")
            if (email != null)
                st = userDao.updatebyemail(Email = email, user=foundUser)
            }
            else
                st = userDao.update(id = ctx.pathParam("user-id").toInt(), user=foundUser)

        if (st != 0){
            ctx.json(mapOf("message" to "UPDATED"))
            ctx.status(200)}
        else{
            ctx.json(mapOf("message" to "NOT UPDATED"))
            ctx.status(400)}
        }
}