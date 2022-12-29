package ie.setu.controllers

import ie.setu.config.DbConfig
import ie.setu.domain.User
import ie.setu.helpers.*
import ie.setu.utils.jsonToObject
import kong.unirest.Unirest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UserControllerTest {

    private val db = DbConfig().getDbConnection()


    @Nested
    inner class ReadUsers {
        @Test
        fun `get all users from the database returns 200 or 404 response`() {
            val response = Unirest.get("$origin/test/users/").asString()
            if (response.status == 200) {
                val retrievedUsers: ArrayList<User> = jsonToObject(response.body.toString())
                assertNotEquals(0, retrievedUsers.size)
            }
            else {
                assertEquals(404, response.status)
            }
        }

        @Test
        fun `get user by id when user does not exist returns 204 response`() {

            //Arrange - test data for user id
            val id = Integer.MIN_VALUE

            // Act - attempt to retrieve the non-existent user from the database
            val retrieveResponse = Unirest.get(origin + "/test/users/${id}").asString()

            // Assert -  verify return code
            assertEquals(204, retrieveResponse.status)
        }

        @Test
        fun `get user by email when user does not exist returns 404 response`() {
            // Arrange & Act - attempt to retrieve the non-existent user from the database
            val retrieveResponse = Unirest.get(origin + "/test/users/email/${nonExistingEmail}").asString()
            // Assert -  verify return code
            assertEquals(404, retrieveResponse.status)
        }

        @Test
        fun `getting a user by id when id exists, returns a 200 response`() {

            //Arrange - add the user
            val addResponse = addUser(validUserName, validEmail, validPassword, validToken, validRole)
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
            addUser(validUserName, validEmail, validPassword, validToken, validRole)

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
            val addResponse = addUser(validUserName, validEmail, validPassword, validToken, validRole)
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
            assertEquals(200, deleteResponse.status)
        }
    }

    @Nested
    inner class UpdateUsers {
        @Test
        fun `updating a user when it exists, returns a 200 response`() {

            //Arrange - add the user that we plan to do an update on
            val addedResponse = addUser(validUserName, validEmail, validPassword, validToken, validRole)
            val addedUser : User = jsonToObject(addedResponse.body.toString())

            //Act & Assert - update the email and name of the retrieved user and assert 204 is returned
            assertEquals(200, updateUser(addedUser.id, updatedUserName, updatedEmail, updatedPassword, updatedToken, updatedRole).status)

            //Act & Assert - retrieve updated user and assert details are correct
            val updatedUserResponse = retrieveUserById(addedUser.id)
            val updatedUser : User = jsonToObject(updatedUserResponse.body.toString())
            assertEquals(updatedUserName, updatedUser.user_name)
            assertEquals(updatedEmail, updatedUser.email)

            //After - restore the db to previous state by deleting the added user
            deleteUser(addedUser.id)
        }

        @Test
        fun `updating a user when it doesn't exist, returns a 400 response`() {

            //Act & Assert - attempt to update the email and name of user that doesn't exist
            assertEquals(400, updateUser(-1, updatedUserName, updatedEmail, updatedPassword, updatedToken, updatedRole).status)
        }
    }

    @Nested
    inner class DeleteUsers {
        @Test
        fun `deleting a user when it doesn't exist, returns a 400 response`() {
            //Act & Assert - attempt to delete a user that doesn't exist
            assertEquals(400, deleteUser(-1).status)
        }

        @Test
        fun `deleting a user when it exists, returns a 204 response`() {

            //Arrange - add the user that we plan to do delete on
            val addedResponse = addUser(validUserName, validEmail, validPassword, validToken, validRole)
            val addedUser : User = jsonToObject(addedResponse.body.toString())

            //Act & Assert - delete the added user and assert a 204 is returned
            assertEquals(200, deleteUser(addedUser.id).status)

            //Act & Assert - attempt to retrieve the deleted user --> 404 response
            assertEquals(204, retrieveUserById(addedUser.id).status)
        }
    }

}