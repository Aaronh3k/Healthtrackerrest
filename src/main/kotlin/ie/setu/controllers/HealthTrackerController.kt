package ie.setu.controllers

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.*
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
import kotlin.reflect.typeOf

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
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Activity>::class)])]
    )
    fun getAllActivities(ctx: Context) {
        val activities = activityDAO.getAll()
        if (activities.size != 0) {
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(activities)
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
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
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
            ctx.status(404)
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
        val userId = userDao.findById(activity.userId)
        if (userId != null) {
            val activityId = activityDAO.save(activity)
            activity.id = activityId
            ctx.json(activity)
            ctx.status(201)
        }
        else{
            ctx.status(404)
        }
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

//--------------------------------------------------------------
// CategoryDAOI specifics
//-------------------------------------------------------------
    @OpenApi(
        summary = "Get all Categories",
        operationId = "getAllCategories",
        tags = ["Category"],
        path = "/api/categories",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Category>::class)])]
    )
    fun getAllCategories(ctx: Context) {
       val categories = categoryDAO.getAll()
       if (categories.size != 0) {
           ctx.status(200)
       }
       else{
           ctx.status(404)
       }
       ctx.json(categories)
    }

@OpenApi(
    summary = "Get category by ID",
    operationId = "getCategoriesByCategoryId",
    tags = ["Category"],
    path = "/api/categories/{category-id}",
    method = HttpMethod.GET,
    pathParams = [OpenApiParam("category-id", Int::class, "The category ID")],
    responses  = [OpenApiResponse("200", [OpenApiContent(Category::class)])]
)
fun getCategoriesByCategoryId(ctx: Context) {

    val category = categoryDAO.findByCategoryId(ctx.pathParam("category-id").toInt())
    if (category != null) {
        ctx.json(category)
        ctx.status(200)
    }
    else{
        ctx.status(404)
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
    val category : Category = jsonToObject(ctx.body())
    category.created_at = DateTime.now()
    val categoryId = categoryDAO.save(category)
    if (categoryId != null) {
        category.id = categoryId
        ctx.json(category)
        ctx.status(201)
        }
    else{
        ctx.status(404)
    }
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
    if (categoryDAO.deleteByCategoryId(ctx.pathParam("category-id").toInt()) != 0)
        ctx.status(204)
    else
        ctx.status(404)
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
    if (categoryDAO.deleteByCategoryId(ctx.pathParam("category-id").toInt()) != 0)
        ctx.status(200)
    else
        ctx.status(404)
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
    val category : Category = jsonToObject(ctx.body())
    val categories = categoryDAO.findByCategoryId(ctx.pathParam("category-id").toInt())
    if (categories != null) {
        category.created_at = categories.created_at
    }
    else{
        ctx.status(404)
    }

    if (categoryDAO.updateByCategoryId(categoryId = ctx.pathParam("category-id").toInt(), categoryDTO =category) != 0)
        ctx.status(204)
    else
        ctx.status(404)
}

//-------------------------------------------------------------
// GoalDAOI specifics
//-------------------------------------------------------------
    @OpenApi(
        summary = "Get all Goals",
        operationId = "getAllGoals",
        tags = ["Goal"],
        path = "/api/goals",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Goal>::class)])]
    )
    fun getAllGoals(ctx: Context) {
    val goals = goalDAO.getAll()
    if (goals.size != 0) {
        ctx.status(200)
    }
    else{
        ctx.status(404)
    }
    ctx.json(goals)
}

    @OpenApi(
        summary = "Get goal by ID",
        operationId = "getGoalsByGoalId",
        tags = ["Goal"],
        path = "/api/goals/{goal-id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("goal-id", Int::class, "The goal ID")],
        responses  = [OpenApiResponse("200", [OpenApiContent(Goal::class)])]
    )
    fun getGoalsByGoalId(ctx: Context) {
        val goal = goalDAO.findByGoalId(ctx.pathParam("goal-id").toInt())
        if (goal != null) {
            ctx.json(goal)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }

    @OpenApi(
        summary = "Add Goal",
        operationId = "addGoals",
        tags = ["Goal"],
        path = "/api/goals",
        method = HttpMethod.POST,
        pathParams = [OpenApiParam("goal-id", Int::class, "The goal ID")],
        responses  = [OpenApiResponse("200")]
    )
    fun addGoals(ctx: Context) {
        val goal : Goal = jsonToObject(ctx.body())
        goal.created_at = DateTime.now()
        val userId = goalDAO.findByUserId(goal.userId)
        if (userId != null) {
            val goalId = goalDAO.save(goal)
            goal.id = goalId
            ctx.json(goal)
            ctx.status(201)
        }
        else{
            ctx.status(404)
        }
    }

    @OpenApi(
        summary = "Delete goal by ID",
        operationId = "deleteGoalByGoalId",
        tags = ["Goal"],
        path = "/api/goals/{goal-id}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("goal-id", Int::class, "The goal ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun deleteGoalByGoalId(ctx: Context){
        if (goalDAO.deleteByGoalId(ctx.pathParam("goal-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    @OpenApi(
        summary = "Delete goal by user ID",
        operationId = "deleteGoalsByUserId",
        tags = ["Goal"],
        path = "/api/users/{user-id}/goals}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("204", [OpenApiContent(Activity::class)])]
    )
    fun deleteGoalByUserId(ctx: Context){
        if(goalDAO.deleteByUserId(ctx.pathParam("user-id").toInt())!=0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    @OpenApi(
        summary = "Update goal by goal ID",
        operationId = "updateGoalByGoalId",
        tags = ["Goal"],
        path = "/api/goals/{goal-id}",
        method = HttpMethod.PATCH,
        pathParams = [OpenApiParam("goal-id", Int::class, "The goal ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun updateGoalByGoalId(ctx: Context){
        val goal : Goal = jsonToObject(ctx.body())
        val goals = goalDAO.findByGoalId(ctx.pathParam("goal-id").toInt())
        if (goals != null) {
            goal.created_at = goals.created_at
        }
        else{
            ctx.status(404)
        }

        if (goalDAO.updateByGoalId(goalId = ctx.pathParam("goal-id").toInt(), goalDTO =goal) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }
    @OpenApi(
        summary = "Get goal by user ID",
        operationId = "getGoalsByUserId",
        tags = ["Goal"],
        path = "/api/users/{user-id}/goals}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("200", [OpenApiContent(Activity::class)])]
    )
    fun getGoalsByUserId(ctx: Context) {
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            val goals = goalDAO.findByUserId(ctx.pathParam("user-id").toInt())
            if (goals.isNotEmpty()) {
                ctx.json(goals)
                ctx.status(200)
            }
            else{
                ctx.status(404)
            }
        }
        else{
            ctx.status(404)
        }
    }

//-------------------------------------------------------------
// ProfileDAOI specifics
//-------------------------------------------------------------
    @OpenApi(
        summary = "Get all UserProfile",
        operationId = "getAllUserProfile",
        tags = ["Profile"],
        path = "/api/userprofile",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Profile>::class)])]
    )
    fun getAllUserProfile(ctx: Context) {
        val userprofile = profileDAO.getAll()
        if (userprofile.size != 0) {
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    ctx.json(userprofile)
    }


    @OpenApi(
        summary = "Get profile by ID",
        operationId = "getUserProfileByProfileId",
        tags = ["Profile"],
        path = "/api/userprofile/{profile-id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("profile-id", Int::class, "The profile ID")],
        responses  = [OpenApiResponse("200", [OpenApiContent(Profile::class)])]
    )
    fun getUserProfileByProfileId(ctx: Context) {
        val userprofile = profileDAO.findByProfileId(ctx.pathParam("profile-id").toInt())
        if (userprofile != null) {
            ctx.json(userprofile)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }

    @OpenApi(
        summary = "Add Profile",
        operationId = "addUserProfile",
        tags = ["Profile"],
        path = "/api/userprofile",
        method = HttpMethod.POST,
        pathParams = [OpenApiParam("profile-id", Int::class, "The profile ID")],
        responses  = [OpenApiResponse("200")]
    )
    fun addUserProfile(ctx: Context) {
        val userprofile : Profile = jsonToObject(ctx.body())
        userprofile.created_at = DateTime.now()
        val userId = profileDAO.findByUserId(userprofile.userId)
        if (userId != null)
            ctx.status(204)
        else{
        val profileId = profileDAO.save(userprofile)
        if (profileId != null) {
            userprofile.id = profileId
            ctx.json(userprofile)
            ctx.status(201)
        }
        else{
            ctx.status(404)
        }
    }}

    @OpenApi(
        summary = "Delete profile by ID",
        operationId = "deleteProfileByProfileId",
        tags = ["Profile"],
        path = "/api/userprofile/{profile-id}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("profile-id", Int::class, "The profile ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun deleteProfileByProfileId(ctx: Context){
        if(profileDAO.deleteByProfileId(ctx.pathParam("profile-id").toInt())!=0)
            ctx.status(200)
        else
            ctx.status(404)
    }

    @OpenApi(
        summary = "Delete profile by user ID",
        operationId = "deleteProfileByUserId",
        tags = ["Profile"],
        path = "/api/userprofile/{profile-id}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("profile-id", Int::class, "The profile ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun deleteProfileByUserId(ctx: Context){
        if(profileDAO.deleteByUserId(ctx.pathParam("user-id").toInt())!=0)
            ctx.status(200)
        else
            ctx.status(404)
    }

    @OpenApi(
        summary = "Update profile by profile ID",
        operationId = "updateProfileByProfileId",
        tags = ["Profile"],
        path = "/api/userprofile/{profile-id}",
        method = HttpMethod.PATCH,
        pathParams = [OpenApiParam("profile-id", Int::class, "The profile ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun updateProfileByProfileId(ctx: Context){
        val profile : Profile = jsonToObject(ctx.body())
        val userId = profileDAO.findByUserId(profile.userId)
        if (userId != null)
            ctx.status(204)
        else{
        val userprofile = profileDAO.findByProfileId(ctx.pathParam("profile-id").toInt())
        if (userprofile != null) {
            profile.created_at = userprofile.created_at
        }
        else{
            ctx.status(404)
        }

        if (profileDAO.updateByProfileId(profileId = ctx.pathParam("profile-id").toInt(), profileDTO =profile) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }}

    @OpenApi(
        summary = "Get profile by user ID",
        operationId = "getUserProfileByUserId",
        tags = ["Profile"],
        path = "/api/users/{user-id}/userprofile}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("200", [OpenApiContent(Activity::class)])]
    )
    fun getUserProfileByUserId(ctx: Context) {
        if (profileDAO.findByUserId(ctx.pathParam("user-id").toInt()) != null) {
            val userprofile = profileDAO.findByUserId(ctx.pathParam("user-id").toInt())
            if (userprofile.isNotEmpty()) {
                ctx.json(userprofile)
                ctx.status(200)
            }
            else{
                ctx.status(404)
            }
        }
        else{
            ctx.status(404)
        }
    }
    @OpenApi(
        summary = "Delete profile by user ID",
        operationId = "deleteUserProfileByUserId",
        tags = ["Profile"],
        path = "/api/users/{user-id}/userprofile}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("204", [OpenApiContent(Activity::class)])]
    )
    fun deleteUserProfileByUserId(ctx: Context){
        if(profileDAO.deleteByUserId(ctx.pathParam("user-id").toInt())!=0)
            ctx.status(204)
        else
            ctx.status(404)
    }
}