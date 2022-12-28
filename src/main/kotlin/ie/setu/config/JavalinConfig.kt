package ie.setu.config

import com.auth0.jwt.interfaces.DecodedJWT
import ie.setu.controllers.*
import ie.setu.domain.User
import ie.setu.utils.jsonObjectMapper
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.core.security.RouteRole
import io.javalin.http.Context
import io.javalin.http.ForbiddenResponse
import io.javalin.plugin.json.JavalinJackson
import io.javalin.plugin.openapi.ui.SwaggerOptions
import io.javalin.plugin.openapi.OpenApiOptions
import io.javalin.plugin.openapi.OpenApiPlugin
import io.javalin.plugin.openapi.ui.ReDocOptions
import io.javalin.plugin.rendering.vue.VueComponent
import io.swagger.v3.oas.models.info.Info
import ie.setu.utils.JwtProvider
import javax.management.relation.Role

internal enum class Roles : RouteRole {
    ANYONE, USER, ADMIN
}

private const val headerTokenName = "Authorization"

private val jwtProvider = JwtProvider()
class JavalinConfig() {

    fun startJavalinService(): Javalin {
        val app = Javalin.create {
            it.enableCorsForAllOrigins()
            it.registerPlugin(getConfiguredOpenApiPlugin())
            it.defaultContentType = "application/json"
            //added this jsonMapper for our integration tests - serialise objects to json
            it.jsonMapper(JavalinJackson(jsonObjectMapper()))
            it.enableWebjars()
        }.apply {
            exception(Exception::class.java) { e, _ -> e.printStackTrace() }
            error(404) { ctx -> ctx.json("404 - Not Found") }
        }.start(getRemoteAssignedPort())

        configure(app)
        registerRoutes(app)
        return app
    }

    private fun configure(app: Javalin) {
        app._conf.accessManager { handler, ctx, permittedRoles ->
            val jwtToken = getJwtTokenHeader(ctx)
            val userRole = getUserRole(jwtToken) ?: Roles.ANYONE
            permittedRoles.takeIf { !it.contains(userRole) }?.apply { throw ForbiddenResponse() }
            ctx.attribute("email", getEmail(jwtToken))
            handler.handle(ctx)
        }
    }
    private fun getRemoteAssignedPort(): Int {
        val herokuPort = System.getenv("PORT")
        return if (herokuPort != null) {
            Integer.parseInt(herokuPort)
        } else 7000
    }

    private fun getJwtTokenHeader(ctx: Context): DecodedJWT? {
        val tokenHeader = ctx.header(headerTokenName)?.substringAfter("Token")?.trim()
            ?: return null

        return jwtProvider.decodeJWT(tokenHeader)
    }

    private fun getEmail(jwtToken: DecodedJWT?): String? {
        return jwtToken?.subject
    }

