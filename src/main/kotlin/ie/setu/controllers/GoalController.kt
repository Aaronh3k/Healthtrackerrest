package ie.setu.controllers

import ie.setu.domain.Activity
import ie.setu.domain.Goal
import ie.setu.domain.repository.GoalDAO
import ie.setu.domain.repository.UserDAO
import ie.setu.utils.jsonToObject
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.*
import org.joda.time.DateTime

object GoalController {

    private val goalDAO = GoalDAO()
    private val userDao = UserDAO()

    @OpenApi(
        summary = "Get all Goals",
        operationId = "getAllGoals",
        tags = ["Goal"],
        path = "/api/goals",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Goal>::class)])]
    )
    fun getAllGoals(ctx: Context) {
        val goals = GoalController.goalDAO.getAll()
        if (goals.size != 0) {
            ctx.json(goals)
            ctx.status(200)
        }
        else{
            val arr = arrayOf<Int>()
            ctx.json(arr)
            ctx.status(204)
        }
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
        val goal = GoalController.goalDAO.findByGoalId(ctx.pathParam("goal-id").toInt())
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
        val userId = GoalController.goalDAO.findByUserId(goal.userId)
        if (userId != null) {
            val goalId = GoalController.goalDAO.save(goal)
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
        if (GoalController.goalDAO.deleteByGoalId(ctx.pathParam("goal-id").toInt()) != 0)
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
        if(GoalController.goalDAO.deleteByUserId(ctx.pathParam("user-id").toInt())!=0)
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
        val goals = GoalController.goalDAO.findByGoalId(ctx.pathParam("goal-id").toInt())
        if (goals != null) {
            goal.created_at = goals.created_at
        }
        else{
            ctx.status(404)
        }

        if (GoalController.goalDAO.updateByGoalId(goalId = ctx.pathParam("goal-id").toInt(), goalDTO =goal) != 0)
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
        if (GoalController.userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            val goals = GoalController.goalDAO.findByUserId(ctx.pathParam("user-id").toInt())
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
}