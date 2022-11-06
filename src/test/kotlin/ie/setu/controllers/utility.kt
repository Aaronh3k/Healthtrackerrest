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
fun addUser (user_name: String, email: String): HttpResponse<JsonNode> {
    return Unirest.post("$origin/api/users")
        .body("{\"user_name\":\"$user_name\", \"email\":\"$email\"}")
        .asJson()
}

//helper function to delete a test user from the database
fun deleteUser (id: Int): HttpResponse<String> {
    return Unirest.delete("$origin/api/users/$id").asString()
}

//helper function to retrieve a test user from the database by email
fun retrieveUserByEmail(email : String) : HttpResponse<String> {
    return Unirest.get(origin + "/api/users/email/${email}").asString()
}

//helper function to retrieve a test user from the database by id
fun retrieveUserById(id: Int) : HttpResponse<String> {
    return Unirest.get(origin + "/api/users/${id}").asString()
}

//helper function to add a test user to the database
fun updateUser (id: Int, user_name: String, email: String): HttpResponse<JsonNode> {
    return Unirest.patch("$origin/api/users/$id")
        .body("{\"user_name\":\"$user_name\", \"email\":\"$email\"}")
        .asJson()
}

//helper function to retrieve all activities
fun retrieveAllActivities(): HttpResponse<JsonNode> {
    return Unirest.get(origin + "/api/activities").asJson()
}

//helper function to retrieve activities by user id
fun retrieveActivitiesByUserId(id: Int): HttpResponse<JsonNode> {
    return Unirest.get(origin + "/api/users/${id}/activities").asJson()
}

//helper function to retrieve activity by activity id
fun retrieveActivityByActivityId(id: Int): HttpResponse<JsonNode> {
    return Unirest.get(origin + "/api/activities/${id}").asJson()
}

//helper function to delete an activity by activity id
fun deleteActivityByActivityId(id: Int): HttpResponse<String> {
    return Unirest.delete("$origin/api/activities/$id").asString()
}

//helper function to delete an activity by activity id
fun deleteActivitiesByUserId(id: Int): HttpResponse<String> {
    return Unirest.delete("$origin/api/users/$id/activities").asString()
}

//helper function to add a test user to the database
fun updateActivity(id: Int, description: String, duration: Double, calories: Double,
                           started: DateTime, userId: Int,
                           categoryId: Int, distance: Double, created_at: DateTime): HttpResponse<JsonNode> {
    return Unirest.patch("$origin/api/activities/$id")
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
    return Unirest.post("$origin/api/activities")
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
    return Unirest.get("$origin/api/categories").asJson()
}

//helper function to retrieve category by category id
fun retrieveCategoryById(id: Int): HttpResponse<JsonNode> {
    return Unirest.get(origin + "/api/categories/${id}").asJson()
}

//helper function to delete a category by category id
fun deleteCategoryByCategoryId(id: Int): HttpResponse<String> {
    return Unirest.delete("$origin/api/categories/$id").asString()
}

//helper function to update a category to the database
fun updateCategory(id: Int, name: String, description: String): HttpResponse<JsonNode> {
    return Unirest.patch("$origin/api/categories/$id")
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
    return Unirest.post("$origin/api/categories")
        .body("""
                {
                   "name":"$name",
                   "description":"$description",
                   "created_at":"${DateTime.now()}"
                }
            """.trimIndent())
        .asJson()
}