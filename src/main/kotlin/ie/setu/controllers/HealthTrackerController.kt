package ie.setu.controllers

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.Activity
import ie.setu.domain.Category
import ie.setu.domain.User
import ie.setu.domain.repository.ActivityDAO
import ie.setu.domain.repository.UserDAO
import ie.setu.domain.repository.CategoryDAO
import ie.setu.domain.repository.ProfileDAO
import ie.setu.domain.repository.GoalDAO
import ie.setu.utils.jsonToObject
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.*
import kong.unirest.json.JSONObject
import org.joda.time.DateTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object HealthTrackerController {

    private val userDao = UserDAO()
    private val activityDAO = ActivityDAO()
    private val categoryDAO = CategoryDAO()
    private val goalDAO = GoalDAO()
    private val profileDAO = ProfileDAO()

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

    @OpenApi(
        summary = "Add User",
        operationId = "addUser",
        tags = ["User"],
        path = "/api/users",
        method = HttpMethod.POST,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("200")]
    )
    fun addUser(ctx: Context) {
        val user : User = jsonToObject(ctx.body())
        val userId = userDao.save(user)
        if (userId != null && userId != 0) {
            user.id = userId
            ctx.json(user)
            ctx.status(201)
        }
        else{
            ctx.status(400)
        }
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
        if (userDao.delete(ctx.pathParam("user-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
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
        if ((userDao.update(id = ctx.pathParam("user-id").toInt(), user=foundUser)) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    //--------------------------------------------------------------
    // ActivityDAOI specifics
    //-------------------------------------------------------------
    @OpenApi(
        summary = "Get all Activities",
        operationId = "getAllActivities",
        tags = ["Activity"],
        path = "/api/activities",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<User>::class)])]
    )
    fun getAllActivities(ctx: Context) {
        //mapper handles the deserialization of Joda date into a String.
        val mapper = jacksonObjectMapper()
            .registerModule(JodaModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        ctx.json(mapper.writeValueAsString( activityDAO.getAll() ))
    }

    @OpenApi(
        summary = "Get activity by user ID",
        operationId = "getActivitiesByUserId",
        tags = ["Activity"],
        path = "/api/activities/user/{user-id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("200", [OpenApiContent(User::class)])]
    )
    fun getActivitiesByUserId(ctx: Context) {
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            val activities = activityDAO.findByUserId(ctx.pathParam("user-id").toInt())
            if (activities.isNotEmpty()) {
                //mapper handles the deserialization of Joda date into a String.
                val mapper = jacksonObjectMapper()
                    .registerModule(JodaModule())
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                ctx.json(mapper.writeValueAsString(activities))
            }
        }
    }

    @OpenApi(
        summary = "Add Activity",
        operationId = "addActivity",
        tags = ["Activity"],
        path = "/api/activities",
        method = HttpMethod.POST,
        pathParams = [OpenApiParam("activity-id", Int::class, "The activity ID")],
        responses  = [OpenApiResponse("200")]
    )
    fun addActivity(ctx: Context) {
        //mapper handles the serialisation of Joda date into a String.
        val mapper = jacksonObjectMapper()
            .registerModule(JodaModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        val map = JSONObject(ctx.body()).toMap()
        map["created_at"] = DateTime.now().toString()
        val activity = mapper.readValue<Activity>(JSONObject(map).toString())
        val activityId = activityDAO.save(activity)
        if (activityId != 0) {
            activity.id = activityId
            ctx.json(activity)
            ctx.status(201)
        }
        else{
            ctx.status(400)
        }
        ctx.json(activity)
    }

    @OpenApi(
        summary = "Get activity by ID",
        operationId = "getActivitiesById",
        tags = ["Activity"],
        path = "/api/activities/{activity-id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("activity-id", Int::class, "The activity ID")],
        responses  = [OpenApiResponse("200", [OpenApiContent(User::class)])]
    )
    fun getActivitiesByActivityId(ctx: Context) {
        val activity = activityDAO.findByActivityId((ctx.pathParam("activity-id").toInt()))
        if (activity != null){
            val mapper = jacksonObjectMapper()
                .registerModule(JodaModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            ctx.json(mapper.writeValueAsString(activity))
        }
    }

    @OpenApi(
        summary = "Delete activity by ID",
        operationId = "deleteActivityByActivityId",
        tags = ["Activity"],
        path = "/api/activities/{activity-id}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("activity-id", Int::class, "The activity ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun deleteActivityByActivityId(ctx: Context){
        activityDAO.deleteByActivityId(ctx.pathParam("activity-id").toInt())
    }

    @OpenApi(
        summary = "Delete activity by user ID",
        operationId = "deleteActivityByUserId",
        tags = ["Activity"],
        path = "/api/activities/{activity-id}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("activity-id", Int::class, "The activity ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun deleteActivityByUserId(ctx: Context){
        activityDAO.deleteByUserId(ctx.pathParam("user-id").toInt())
    }

    @OpenApi(
        summary = "Update activity by ID",
        operationId = "updateActivity",
        tags = ["Activity"],
        path = "/api/activities/{activity-id}",
        method = HttpMethod.PATCH,
        pathParams = [OpenApiParam("activity-id", Int::class, "The activity ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun updateActivity(ctx: Context){
        val mapper = jacksonObjectMapper()
            .registerModule(JodaModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        val map = JSONObject(ctx.body()).toMap()
        map["created_at"] = DateTime.now().toString()
        val activity = mapper.readValue<Activity>(JSONObject(map).toString())
        if (activityDAO.updateByActivityId(
            activityId = ctx.pathParam("activity-id").toInt(),
            activityDTO=activity)!=0)
            ctx.status(204)
        else
            ctx.status(404)
    }

//--------------------------------------------------------------
// CategoryDAOI specifics
//-------------------------------------------------------------
@OpenApi(
    summary = "Get all Categories",
    operationId = "getAllCategories",
    tags = ["Category"],
    path = "/api/categories",
    method = HttpMethod.GET,
    responses = [OpenApiResponse("200", [OpenApiContent(Array<User>::class)])]
)
fun getAllCategories(ctx: Context) {
    //mapper handles the deserialization of Joda date into a String.
    val mapper = jacksonObjectMapper()
        .registerModule(JodaModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    ctx.json(mapper.writeValueAsString( HealthTrackerController.categoryDAO.getAll() ))
}

@OpenApi(
    summary = "Get category by ID",
    operationId = "getCategoriesByCategoryId",
    tags = ["Category"],
    path = "/api/categories/{category-id}",
    method = HttpMethod.GET,
    pathParams = [OpenApiParam("category-id", Int::class, "The category ID")],
    responses  = [OpenApiResponse("200", [OpenApiContent(User::class)])]
)
fun getCategoriesByCategoryId(ctx: Context) {
    val category = HealthTrackerController.categoryDAO.findByCategoryId((ctx.pathParam("category-id").toInt()))
    if (category != null){
        val mapper = jacksonObjectMapper()
            .registerModule(JodaModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        ctx.json(mapper.writeValueAsString(category))
    }
}

@OpenApi(
    summary = "Add Category",
    operationId = "addCategories",
    tags = ["Category"],
    path = "/api/categories",
    method = HttpMethod.POST,
    pathParams = [OpenApiParam("category-id", Int::class, "The category ID")],
    responses  = [OpenApiResponse("200")]
)
fun addCategories(ctx: Context) {
    //mapper handles the serialisation of Joda date into a String.
    val mapper = jacksonObjectMapper()
        .registerModule(JodaModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    val map = JSONObject(ctx.body()).toMap()
    map["created_at"] = DateTime.now().toString()
    val category = mapper.readValue<Category>(JSONObject(map).toString())
    val categoryId = HealthTrackerController.categoryDAO.save(category)
    if (categoryId != 0) {
        category.id = categoryId
        ctx.json(category)
        ctx.status(201)
    }
    else{
        ctx.status(400)
    }
    ctx.json(category)
}

@OpenApi(
    summary = "Delete category by ID",
    operationId = "deleteCategoryByCategoryId",
    tags = ["Category"],
    path = "/api/categories/{category-id}",
    method = HttpMethod.DELETE,
    pathParams = [OpenApiParam("category-id", Int::class, "The category ID")],
    responses  = [OpenApiResponse("204")]
)
fun deleteCategoryByCategoryId(ctx: Context){
    HealthTrackerController.categoryDAO.deleteByCategoryId(ctx.pathParam("category-id").toInt())
}

@OpenApi(
    summary = "Delete category by user ID",
    operationId = "deleteCategoryByUserId",
    tags = ["Category"],
    path = "/api/categories/{category-id}",
    method = HttpMethod.DELETE,
    pathParams = [OpenApiParam("category-id", Int::class, "The category ID")],
    responses  = [OpenApiResponse("204")]
)
fun deleteCategoryByUserId(ctx: Context){
    HealthTrackerController.categoryDAO.deleteByCategoryId(ctx.pathParam("category-id").toInt())
}

@OpenApi(
    summary = "Update category by category ID",
    operationId = "updateCategoryByCategoryId",
    tags = ["Category"],
    path = "/api/categories/{category-id}",
    method = HttpMethod.PATCH,
    pathParams = [OpenApiParam("category-id", Int::class, "The category ID")],
    responses  = [OpenApiResponse("204")]
)
fun updateCategoryByCategoryId(ctx: Context){
    val mapper = jacksonObjectMapper()
        .registerModule(JodaModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    val map = JSONObject(ctx.body()).toMap()
    map["created_at"] = DateTime.now().toString()
    val category = mapper.readValue<Category>(JSONObject(map).toString())
    if (categoryDAO.updateByCategoryId(
            categoryId = ctx.pathParam("category-id").toInt(),
            categoryDTO = category) != 0)
        ctx.status(204)
    else
        ctx.status(404)
}
}