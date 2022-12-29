package ie.setu.controllers

import ie.setu.config.DbConfig
import ie.setu.domain.User
import ie.setu.domain.Goal
import ie.setu.helpers.*
import ie.setu.utils.jsonToObject
import ie.setu.utils.jsonNodeToObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class GoalControllerTest {

    private val db = DbConfig().getDbConnection()

    @Nested
    inner class CreateGoals {

        @Test
        fun `add an goal, returns a 201 response`() {
            val addedUser : User = jsonToObject(addUser(validUserName, validEmail, validPassword, validToken, validRole).body.toString())
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
            val addedUser : User = jsonToObject(addUser(validUserName, validEmail, validPassword, validToken, validRole).body.toString())
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
            assertEquals(200, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all goals by user id when no goals exist returns 204 response`() {
            //Arrange - add a user
            val addedUser : User = jsonToObject(addUser(validUserName, validEmail, validPassword, validToken, validRole).body.toString())

            //Assert and Act - retrieve the goals by user id
            val response = retrieveGoalsByUserId(addedUser.id)
            assertEquals(204, response.status)

            //After - delete the added user and assert a 204 is returned
            assertEquals(200, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all goals by user id when no user exists returns 204 response`() {
            //Arrange
            val userId = -1

            //Assert and Act - retrieve goals by user id
            val response = retrieveGoalsByUserId(userId)
            assertEquals(204, response.status)
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
            val addedUser : User = jsonToObject(addUser(validUserName, validEmail, validPassword, validToken, validRole).body.toString())
            val addGoalResponse = addGoal(
                addedUser.id, goals[0].calories, goals[0].standing_hours,
                goals[0].steps, goals[0].distance)
            assertEquals(201, addGoalResponse.status)
            val addedGoal = jsonNodeToObject<Goal>(addGoalResponse)

            //Act & Assert - retrieve the goal by goal id
            val response = retrieveGoalById(addedGoal.id)
            assertEquals(200, response.status)

            //After - delete the added user and assert a 204 is returned
            assertEquals(200, deleteUser(addedUser.id).status)
        }

    }

    @Nested
    inner class UpdateGoals {

        @Test
        fun `updating an goal by goal id when it doesn't exist, returns a 204 response`() {
            val userId = -1
            val goalID = -1

            //Arrange - check there is no user for -1 id
            assertEquals(204, retrieveUserById(userId).status)


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