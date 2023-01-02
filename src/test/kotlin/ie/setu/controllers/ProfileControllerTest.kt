package ie.setu.controllers

import ie.setu.config.DbConfig
import ie.setu.domain.Profile
import ie.setu.domain.User
import ie.setu.helpers.*
import ie.setu.utils.jsonNodeToObject
import ie.setu.utils.jsonToObject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ProfileControllerTest {

    private val db = DbConfig().getDbConnection()

    @Nested
    inner class CreateProfiles {

        @Test
        fun `add an profile, returns a 201 response`() {
            val addedUser : User = jsonToObject(addUser(validUserName, validEmail, validPassword, validToken, validRole).body.toString())
            val addProfileResponse = addUserProfile(addedUser.id, validFirstname, validlastname, validdob, validGender)
            Assertions.assertEquals(201, addProfileResponse.status)

            //delete added Goal and user
            deleteUser(addedUser.id)
        }
    }
    @Nested
    inner class ReadProfiles {

        @Test
        fun `get all profiles from the database returns 200 or 404 response`() {
            val response = retrieveAllProfiles()
            if (response.status == 200){
                val retrievedProfile = jsonNodeToObject<Array<Profile>>(response)
                Assertions.assertNotEquals(0, retrievedProfile.size)
            }
            else{
                Assertions.assertEquals(404, response.status)
            }
        }

        @Test
        fun `get all profiles by user id when user and profile exists returns 200 response`() {
            //Arrange - add a user and 3 associated goals that we plan to retrieve
            val addedUser : User = jsonToObject(addUser(validUserName, validEmail, validPassword, validToken, validRole).body.toString())
            addUserProfile(
                addedUser.id, userprofile[0].first_name, userprofile[0].last_name,
                userprofile[0].dob, userprofile[0].gender)
            addUserProfile(
                addedUser.id, userprofile[0].first_name, userprofile[0].last_name,
                userprofile[0].dob, userprofile[0].gender)
            addUserProfile(
                addedUser.id, userprofile[0].first_name, userprofile[0].last_name,
                userprofile[0].dob, userprofile[0].gender)

            //Assert and Act - retrieve the three added profile by user id
            val response = retrieveProfileByUserId(addedUser.id)
            Assertions.assertEquals(200, response.status)
            //After - delete the added user and assert a 204 is returned (profile are cascade deleted)
            Assertions.assertEquals(200, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all profile by user id when no profile exist returns 204 response`() {
            //Arrange - add a user
            val addedUser : User = jsonToObject(addUser(validUserName, validEmail, validPassword, validToken, validRole).body.toString())

            //Assert and Act - retrieve the profile by user id
            val response = retrieveProfileByUserId(addedUser.id)
            Assertions.assertEquals(204, response.status)

            //After - delete the added user and assert a 204 is returned
            Assertions.assertEquals(200, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all profile by user id when no user exists returns 204 response`() {
            //Arrange
            val userId = -1

            //Assert and Act - retrieve profile by user id
            val response = retrieveProfileByUserId(userId)
            Assertions.assertEquals(204, response.status)
        }

        @Test
        fun `get profile by profile id when no profile exists returns 404 response`() {
            //Arrange
            val profileId = -1
            //Assert and Act - attempt to retrieve the profile by profile id
            val response = retrieveProfileById(profileId)
            Assertions.assertEquals(404, response.status)
        }


        @Test
        fun `get profile by profile id when profile exists returns 200 response`() {
            //Arrange - add a user and associated activity
            val addedUser : User = jsonToObject(addUser(validUserName, validEmail, validPassword, validToken, validRole).body.toString())
            val addProfileResponse = addUserProfile(
                addedUser.id, userprofile[0].first_name, userprofile[0].last_name,
                userprofile[0].dob, userprofile[0].gender)
            Assertions.assertEquals(201, addProfileResponse.status)
            val addedProfile = jsonNodeToObject<Profile>(addProfileResponse)

            //Act & Assert - retrieve the profile by profile id
            val response = retrieveProfileById(addedProfile.id)
            Assertions.assertEquals(200, response.status)

            //After - delete the added user and assert a 204 is returned
            Assertions.assertEquals(200, deleteUser(addedUser.id).status)
        }

    }

    @Nested
    inner class UpdateProfile {

        @Test
        fun `updating an profile by profile id when it doesn't exist, returns a 204 response`() {
            val userId = -1
            val profileID = -1

            //Arrange - check there is no user for -1 id
            Assertions.assertEquals(204, retrieveUserById(userId).status)


            //Act & Assert - attempt to update the details of an profile/user that doesn't exist
            Assertions.assertEquals(
                400, updateProfile(
                    profileID, userId, validFirstname, validlastname, validdob, validGender
                ).status
            )
        }

    }

    @Nested
    inner class DeleteProfile {

        @Test
        fun `deleting an profile by profile id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a profile that doesn't exist
            Assertions.assertEquals(404, deleteProfileByProfileId(-1).status)
        }

        @Test
        fun `deleting profile by user id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a profile that doesn't exist
            Assertions.assertEquals(404, deleteProfileByUserId(-1).status)
        }
    }
}