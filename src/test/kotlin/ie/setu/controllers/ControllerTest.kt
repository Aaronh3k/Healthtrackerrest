package ie.setu.controllers

import ie.setu.config.DbConfig
import ie.setu.domain.User
import ie.setu.domain.Activity
import ie.setu.domain.Category
import ie.setu.domain.Goal
import ie.setu.helpers.*
import ie.setu.utils.jsonToObject
import ie.setu.utils.jsonNodeToObject
import kong.unirest.Unirest
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ControllerTest {

    private val db = DbConfig().getDbConnection()
/*    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()*/

    @Nested
    inner class ReadUsers {
        @Test
        fun `get all users from the database returns 200 or 404 response`() {
            val response = Unirest.get("$origin/api/users/").asString()
            if (response.status == 200) {
                val retrievedUsers: ArrayList<User> = jsonToObject(response.body.toString())
                assertNotEquals(0, retrievedUsers.size)
            }
            else {
                assertEquals(404, response.status)
            }
        }

        @Test
        fun `get user by id when user does not exist returns 404 response`() {

            //Arrange - test data for user id
            val id = Integer.MIN_VALUE

            // Act - attempt to retrieve the non-existent user from the database
            val retrieveResponse = Unirest.get(origin + "/api/users/${id}").asString()

            // Assert -  verify return code
            assertEquals(404, retrieveResponse.status)
        }

        @Test
        fun `get user by email when user does not exist returns 404 response`() {
            // Arrange & Act - attempt to retrieve the non-existent user from the database
            val retrieveResponse = Unirest.get(origin + "/api/users/email/${nonExistingEmail}").asString()
            // Assert -  verify return code
            assertEquals(404, retrieveResponse.status)
        }

        @Test
        fun `getting a user by id when id exists, returns a 200 response`() {

            //Arrange - add the user
            val addResponse = addUser(validUserName, validEmail)
            val addedUser : User = jsonToObject(addResponse.body.toString())

            //Assert - retrieve the added user from the database and verify return code
            val retrieveResponse = retrieveUserById(addedUser.id)
            assertEquals(200, retrieveResponse.status)

            //After - restore the db to previous state by deleting the added user
            deleteUser(addedUser.id)
        }

        @Test
        fun `getting a user by email when email exists, returns a 200 response`() {

            //Arrange - add the user
            addUser(validUserName, validEmail)

            //Assert - retrieve the added user from the database and verify return code
            val retrieveResponse = retrieveUserByEmail(validEmail)
            assertEquals(200, retrieveResponse.status)

            //After - restore the db to previous state by deleting the added user
            val retrievedUser : User = jsonToObject(retrieveResponse.body.toString())
            deleteUser(retrievedUser.id)
        }
    }

    @Nested
    inner class CreateUsers {
        @Test
        fun `add a user with correct details returns a 201 response`() {

            //Arrange & Act & Assert
            //    add the user and verify return code (using fixture data)
            val addResponse = addUser(validUserName, validEmail)
            assertEquals(201, addResponse.status)

            //Assert - retrieve the added user from the database and verify return code
            val retrieveResponse= retrieveUserByEmail(validEmail)
            assertEquals(200, retrieveResponse.status)

            //Assert - verify the contents of the retrieved user
            val retrievedUser : User = jsonToObject(addResponse.body.toString())
            assertEquals(validEmail, retrievedUser.email)
            assertEquals(validUserName, retrievedUser.user_name)

            //After - restore the db to previous state by deleting the added user
            val deleteResponse = deleteUser(retrievedUser.id)
            assertEquals(204, deleteResponse.status)
        }
    }

    @Nested
    inner class UpdateUsers {
        @Test
        fun `updating a user when it exists, returns a 204 response`() {

            //Arrange - add the user that we plan to do an update on
            val addedResponse = addUser(validUserName, validEmail)
            val addedUser : User = jsonToObject(addedResponse.body.toString())

            //Act & Assert - update the email and name of the retrieved user and assert 204 is returned
            assertEquals(204, updateUser(addedUser.id, updatedUserName, updatedEmail).status)

            //Act & Assert - retrieve updated user and assert details are correct
            val updatedUserResponse = retrieveUserById(addedUser.id)
            val updatedUser : User = jsonToObject(updatedUserResponse.body.toString())
            assertEquals(updatedUserName, updatedUser.user_name)
            assertEquals(updatedEmail, updatedUser.email)

            //After - restore the db to previous state by deleting the added user
            deleteUser(addedUser.id)
        }

        @Test
        fun `updating a user when it doesn't exist, returns a 404 response`() {

            //Act & Assert - attempt to update the email and name of user that doesn't exist
            assertEquals(404, updateUser(-1, updatedUserName, updatedEmail).status)
        }
    }

    @Nested
    inner class DeleteUsers {
        @Test
        fun `deleting a user when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a user that doesn't exist
            assertEquals(404, deleteUser(-1).status)
        }

        @Test
        fun `deleting a user when it exists, returns a 204 response`() {

            //Arrange - add the user that we plan to do delete on
            val addedResponse = addUser(validUserName, validEmail)
            val addedUser : User = jsonToObject(addedResponse.body.toString())

            //Act & Assert - delete the added user and assert a 204 is returned
            assertEquals(204, deleteUser(addedUser.id).status)

            //Act & Assert - attempt to retrieve the deleted user --> 404 response
            assertEquals(404, retrieveUserById(addedUser.id).status)
        }
    }

    @Nested
    inner class CreateActivities {

        @Test
        fun `add an activity when a user exists for it, returns a 201 response`() {

            //Arrange - add a user, category and an associated activity that we plan to do delete on
            val addedUser: User = jsonToObject(addUser(validUserName, validEmail).body.toString())
            val addedCategory : Category = jsonToObject(addCategory(category_name, category_description).body.toString())


            val addActivityResponse = addActivity(
                activities[0].description, activities[0].duration,
                activities[0].calories, activities[0].started, addedUser.id, addedCategory.id,
                activities[0].distance, activities[0].created_at
            )
            assertEquals(201, addActivityResponse.status)

            //After - delete the user (Activity will cascade delete in the database)
            deleteUser(addedUser.id)
            deleteCategoryByCategoryId(addedCategory.id)
        }

        @Test
        fun `add an activity when no user exists for it, returns a 404 response`() {

            //Arrange - check there is no user for -1 id
            val userId = -1
            assertEquals(404, retrieveUserById(userId).status)

            val addActivityResponse = addActivity(
                activities[0].description, activities[0].duration,
                activities[0].calories, activities[0].started, userId, activities[0].categoryId,
                activities[0].distance, activities[0].created_at
            )
            assertEquals(404, addActivityResponse.status)
        }
    }

    @Nested
    inner class ReadActivities {

        @Test
        fun `get all activities from the database returns 200 or 404 response`() {
            val response = retrieveAllActivities()
            if (response.status == 200){
                val retrievedActivities = jsonNodeToObject<Array<Activity>>(response)
                assertNotEquals(0, retrievedActivities.size)
            }
            else{
                assertEquals(404, response.status)
            }
        }

        @Test
        fun `get all activities by user id when user and activities exists returns 200 response`() {
            //Arrange - add a user and 3 associated activities that we plan to retrieve
            val addedUser : User = jsonToObject(addUser(validUserName, validEmail).body.toString())
            val addedCategory : Category = jsonToObject(addCategory(category_name, category_description).body.toString())
            addActivity(
                activities[0].description, activities[0].duration,
                activities[0].calories, activities[0].started, addedUser.id, addedCategory.id,
                activities[0].distance, activities[0].created_at)
            addActivity(
                activities[1].description, activities[1].duration,
                activities[1].calories, activities[1].started, addedUser.id, addedCategory.id,
                activities[0].distance, activities[0].created_at)
            addActivity(
                activities[2].description, activities[2].duration,
                activities[2].calories, activities[2].started, addedUser.id, addedCategory.id,
                activities[0].distance, activities[0].created_at)

            //Assert and Act - retrieve the three added activities by user id
            val response = retrieveActivitiesByUserId(addedUser.id)
            assertEquals(200, response.status)
            val retrievedActivities = jsonNodeToObject<Array<Activity>>(response)
            assertEquals(3, retrievedActivities.size)

            //After - delete the added user and assert a 204 is returned (activities are cascade deleted)
            assertEquals(204, deleteUser(addedUser.id).status)
            assertEquals(204, deleteCategoryByCategoryId(addedCategory.id).status)
        }

        @Test
        fun `get all activities by user id when no activities exist returns 404 response`() {
            //Arrange - add a user
            val addedUser : User = jsonToObject(addUser(validUserName, validEmail).body.toString())

            //Assert and Act - retrieve the activities by user id
            val response = retrieveActivitiesByUserId(addedUser.id)
            assertEquals(404, response.status)

            //After - delete the added user and assert a 204 is returned
            assertEquals(204, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all activities by user id when no user exists returns 404 response`() {
            //Arrange
            val userId = -1

            //Assert and Act - retrieve activities by user id
            val response = retrieveActivitiesByUserId(userId)
            assertEquals(404, response.status)
        }

        @Test
        fun `get activity by activity id when no activity exists returns 404 response`() {
            //Arrange
            val activityId = -1
            //Assert and Act - attempt to retrieve the activity by activity id
            val response = retrieveActivityByActivityId(activityId)
            assertEquals(404, response.status)
        }


        @Test
        fun `get activity by activity id when activity exists returns 200 response`() {
            //Arrange - add a user and associated activity
            val addedUser : User = jsonToObject(addUser(validUserName, validEmail).body.toString())
            val addedCategory : Category = jsonToObject(addCategory(category_name, category_description).body.toString())
            val addActivityResponse = addActivity(
                activities[0].description,
                activities[0].duration, activities[0].calories,
                activities[0].started, addedUser.id, addedCategory.id,
                activities[0].distance, activities[0].created_at)
            assertEquals(201, addActivityResponse.status)
            val addedActivity = jsonNodeToObject<Activity>(addActivityResponse)

            //Act & Assert - retrieve the activity by activity id
            val response = retrieveActivityByActivityId(addedActivity.id)
            assertEquals(200, response.status)

            //After - delete the added user, category and assert a 204 is returned
            assertEquals(204, deleteUser(addedUser.id).status)
            assertEquals(204, deleteCategoryByCategoryId(addedCategory.id).status)
        }

    }

    @Nested
    inner class UpdateActivities {

        @Test
        fun `updating an activity by activity id when it doesn't exist, returns a 404 response`() {
            val userId = -1
            val activityID = -1
            val categoryId = -1

            //Arrange - check there is no user for -1 id
            assertEquals(404, retrieveUserById(userId).status)

            //Arrange - check there is no category for -1 id
            assertEquals(404, retrieveCategoryById(categoryId).status)

            //Act & Assert - attempt to update the details of an activity/user that doesn't exist
            assertEquals(
                404, updateActivity(
                    activityID, updatedDescription, updatedDuration,
                    updatedCalories, updatedStarted, userId, categoryId, updatedDistance, updateCreatedAt
                ).status
            )
        }

        @Test
        fun `updating an activity by activity id when it exists, returns 204 response`() {

            //Arrange - add a user, category and an associated activity that we plan to do an update on
            val addedUser : User = jsonToObject(addUser(validUserName, validEmail).body.toString())
            val addedCategory : Category = jsonToObject(addCategory(category_name, category_description).body.toString())
            val addActivityResponse = addActivity(
                activities[0].description,
                activities[0].duration, activities[0].calories,
                activities[0].started, addedUser.id, addedCategory.id, activities[0].distance, activities[0].created_at)
            assertEquals(201, addActivityResponse.status)
            val addedActivity = jsonNodeToObject<Activity>(addActivityResponse)

            //Act & Assert - update the added activity and assert a 204 is returned
            val updatedActivityResponse = updateActivity(addedActivity.id, updatedDescription,
                updatedDuration, updatedCalories, updatedStarted, addedUser.id, addedCategory.id, activities[0].distance, updateCreatedAt)
            assertEquals(204, updatedActivityResponse.status)

            //Assert that the individual fields were all updated as expected
            val retrievedActivityResponse = retrieveActivityByActivityId(addedActivity.id)
            val updatedActivity = jsonNodeToObject<Activity>(retrievedActivityResponse)
            assertEquals(updatedDescription,updatedActivity.description)
            assertEquals(updatedDuration, updatedActivity.duration, 0.1)
            assertEquals(updatedCalories, updatedActivity.calories)
            assertEquals(updatedStarted, updatedActivity.started )

            //After - delete the user
            deleteUser(addedUser.id)
            deleteCategoryByCategoryId(addedCategory.id)
        }
    }

    @Nested
    inner class DeleteActivities {

        @Test
        fun `deleting an activity by activity id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a user that doesn't exist
            assertEquals(404, deleteActivityByActivityId(-1).status)
        }

        @Test
        fun `deleting activities by user id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a user that doesn't exist
            assertEquals(404, deleteActivitiesByUserId(-1).status)
        }

        @Test
        fun `deleting an activity by id when it exists, returns a 204 response`() {

            //Arrange - add a user, category and an associated activity that we plan to do delete on
            val addedUser : User = jsonToObject(addUser(validUserName, validEmail).body.toString())
            val addedCategory : Category = jsonToObject(addCategory(category_name, category_description).body.toString())
            val addActivityResponse = addActivity(
                activities[0].description, activities[0].duration,
                activities[0].calories, activities[0].started, addedUser.id, addedCategory.id, activities[0].distance, activities[0].created_at)
            assertEquals(201, addActivityResponse.status)

            //Act & Assert - delete the added activity and assert a 204 is returned
            val addedActivity = jsonNodeToObject<Activity>(addActivityResponse)
            assertEquals(204, deleteActivityByActivityId(addedActivity.id).status)

            //After - delete the user
            deleteUser(addedUser.id)
            deleteCategoryByCategoryId(addedCategory.id)
        }

        @Test
        fun `deleting all activities by userid when it exists, returns a 204 response`() {

            //Arrange - add a user, category and 3 associated activities that we plan to do a cascade delete
            val addedUser : User = jsonToObject(addUser(validUserName, validEmail).body.toString())
            val addedCategory : Category = jsonToObject(addCategory(category_name, category_description).body.toString())
            val addActivityResponse1 = addActivity(
                activities[0].description, activities[0].duration,
                activities[0].calories, activities[0].started, addedUser.id, addedCategory.id, activities[0].distance, activities[0].created_at)
            assertEquals(201, addActivityResponse1.status)
            val addActivityResponse2 = addActivity(
                activities[1].description, activities[1].duration,
                activities[1].calories, activities[1].started, addedUser.id, addedCategory.id, activities[0].distance, activities[0].created_at)
            assertEquals(201, addActivityResponse2.status)
            val addActivityResponse3 = addActivity(
                activities[2].description, activities[2].duration,
                activities[2].calories, activities[2].started, addedUser.id, addedCategory.id, activities[0].distance, activities[0].created_at)
            assertEquals(201, addActivityResponse3.status)

            //Act & Assert - delete the added user, category and assert a 204 is returned
            assertEquals(204, deleteUser(addedUser.id).status)
            assertEquals(204, deleteCategoryByCategoryId(addedCategory.id).status)

            //Act & Assert - attempt to retrieve the deleted activities
            val addedActivity1 = jsonNodeToObject<Activity>(addActivityResponse1)
            val addedActivity2 = jsonNodeToObject<Activity>(addActivityResponse2)
            val addedActivity3 = jsonNodeToObject<Activity>(addActivityResponse3)
            assertEquals(404, retrieveActivityByActivityId(addedActivity1.id).status)
            assertEquals(404, retrieveActivityByActivityId(addedActivity2.id).status)
            assertEquals(404, retrieveActivityByActivityId(addedActivity3.id).status)
        }
    }

    @Nested
    inner class CreateCategories {

        @Test
        fun `add an category, returns a 201 response`() {

            val addCategoryResponse = addCategory(
                categories[0].name, categories[0].description
            )
            assertEquals(201, addCategoryResponse.status)

            //delete added category
            deleteCategoryByCategoryId(addCategoryResponse.body.`object`.get("id") as Int)
        }
    }
    @Nested
    inner class ReadCategories {

        @Test
        fun `get all categories from the database returns 200 or 404 response`() {
            val response = retrieveAllCategories()
            if (response.status == 200){
                val retrievedCategories = jsonNodeToObject<Array<Category>>(response)
                assertNotEquals(0, retrievedCategories.size)
            }
            else{
                assertEquals(404, response.status)
            }
        }

        @Test
        fun `get category by category id when no category exists returns 404 response`() {
            //Arrange
            val categoryId = -1
            //Assert and Act - attempt to retrieve the category by category id
            val response = retrieveCategoryById(categoryId)
            assertEquals(404, response.status)
        }


        @Test
        fun `get category by category id when category exists returns 200 response`() {
            val addCategoryResponse = addCategory(
                categories[0].name, categories[0].description)
            assertEquals(201, addCategoryResponse.status)
            val addedCategory = jsonNodeToObject<Category>(addCategoryResponse)

            //Act & Assert - retrieve the category by category id
            val response = retrieveCategoryById(addedCategory.id)
            assertEquals(200, response.status)

            //After - delete the added category a 204 is returned
            assertEquals(204, deleteCategoryByCategoryId(response.body.`object`.get("id") as Int).status)
        }

    }

    @Nested
    inner class UpdateCategories {

        @Test
        fun `updating an category by category id when it doesn't exist, returns a 404 response`() {
            val categoryId = -1


            //Act & Assert - attempt to update the details of a category that doesn't exist
            assertEquals(
                404, updateCategory(
                    categoryId, category_name, category_description
                ).status
            )
        }

        @Test
        fun `updating an category by category id when it exists, returns 204 response`() {

            //Arrange - add a category that we plan to do an update on
            val addCategoryResponse = addCategory(
                categories[0].name, categories[0].description)
            assertEquals(201, addCategoryResponse.status)
            val addedCategory = jsonNodeToObject<Category>(addCategoryResponse)

            //Act & Assert - update the added category and assert a 204 is returned
            val updatedCategoryResponse = updateCategory(addedCategory.id, category_name, updatedDescription)
            assertEquals(204, updatedCategoryResponse.status)

            //Assert that the individual fields were all updated as expected
            val retrievedCategoryResponse = retrieveCategoryById(addedCategory.id)
            val updatedCategory = jsonNodeToObject<Category>(retrievedCategoryResponse)
            assertEquals(updatedDescription,updatedCategory.description)
            assertEquals(category_name, updatedCategory.name)

            //delete created category
            deleteCategoryByCategoryId(addedCategory.id)
        }
    }

    @Nested
    inner class DeleteCategories {

        @Test
        fun `deleting an category by category id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a category that doesn't exist
            assertEquals(404, deleteCategoryByCategoryId(-1).status)
        }

        @Test
        fun `deleting an category by id when it exists, returns a 204 response`() {

            //Arrange - add a category that we plan to do delete on
            val addCategoryResponse = addCategory(
                categories[0].name, categories[0].description)
            assertEquals(201, addCategoryResponse.status)

            //Act & Assert - delete the added category and assert a 204 is returned
            val addedCategory = jsonNodeToObject<Category>(addCategoryResponse)
            assertEquals(204, deleteCategoryByCategoryId(addedCategory.id).status)

            //After - delete the category
            deleteCategoryByCategoryId(addedCategory.id)
        }

    }

    @Nested
    inner class CreateGoals {

        @Test
        fun `add an goal, returns a 201 response`() {
            val addedUser : User = jsonToObject(addUser(validUserName, validEmail).body.toString())
            val addGoalResponse = addGoal(
                addedUser.id, goals[0].calories, goals[0].standing_hours, goals[0].steps, goals[0].distance
            )
            assertEquals(201, addGoalResponse.status)

            //delete added Goal and user
            deleteUser(addedUser.id)
        }
    }
    @Nested
    inner class ReadGoals {

        @Test
        fun `get all goals from the database returns 200 or 404 response`() {
            val response = retrieveAllGoals()
            if (response.status == 200){
                val retrievedGoals = jsonNodeToObject<Array<Goal>>(response)
                assertNotEquals(0, retrievedGoals.size)
            }
            else{
                assertEquals(404, response.status)
            }
        }

        @Test
        fun `get all goals by user id when user and goals exists returns 200 response`() {
            //Arrange - add a user and 3 associated goals that we plan to retrieve
            val addedUser : User = jsonToObject(addUser(validUserName, validEmail).body.toString())
            addGoal(
                addedUser.id, goals[0].calories, goals[0].standing_hours,
                goals[0].steps, goals[0].distance)
            addGoal(
                addedUser.id, goals[0].calories, goals[0].standing_hours,
                goals[0].steps, goals[0].distance)
            addGoal(
                addedUser.id, goals[0].calories, goals[0].standing_hours,
                goals[0].steps, goals[0].distance)

            //Assert and Act - retrieve the three added goals by user id
            val response = retrieveGoalsByUserId(addedUser.id)
            assertEquals(200, response.status)
            val retrievedGoals = jsonNodeToObject<Array<Goal>>(response)
            assertEquals(3, retrievedGoals.size)

            //After - delete the added user and assert a 204 is returned (activities are cascade deleted)
            assertEquals(204, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all goals by user id when no goals exist returns 404 response`() {
            //Arrange - add a user
            val addedUser : User = jsonToObject(addUser(validUserName, validEmail).body.toString())

            //Assert and Act - retrieve the goals by user id
            val response = retrieveGoalsByUserId(addedUser.id)
            assertEquals(404, response.status)

            //After - delete the added user and assert a 204 is returned
            assertEquals(204, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all goals by user id when no user exists returns 404 response`() {
            //Arrange
            val userId = -1

            //Assert and Act - retrieve goals by user id
            val response = retrieveGoalsByUserId(userId)
            assertEquals(404, response.status)
        }

        @Test
        fun `get goal by goal id when no goal exists returns 404 response`() {
            //Arrange
            val goalId = -1
            //Assert and Act - attempt to retrieve the goal by goal id
            val response = retrieveGoalById(goalId)
            assertEquals(404, response.status)
        }


        @Test
        fun `get goal by goal id when goal exists returns 200 response`() {
            //Arrange - add a user and associated activity
            val addedUser : User = jsonToObject(addUser(validUserName, validEmail).body.toString())
            val addGoalResponse = addGoal(
                addedUser.id, goals[0].calories, goals[0].standing_hours,
                goals[0].steps, goals[0].distance)
            assertEquals(201, addGoalResponse.status)
            val addedGoal = jsonNodeToObject<Goal>(addGoalResponse)

            //Act & Assert - retrieve the goal by goal id
            val response = retrieveGoalById(addedGoal.id)
            assertEquals(200, response.status)

            //After - delete the added user and assert a 204 is returned
            assertEquals(204, deleteUser(addedUser.id).status)
        }

    }

    @Nested
    inner class UpdateGoals {

        @Test
        fun `updating an goal by goal id when it doesn't exist, returns a 404 response`() {
            val userId = -1
            val goalID = -1

            //Arrange - check there is no user for -1 id
            assertEquals(404, retrieveUserById(userId).status)


            //Act & Assert - attempt to update the details of an goal/user that doesn't exist
            assertEquals(
                404, updateGoal(
                    goalID, userId, goal_calories, goal_standing_hours, goal_steps, goal_distance
                ).status
            )
        }

    }

    @Nested
    inner class DeleteGoals {

        @Test
        fun `deleting an goal by goal id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a goal that doesn't exist
            assertEquals(404, deleteGoalByGoaId(-1).status)
        }

        @Test
        fun `deleting goals by user id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a goal that doesn't exist
            assertEquals(404, deleteGoalsByUserId(-1).status)
        }
    }
}
