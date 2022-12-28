package ie.setu.controllers

import ie.setu.domain.*
import ie.setu.domain.repository.ActivityDAO
import ie.setu.utils.jsonToObject
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.*
import org.joda.time.DateTime
import ie.setu.domain.repository.UserDAO
import kotlin.reflect.typeOf

object ActivityController {

    private val activityDAO = ActivityDAO()
    private val userDao = UserDAO()

    @OpenApi(
        summary = "Get all Activities",
        operationId = "getAllActivities",
        tags = ["Activity"],
        path = "/api/activities",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Activity>::class)])]
    )
    fun getAllActivities(ctx: Context) {
        val activities = activityDAO.getAll()
        if (activities.size != 0) {
            ctx.json(activities)
            ctx.status(200)
        }
        else{
            val arr = arrayOf<Int>()
            ctx.json(arr)
            ctx.status(204)
        }
    }

    @OpenApi(
        summary = "Get activity by user ID",
        operationId = "getActivitiesByUserId",
        tags = ["Activity"],
        path = "/api/activities/user/{user-id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("200", [OpenApiContent(Activity::class)])]
    )
    fun getActivitiesByUserId(ctx: Context) {
        if (ctx.pathParamMap().get("user-id") != null) {
            val activities = activityDAO.findByUserId(ctx.pathParam("user-id").toInt())
            if (activities.isNotEmpty()) {
                ctx.json(activities)
                ctx.status(200)
            }
            else{
                ctx.status(404)
            }
        }
        else{
            val email = ctx.attribute<String>("email")
            if (email != null) {
                val activities = userDao.findUserIdByEmail(email)?.id?.let { activityDAO.findByUserId(it) }
            }
            ctx.status(200)
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
        val activity : Activity = jsonToObject(ctx.body())
        activity.created_at = DateTime.now()
        val userId = activity.userId?.let { ActivityController.userDao.findById(it) }
        if (userId != null) {
            val activityId = activityDAO.save(activity)
            activity.id = activityId
            ctx.json(activity)
            ctx.status(201)
        }
        else{
            ctx.status(400)
        }
    }

    fun addActivityByUserId(ctx: Context) {
        val activity : Activity = jsonToObject(ctx.body())
        activity.created_at = DateTime.now()
        val email = ctx.attribute<String>("email")
        activity.userId = email?.let { userDao.findUserIdByEmail(it)?.id?.toInt() } ?: -1
        if (activity.userId != -1){
            val activityId = activityDAO.save(activity)
            activity.id = activityId
            ctx.json(activity)
            ctx.status(201)
            return
        }
        ctx.status(400)
    }

    @OpenApi(
        summary = "Get activity by ID",
        operationId = "getActivitiesById",
        tags = ["Activity"],
        path = "/api/activities/{activity-id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("activity-id", Int::class, "The activity ID")],
        responses  = [OpenApiResponse("200", [OpenApiContent(Activity::class)])]
    )
    fun getActivitiesByActivityId(ctx: Context) {
        val activity = activityDAO.findByActivityId((ctx.pathParam("activity-id").toInt()))
        if (activity != null){
            ctx.json(activity)
            ctx.status(200)
        }
        else{
            ctx.status(404)
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
        if (activityDAO.deleteByActivityId(ctx.pathParam("activity-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
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
        if (activityDAO.deleteByUserId(ctx.pathParam("user-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
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
        val activity : Activity = jsonToObject(ctx.body())
        val activities = activityDAO.findByActivityId(ctx.pathParam("activity-id").toInt())
        if (activities != null) {
            activity.created_at = activities.created_at
        }
        else{
            ctx.status(404)
        }

        if (activityDAO.updateByActivityId(activityId = ctx.pathParam("activity-id").toInt(), activityDTO =activity) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }
}