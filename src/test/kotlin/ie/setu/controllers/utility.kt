package ie.setu.controllers

import ie.setu.helpers.ServerContainer
import kong.unirest.HttpResponse
import kong.unirest.JsonNode
import kong.unirest.Unirest
import org.joda.time.DateTime

val app = ServerContainer.instance
val origin = "http://localhost:" + app.port()

//--------------------------------------------------------------------------------------
// HELPER METHODS
//--------------------------------------------------------------------------------------

//helper function to add a test user to the database
fun addUser (user_name: String, email: String, password: String, token: String, role: String): HttpResponse<JsonNode> {
    return Unirest.post("$origin/test/users")
        .body("{\"user_name\":\"$user_name\", \"email\":\"$email\", \"password\":\"$password\", \"token\":\"$token\", \"role\":\"$role\"}")
        .asJson()
}

//helper function to delete a test user from the database
fun deleteUser (id: Int): HttpResponse<String> {
    return Unirest.delete("$origin/test/users/$id").asString()
}

//helper function to retrieve a test user from the database by email
fun retrieveUserByEmail(email : String) : HttpResponse<String> {
    return Unirest.get(origin + "/test/users/email/${email}").asString()
}

//helper function to retrieve a test user from the database by id
fun retrieveUserById(id: Int) : HttpResponse<String> {
    return Unirest.get(origin + "/test/users/${id}").asString()
}

//helper function to add a test user to the database
fun updateUser (id: Int, user_name: String, email: String, password: String, token: String, role: String): HttpResponse<JsonNode> {
    return Unirest.patch("$origin/test/users/$id")
        .body("{\"user_name\":\"$user_name\", \"email\":\"$email\", \"password\":\"$password\", \"token\":\"$token\", \"role\":\"$role\"}")
        .asJson()
}

//helper function to retrieve all activities
fun retrieveAllActivities(): HttpResponse<JsonNode> {
    return Unirest.get(origin + "/test/activities").asJson()
}

//helper function to retrieve activities by user id
fun retrieveActivitiesByUserId(id: Int): HttpResponse<JsonNode> {
    return Unirest.get(origin + "/test/users/${id}/activities").asJson()
}

//helper function to retrieve activity by activity id
fun retrieveActivityByActivityId(id: Int): HttpResponse<JsonNode> {
    return Unirest.get(origin + "/test/activities/${id}").asJson()
}

//helper function to delete an activity by activity id
fun deleteActivityByActivityId(id: Int): HttpResponse<String> {
    return Unirest.delete("$origin/test/activities/$id").asString()
}

//helper function to delete an activity by activity id
fun deleteActivitiesByUserId(id: Int): HttpResponse<String> {
    return Unirest.delete("$origin/test/users/$id/activities").asString()
}

//helper function to add a test user to the database
fun updateActivity(id: Int, description: String, duration: Double, calories: Double,
                           started: DateTime, userId: Int,
                           categoryId: Int, distance: Double, created_at: DateTime): HttpResponse<JsonNode> {
    return Unirest.patch("$origin/test/activities/$id")
        .body("""
                {
                   "description":"$description",
                   "duration":$duration,
                   "calories":$calories,
                   "started":"$started",
                   "userId":$userId,
                   "categoryId":$categoryId,
                   "distance":$distance,
                   "created_at":"$created_at"
                }
            """.trimIndent()).asJson()
}

//helper function to add an activity
fun addActivity(
    description: String, duration: Double, calories: Double,
    started: DateTime, userId: Int, categoryId: Int, distance: Double, created_at: DateTime): HttpResponse<JsonNode> {
    return Unirest.post("$origin/test/activities")
        .body("""
                {
                   "description":"$description",
                   "duration":$duration,
                   "calories":$calories,
                   "started":"$started",
                   "userId":$userId,
                   "categoryId":$categoryId,
                   "distance":$distance,
                   "created_at":"$created_at"
                }
            """.trimIndent())
        .asJson()
}

//helper function to retrieve all categories
fun retrieveAllCategories(): HttpResponse<JsonNode> {
    return Unirest.get("$origin/test/categories").asJson()
}

//helper function to retrieve category by category id
fun retrieveCategoryById(id: Int): HttpResponse<JsonNode> {
    return Unirest.get(origin + "/test/categories/${id}").asJson()
}

//helper function to delete a category by category id
fun deleteCategoryByCategoryId(id: Int): HttpResponse<String> {
    return Unirest.delete("$origin/test/categories/$id").asString()
}

//helper function to update a category to the database
fun updateCategory(id: Int, name: String, description: String): HttpResponse<JsonNode> {
    return Unirest.patch("$origin/test/categories/$id")
        .body("""
                {
                   "name":"$name",
                   "description":"$description",
                   "created_at":"${DateTime.now()}"
                }
            """.trimIndent()).asJson()
}

//helper function to add a category
fun addCategory(
    name: String, description: String): HttpResponse<JsonNode> {
    return Unirest.post("$origin/test/categories")
        .body("""
                {
                   "name":"$name",
                   "description":"$description",
                   "created_at":"${DateTime.now()}"
                }
            """.trimIndent())
        .asJson()
}

//helper function to retrieve all goals
fun retrieveAllGoals(): HttpResponse<JsonNode> {
    return Unirest.get("$origin/test/goals").asJson()
}

//helper function to retrieve goal by goal id
fun retrieveGoalById(id: Int): HttpResponse<JsonNode> {
    return Unirest.get(origin + "/test/goals/${id}").asJson()
}

//helper function to retrieve goals by user id
fun retrieveGoalsByUserId(id: Int): HttpResponse<JsonNode> {
    return Unirest.get(origin + "/test/users/${id}/goals").asJson()
}

//helper function to delete a goal by goal id
fun deleteGoalByGoaId(id: Int): HttpResponse<String> {
    return Unirest.delete("$origin/test/goals/$id").asString()
}

//helper function to delete an goal by user id
fun deleteGoalsByUserId(id: Int): HttpResponse<String> {
    return Unirest.delete("$origin/test/users/$id/goals").asString()
}

//helper function to update a goal to the database
fun updateGoal(id: Int, userId: Int, calories: Int, standing_hours: Double, steps: Int,
distance: Int): HttpResponse<JsonNode> {
    return Unirest.patch("$origin/test/goals/$id")
        .body("""
                {
                   "userId":$userId,
                   "calories":"$calories",
                   "standing_hours":"$standing_hours",
                   "steps":"$steps",
                   "distance":"$distance",
                   "created_at":"${DateTime.now()}"
                }
            """.trimIndent()).asJson()
}

//helper function to add a Goal
fun addGoal(
    userId: Int, calories: Int, standing_hours: Double, steps: Int,
    distance: Int): HttpResponse<JsonNode> {
    return Unirest.post("$origin/test/goals")
        .body("""
                {
                   "userId":$userId,
                   "calories":"$calories",
                   "standing_hours":"$standing_hours",
                   "steps":"$steps",
                   "distance":"$distance",
                   "created_at":"${DateTime.now()}"
                }
            """.trimIndent())
        .asJson()
}

//helper function to add a test userprofile to the database
fun addUserProfile(userId: Int, first_name: String, last_name: String, dob: DateTime, gender: Char,
                   created_at: DateTime): HttpResponse<JsonNode> {
    return Unirest.post("$origin/test/userprofile")
        .body("""
                {
                   "first_name":"$first_name",
                   "last_name":$last_name,
                   "dob":$dob,
                   "gender":"$gender",
                   "userId":$userId,
                   "created_at":"$created_at"
                }
            """.trimIndent())
        .asJson()
}
