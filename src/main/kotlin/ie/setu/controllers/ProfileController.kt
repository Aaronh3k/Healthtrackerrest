package ie.setu.controllers

import ie.setu.domain.Activity
import ie.setu.domain.Profile
import ie.setu.domain.User
import ie.setu.domain.repository.ProfileDAO
import ie.setu.utils.jsonToObject
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.*
import org.joda.time.DateTime


object ProfileController {

    val profileDAO = ProfileDAO()

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
            ctx.json(userprofile)
            ctx.status(200)
        }
        else{
            val arr = arrayOf<Int>()
            ctx.json(arr)
            ctx.status(204)
        }
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
        val userprofile = ProfileController.profileDAO.findByProfileId(ctx.pathParam("profile-id").toInt())
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
        val email = ctx.attribute<String>("email")
        val fuser = email?.let { UserController.userDao.findByEmail(it) }
        val userprofile : Profile = jsonToObject(ctx.body())
        if (fuser != null) {
            userprofile.userId = fuser.id
        }
        userprofile.created_at = DateTime.now()
        val user = UserController.userDao.findById(userprofile.userId)
        if (user == null)
            ctx.status(204)
        else{
            val profileId = profileDAO.save(userprofile)
            if (profileId != 0) {
                userprofile.id = profileId
                ctx.json(userprofile)
                ctx.status(201)
            }
            else{
                ctx.status(204)
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
        if(ProfileController.profileDAO.deleteByProfileId(ctx.pathParam("profile-id").toInt())!=0)
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
        if(ProfileController.profileDAO.deleteByUserId(ctx.pathParam("user-id").toInt())!=0)
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
    fun updateProfile(ctx: Context){
        val foundprofile : Profile = jsonToObject(ctx.body())
        var st = 0
        if (ctx.pathParamMap().get("profile-id") == null) {
            val email = ctx.attribute<String>("email")
            val user = email?.let { UserController.userDao.findByEmail(it) }
            val userprofile = user?.id?.let { profileDAO.findByUserId(it) }
            if (userprofile != null) {
                if (userprofile.id?.let {
                        profileDAO.updateByProfileId(
                            profileId = it,
                            profileDTO = foundprofile
                        )
                    } != 0) {
                    ctx.json(mapOf("message" to "UPDATED"))
                    ctx.status(201)
                    return
                } else {
                    ctx.json(mapOf("message" to "NOT UPDATED"))
                    ctx.status(204)
                    return
                }
            }
        }

        else{
            st = profileDAO.updateByProfileId(profileId = ctx.pathParam("profile-id").toInt(), profileDTO = foundprofile)

            if (st != 0){
                ctx.json(mapOf("message" to "UPDATED"))
                ctx.status(200)
                return}
            else{
                ctx.json(mapOf("message" to "NOT UPDATED"))
                ctx.status(400)
                return}
        }
    }

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
        if (ProfileController.profileDAO.findByUserId(ctx.pathParam("user-id").toInt()) != null) {
            val userprofile = ProfileController.profileDAO.findByUserId(ctx.pathParam("user-id").toInt())
                if (userprofile != null) {
                    ctx.json(userprofile)
                    ctx.status(200)
                }
                else{
                    ctx.status(204)
            }
        }
        else{
            ctx.status(204)
        }
    }

//    fun updateUserProfileByUserId(ctx: Context) {
//        if (profileDAO.findByUserId(ctx.pathParam("user-id").toInt()) != null) {
//            val userprofile = profileDAO.findByUserId(ctx.pathParam("user-id").toInt())
//            if (userprofile != null) {
//                if (profileDAO.updateByProfileId(profileId = ctx.pathParam("profile-id").toInt(), profileDTO = userprofile) != 0)
//                    ctx.status(201)
//                else
//                    ctx.status(204)
//            }
//        }
//        else{
//            ctx.status(204)
//        }
//    }

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
        if(ProfileController.profileDAO.deleteByUserId(ctx.pathParam("user-id").toInt())!=0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    fun getProfileByToken(ctx: Context) {
        val email = ctx.attribute<String>("email")
        val user = email?.let { UserController.userDao.findByEmail(it) }

        val userprofile = user?.id?.let { profileDAO.findByUserId(it) }
        if (userprofile != null) {
            ctx.json(userprofile)
            ctx.status(200)
        }
        else{
            ctx.status(204)
        }
    }
}