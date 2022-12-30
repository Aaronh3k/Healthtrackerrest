package ie.setu.controllers

import ie.setu.config.DbConfig
import ie.setu.domain.User
import ie.setu.domain.Activity
import ie.setu.domain.Category
import ie.setu.helpers.*
import ie.setu.utils.jsonToObject
import ie.setu.utils.jsonNodeToObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ActivityControllerTest {

    private val db = DbConfig().getDbConnection()

    @Nested
    inner class CreateActivities {

        @Test
        fun `add an activity when a user exists for it, returns a 201 response`() {

            //Arrange - add a user, category and an associated activity that we plan to do delete on
            val addedUser: User = jsonToObject(addUser(validUserName, validEmail, validPassword, validToken, validRole).body.toString())
            val addedCategory : Category = jsonToObject(addCategory(category_name, category_description).body.toString())


            val addActivityResponse = activities[0].created_at?.let {
                addActivity(
                    activities[0].description, activities[0].duration,
                    activities[0].calories, activities[0].started, addedUser.id, addedCategory.id,
                    activities[0].distance, it
                )
            }
            if (addActivityResponse != null) {
                assertEquals(201, addActivityResponse.status)
            }

            //After - delete the user (Activity will cascade delete in the database)
            deleteUser(addedUser.id)
            deleteCategoryByCategoryId(addedCategory.id)
        }

        @Test
        fun `add an activity when no user exists for it, returns a 404 response`() {

            //Arrange - check there is no user for -1 id
            val userId = -1
            assertEquals(204, retrieveUserById(userId).status)

            val addActivityResponse = activities[0].created_at?.let {
                addActivity(
                    activities[0].description, activities[0].duration,
                    activities[0].calories, activities[0].started, userId, activities[0].categoryId,
                    activities[0].distance, it
                )
            }
            if (addActivityResponse != null) {
                assertEquals(204, addActivityResponse.status)
            }
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
            val addedUser : User = jsonToObject(addUser(validUserName, validEmail, validPassword, validToken, validRole).body.toString())
            val addedCategory : Category = jsonToObject(addCategory(category_name, category_description).body.toString())
            activities[0].created_at?.let {
                addActivity(
                    activities[0].description, activities[0].duration,
                    activities[0].calories, activities[0].started, addedUser.id, addedCategory.id,
                    activities[0].distance, it
                )
            }
            activities[0].created_at?.let {
                addActivity(
                    activities[1].description, activities[1].duration,
                    activities[1].calories, activities[1].started, addedUser.id, addedCategory.id,
                    activities[0].distance, it
                )
            }
            activities[0].created_at?.let {
                addActivity(
                    activities[2].description, activities[2].duration,
                    activities[2].calories, activities[2].started, addedUser.id, addedCategory.id,
                    activities[0].distance, it
                )
            }

            //Assert and Act - retrieve the three added activities by user id
            val response = retrieveActivitiesByUserId(addedUser.id)
            assertEquals(200, response.status)
            val retrievedActivities = jsonNodeToObject<Array<Activity>>(response)
            assertEquals(3, retrievedActivities.size)

            //After - delete the added user and assert a 204 is returned (activities are cascade deleted)
            assertEquals(200, deleteUser(addedUser.id).status)
            assertEquals(204, deleteCategoryByCategoryId(addedCategory.id).status)
        }

        @Test
        fun `get all activities by user id when no activities exist returns 404 response`() {
            //Arrange - add a user
            val addedUser : User = jsonToObject(addUser(validUserName, validEmail, validPassword, validToken, validRole).body.toString())

            //Assert and Act - retrieve the activities by user id
            val response = retrieveActivitiesByUserId(addedUser.id)
            assertEquals(404, response.status)

            //After - delete the added user and assert a 204 is returned
            assertEquals(200, deleteUser(addedUser.id).status)
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
            val addedUser : User = jsonToObject(addUser(validUserName, validEmail, validPassword, validToken, validRole).body.toString())
            val addedCategory : Category = jsonToObject(addCategory(category_name, category_description).body.toString())
            val addActivityResponse = activities[0].created_at?.let {
                addActivity(
                    activities[0].description,
                    activities[0].duration, activities[0].calories,
                    activities[0].started, addedUser.id, addedCategory.id,
                    activities[0].distance, it
                )
            }
            if (addActivityResponse != null) {
                assertEquals(201, addActivityResponse.status)
            }
            val addedActivity = addActivityResponse?.let { jsonNodeToObject<Activity>(it) }

            //Act & Assert - retrieve the activity by activity id
            val response = addedActivity?.let { retrieveActivityByActivityId(it.id) }
            if (response != null) {
                assertEquals(200, response.status)
            }

            //After - delete the added user, category and assert a 204 is returned
            assertEquals(200, deleteUser(addedUser.id).status)
            assertEquals(204, deleteCategoryByCategoryId(addedCategory.id).status)
        }

    }

    @Nested
    inner class UpdateActivities {

        @Test
        fun `updating an activity by activity id when it doesn't exist, returns a 204 response`() {
            val userId = -1
            val activityID = -1
            val categoryId = -1

            //Arrange - check there is no user for -1 id
            assertEquals(204, retrieveUserById(userId).status)

            //Arrange - check there is no category for -1 id
            assertEquals(204, retrieveCategoryById(categoryId).status)

            //Act & Assert - attempt to update the details of an activity/user that doesn't exist
            assertEquals(
                204, updateActivity(
                    activityID, updatedDescription, updatedDuration,
                    updatedCalories, updatedStarted, userId, categoryId, updatedDistance, updateCreatedAt
                ).status
            )
        }

        @Test
        fun `updating an activity by activity id when it exists, returns 200 response`() {

            //Arrange - add a user, category and an associated activity that we plan to do an update on
            val addedUser : User = jsonToObject(addUser(validUserName, validEmail, validPassword, validToken, validRole).body.toString())
            val addedCategory : Category = jsonToObject(addCategory(category_name, category_description).body.toString())
            val addActivityResponse = activities[0].created_at?.let {
                addActivity(
                    activities[0].description,
                    activities[0].duration, activities[0].calories,
                    activities[0].started, addedUser.id, addedCategory.id, activities[0].distance, it
                )
            }
            if (addActivityResponse != null) {
                assertEquals(201, addActivityResponse.status)
            }
            val addedActivity = addActivityResponse?.let { jsonNodeToObject<Activity>(it) }

            //Act & Assert - update the added activity and assert a 204 is returned
            val updatedActivityResponse = addedActivity?.let {
                updateActivity(
                    it.id, updatedDescription,
                    updatedDuration, updatedCalories, updatedStarted, addedUser.id, addedCategory.id, activities[0].distance, updateCreatedAt)
            }
            if (updatedActivityResponse != null) {
                assertEquals(200, updatedActivityResponse.status)
            }

            //Assert that the individual fields were all updated as expected
            val retrievedActivityResponse = addedActivity?.let { retrieveActivityByActivityId(it.id) }
            val updatedActivity = retrievedActivityResponse?.let { jsonNodeToObject<Activity>(it) }
            if (updatedActivity != null) {
                assertEquals(updatedDescription,updatedActivity.description)
            }
            if (updatedActivity != null) {
                assertEquals(updatedDuration, updatedActivity.duration, 0.1)
            }
            if (updatedActivity != null) {
                assertEquals(updatedCalories, updatedActivity.calories)
            }
            if (updatedActivity != null) {
                assertEquals(updatedStarted, updatedActivity.started )
            }

            //After - delete the user
            deleteUser(addedUser.id)
            deleteCategoryByCategoryId(addedCategory.id)
        }
    }

    @Nested
    inner class DeleteActivities {

        @Test
        fun `deleting an activity by activity id when it doesn't exist, returns a 204 response`() {
            //Act & Assert - attempt to delete a user that doesn't exist
            assertEquals(204, deleteActivityByActivityId(-1).status)
        }

        @Test
        fun `deleting activities by user id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a user that doesn't exist
            assertEquals(404, deleteActivitiesByUserId(-1).status)
        }

        @Test
        fun `deleting an activity by id when it exists, returns a 200 response`() {

            //Arrange - add a user, category and an associated activity that we plan to do delete on
            val addedUser: User =
                jsonToObject(addUser(validUserName, validEmail, validPassword, validToken, validRole).body.toString())
            val addedCategory: Category = jsonToObject(addCategory(category_name, category_description).body.toString())
            val addActivityResponse = activities[0].created_at?.let {
                addActivity(
                    activities[0].description,
                    activities[0].duration,
                    activities[0].calories,
                    activities[0].started,
                    addedUser.id,
                    addedCategory.id,
                    activities[0].distance,
                    it
                )
            }
            if (addActivityResponse != null) {
                assertEquals(201, addActivityResponse.status)
            }

            //Act & Assert - delete the added activity and assert a 204 is returned
            val addedActivity = addActivityResponse?.let { jsonNodeToObject<Activity>(it) }
            if (addedActivity != null) {
                assertEquals(200, deleteActivityByActivityId(addedActivity.id).status)
            }

            //After - delete the user
            deleteUser(addedUser.id)
            deleteCategoryByCategoryId(addedCategory.id)
        }

        @Test
        fun `deleting all activities by userid when it exists, returns a 200 response`() {

            //Arrange - add a user, category and 3 associated activities that we plan to do a cascade delete
            val addedUser: User =
                jsonToObject(addUser(validUserName, validEmail, validPassword, validToken, validRole).body.toString())
            val addedCategory: Category = jsonToObject(addCategory(category_name, category_description).body.toString())
            val addActivityResponse1 = activities[0].created_at?.let {
                addActivity(
                    activities[0].description,
                    activities[0].duration,
                    activities[0].calories,
                    activities[0].started,
                    addedUser.id,
                    addedCategory.id,
                    activities[0].distance,
                    it
                )
            }
            if (addActivityResponse1 != null) {
                assertEquals(201, addActivityResponse1.status)
            }
            val addActivityResponse2 = activities[0].created_at?.let {
                addActivity(
                    activities[1].description,
                    activities[1].duration,
                    activities[1].calories,
                    activities[1].started,
                    addedUser.id,
                    addedCategory.id,
                    activities[0].distance,
                    it
                )
            }
            if (addActivityResponse2 != null) {
                assertEquals(201, addActivityResponse2.status)
            }
            val addActivityResponse3 = activities[0].created_at?.let {
                addActivity(
                    activities[2].description,
                    activities[2].duration,
                    activities[2].calories,
                    activities[2].started,
                    addedUser.id,
                    addedCategory.id,
                    activities[0].distance,
                    it
                )
            }
            if (addActivityResponse3 != null) {
                assertEquals(201, addActivityResponse3.status)
            }

            //Act & Assert - delete the added user, category and assert a 204 is returned
            assertEquals(200, deleteUser(addedUser.id).status)
            assertEquals(204, deleteCategoryByCategoryId(addedCategory.id).status)

            //Act & Assert - attempt to retrieve the deleted activities
            val addedActivity1 = addActivityResponse1?.let { jsonNodeToObject<Activity>(it) }
            val addedActivity2 = addActivityResponse2?.let { jsonNodeToObject<Activity>(it) }
            val addedActivity3 = addActivityResponse3?.let { jsonNodeToObject<Activity>(it) }
            if (addedActivity1 != null) {
                assertEquals(404, retrieveActivityByActivityId(addedActivity1.id).status)
            }
            if (addedActivity2 != null) {
                assertEquals(404, retrieveActivityByActivityId(addedActivity2.id).status)
            }
            if (addedActivity3 != null) {
                assertEquals(404, retrieveActivityByActivityId(addedActivity3.id).status)
            }
        }
    }
}