    private fun getUserRole(jwtToken: DecodedJWT?): RouteRole? {
        val userRole = jwtToken?.getClaim("role")?.asString() ?: return null
        return Roles.valueOf(userRole)
    }
    private fun registerRoutes(app: Javalin) {
        app.routes {
            path("/api/register"){
                post(UserController::registerUser, Roles.ANYONE)
            }
            path("/api/login"){
                post(UserController::loginUser, Roles.ANYONE)
            }
            path("/api/users") {
                get(UserController::getAllUsers, Roles.ADMIN)
                path("{user-id}"){
                    get(UserController::getUserByUserId, Roles.ADMIN)
                    delete(UserController::deleteUser, Roles.ADMIN)
                    patch(UserController::updateUser, Roles.ADMIN)
                    path("activities"){
                        get(ActivityController::getActivitiesByUserId, Roles.ADMIN)
                        delete(ActivityController::deleteActivityByUserId, Roles.ADMIN)
                    }
                    path("goals"){
                        get(GoalController::getGoalsByUserId, Roles.USER, Roles.ADMIN)
                        delete(GoalController::deleteGoalByUserId, Roles.USER, Roles.ADMIN)
                    }
                    path("userprofile"){
                        get(ProfileController::getUserProfileByUserId, Roles.ADMIN)
                        delete(ProfileController::deleteProfileByUserId, Roles.ADMIN)
                    }
                }
                path("/email/{email}"){
                    get(UserController::getUserByEmail, Roles.ADMIN)
                }
            }
            path("/api/user"){
                get(UserController::getUserByToken, Roles.USER, Roles.ADMIN)
                patch(UserController::updateUser, Roles.USER, Roles.ADMIN)
                delete(UserController::deleteUser, Roles.USER, Roles.ADMIN)
                path("activity"){
                    get(ActivityController::getActivitiesByUserId, Roles.USER, Roles.ADMIN)
                    post(ActivityController::addActivityByUserId, Roles.USER, Roles.ADMIN)
                    delete(ActivityController::deleteActivityByUserId, Roles.USER, Roles.ADMIN)
                }
            }
            path("/api/activities") {
                get(ActivityController::getAllActivities, Roles.ANYONE, Roles.ADMIN)
                post(ActivityController::addActivity, Roles.USER, Roles.ADMIN)
                path("{activity-id}") {
                    get(ActivityController::getActivitiesByActivityId, Roles.USER, Roles.ADMIN)
                    delete(ActivityController::deleteActivityByActivityId, Roles.USER, Roles.ADMIN)
                    patch(ActivityController::updateActivity, Roles.USER, Roles.ADMIN)
                }
            }
            path("/api/categories"){
                get(CategoryController::getAllCategories, Roles.USER, Roles.ADMIN)
                post(CategoryController::addCategories, Roles.ADMIN)
                path("{category-id}") {
                    get(CategoryController::getCategoriesByCategoryId, Roles.USER, Roles.ADMIN)
                    delete(CategoryController::deleteCategoryByCategoryId, Roles.ADMIN)
                    patch(CategoryController::updateCategoryByCategoryId, Roles.ADMIN)
                }
            }
            path("/api/goals"){
                get(GoalController::getAllGoals, Roles.ADMIN)
                post(GoalController::addGoals, Roles.USER, Roles.ADMIN)
                path("{goal-id}") {
                    get(GoalController::getGoalsByGoalId, Roles.USER, Roles.ADMIN)
                    delete(GoalController::deleteGoalByGoalId, Roles.USER, Roles.ADMIN)
                    patch(GoalController::updateGoalByGoalId, Roles.USER, Roles.ADMIN)
                }
            }
            path("/api/profile"){
                get(ProfileController::getAllUserProfile, Roles.ADMIN)
                post(ProfileController::addUserProfile, Roles.USER, Roles.ADMIN)
                path("{profile-id}") {
                    get(ProfileController::getUserProfileByProfileId, Roles.USER, Roles.ADMIN)
                    delete(ProfileController::deleteProfileByProfileId, Roles.ADMIN)
                    patch(ProfileController::updateProfileByProfileId, Roles.USER, Roles.ADMIN)
                }
            }

            get("/", VueComponent("<home-page></home-page>"), Roles.ANYONE)
            get("/users", VueComponent("<user-overview></user-overview>"), Roles.ANYONE)
            get("/users/{user-id}", VueComponent("<user-profile></user-profile>"), Roles.ANYONE)
            get("/activities", VueComponent("<activities-overview></activities-overview>"), Roles.ANYONE)
            get("/activities/{activity-id}", VueComponent("<activities-profile></activities-profile>"), Roles.ANYONE)
            get("/users/{user-id}/activities", VueComponent("<user-activity-overview></user-activity-overview>"), Roles.ANYONE)

            path("/api/ui/users") {
                get(UserController::getAllUsers, Roles.ANYONE)
                path("{user-id}"){
                    get(UserController::getUserByUserId, Roles.ANYONE)
                    delete(UserController::deleteUser, Roles.ANYONE)
                    patch(UserController::updateUser, Roles.ANYONE)
                    path("activities"){
                        get(ActivityController::getActivitiesByUserId, Roles.ANYONE)
                        delete(ActivityController::deleteActivityByUserId, Roles.ANYONE)
                    }
                    path("goals"){
                        get(GoalController::getGoalsByUserId, Roles.ANYONE)
                        delete(GoalController::deleteGoalByUserId, Roles.ANYONE)
                    }
                    path("userprofile"){
                        get(ProfileController::getUserProfileByUserId, Roles.ANYONE)
                        delete(ProfileController::deleteProfileByUserId, Roles.ANYONE)
                    }
                }
                path("/email/{email}"){
                    get(UserController::getUserByEmail, Roles.ANYONE)
                }
            }
            path("/api/ui/activities") {
                get(ActivityController::getAllActivities, Roles.ANYONE)
                post(ActivityController::addActivity, Roles.ANYONE)
                path("{activity-id}") {
                    get(ActivityController::getActivitiesByActivityId, Roles.ANYONE)
                    delete(ActivityController::deleteActivityByActivityId, Roles.ANYONE)
                    patch(ActivityController::updateActivity, Roles.ANYONE)
                }
            }


            }
        }

    private fun getConfiguredOpenApiPlugin() = OpenApiPlugin(
        OpenApiOptions(
            Info().apply {
                title("Health Tracker App")
                version("1.0")
                description("Health Tracker API")
            }
        ).apply {
            path("/swagger-docs") // endpoint for OpenAPI json
            swagger(SwaggerOptions("/swagger-ui")) // endpoint for swagger-ui
            reDoc(ReDocOptions("/redoc")) // endpoint for redoc
        }
    )
}