package ie.setu.repository

import ie.setu.controllers.addUserProfile
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import ie.setu.domain.db.UserProfiles
import ie.setu.domain.Profile
import ie.setu.domain.repository.ProfileDAO
import ie.setu.helpers.populateProfileTable
import ie.setu.helpers.userprofile
import ie.setu.helpers.populateUserTable
import junit.framework.TestCase
import kotlin.test.assertEquals

//retrieving some test data from Fixtures
private val profile1 = userprofile[0]
private val profile2 = userprofile[1]
private val profile3 = userprofile[2]

class ProfileDAOTest {

    companion object {
        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    @Nested
    inner class CreateUserProfile {

        @Test
        fun `multiple userprofile added to table can be retrieved successfully`() {
            transaction {
                //Arrange - create and populate tables with three users and three userprofile
                populateUserTable()
                val profileDAO = populateProfileTable()
                //Act & Assert
                assertEquals(3, profileDAO.getAll().size)
                assertEquals(profile1, profileDAO.findByProfileId(profile1.id))
                assertEquals(profile2, profileDAO.findByProfileId(profile2.id))
                assertEquals(profile3, profileDAO.findByProfileId(profile3.id))
            }
        }

        @Test
        fun `userprofile added with existing userId in table results in unsuccessful creation`() {
            transaction {

                //Arrange - create and populate table with three users
                val userDAO = populateProfileTable()
                val `userprofilecreation` = addUserProfile(userId = 1, first_name = "Test", last_name = "Test",
                dob = DateTime.parse("1998-06-11"), gender = 'M')

                //Act & Assert
                TestCase.assertEquals(204, `userprofilecreation`.status)
            }
        }
    }

    @Nested
    inner class ReadUserProfile {

        @Test
        fun `getting all userprofile from a populated table returns all rows`() {
            transaction {
                //Arrange - create and populate tables with three users and three userprofile
                populateUserTable()
                val profileDAO = populateProfileTable()
                //Act & Assert
                assertEquals(3, profileDAO.getAll().size)
            }
        }

        @Test
        fun `get userprofile by user id that has no userprofile, results in no record returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three userprofile
                populateUserTable()
                val profileDAO = populateProfileTable()
                //Act & Assert
                assertEquals(1, profileDAO.findByUserIdList(1).size)
            }
        }

        @Test
        fun `get userprofile by user id that exists, results in a correct profile returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three userprofile
                populateUserTable()
                val profileDAO = populateProfileTable()
                //Act & Assert
                assertEquals(profile1, profileDAO.findByUserIdList(1)[0])
                assertEquals(profile2, profileDAO.findByUserIdList(3)[0])
                assertEquals(profile3, profileDAO.findByUserIdList(2)[0])
            }
        }

        @Test
        fun `get all userprofile over empty table returns none`() {
            transaction {

                //Arrange - create and setup profileDAO object
                SchemaUtils.create(UserProfiles)
                val profileDAO = ProfileDAO()

                //Act & Assert
                assertEquals(0, profileDAO.getAll().size)
            }
        }

        @Test
        fun `get profile by profile id that has no records, results in no record returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three userprofile
                populateUserTable()
                val profileDAO = populateProfileTable()
                //Act & Assert
                assertEquals(null, profileDAO.findByProfileId(4))
            }
        }

        @Test
        fun `get profile by profile id that exists, results in a correct profile returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three userprofile
                populateUserTable()
                val profileDAO = populateProfileTable()
                //Act & Assert
                assertEquals(profile1, profileDAO.findByProfileId(1))
                assertEquals(profile3, profileDAO.findByProfileId(3))
            }
        }
    }

    @Nested
    inner class UpdateUserProfile {

        @Test
        fun `updating existing profile in table results in successful update`() {
            transaction {

                //Arrange - create and populate tables with three users and three userprofile
                populateUserTable()
                val profileDAO = populateProfileTable()

                //Act & Assert
                val profile3updated = Profile(id = 3, first_name = "Test", last_name = "test", gender = 'M',
                    dob = DateTime.parse("1998-06-11"), created_at = DateTime.parse("1998-06-11"), userId = 2)
                profileDAO.updateByProfileId(profile3updated.id, profile3updated)
                assertEquals(profile3updated, profileDAO.findByProfileId(3))
            }
        }

        @Test
        fun `updating existing profile in table with existing userId results in unsuccessful update`() {
            transaction {

                //Arrange - create and populate tables with three users and userprofile
                populateUserTable()
                val profileDAO = populateProfileTable()

                //Act & Assert
                val profile3updated = Profile(id = 3, first_name = "Test", last_name = "test", gender = 'M',
                    dob = DateTime.parse("1998-06-11"), created_at = DateTime.now(), userId = 1)
                assertEquals(0, profileDAO.updateByProfileId(profile3updated.id, profile3updated))
            }
        }

        @Test
        fun `updating existing profile in table results in unsuccessful update when user id is not unique`() {
            transaction {

                //Arrange - create and populate tables with three users and three userprofile
                populateUserTable()
                val profileDAO = populateProfileTable()

                //Act & Assert
                val profile3updated = Profile(id = 3, first_name = "Test", last_name = "test", gender = 'M',
                    dob = DateTime.parse("1998-06-11"), created_at = DateTime.now(), userId = 1)
                assertEquals(0,profileDAO.updateByProfileId(profile3updated.id, profile3updated))
            }
        }

        @Test
        fun `updating existing profile in table results in successful update when user id is unique`() {
            transaction {

                //Arrange - create and populate tables with three users and three userprofile
                populateUserTable()
                val profileDAO = populateProfileTable()

                //Act & Assert
                val profile3updated = Profile(id = 3, first_name = "Test", last_name = "test", gender = 'M',
                    dob = DateTime.parse("1998-06-11"), created_at = DateTime.now(), userId = 2)
                assertEquals(1,profileDAO.updateByProfileId(profile3updated.id, profile3updated))
            }
        }

        @Test
        fun `updating non-existant profile in table results in no updates`() {
            transaction {

                //Arrange - create and populate tables with three users and three userprofile
                populateUserTable()
                val profileDAO = populateProfileTable()

                //Act & Assert
                val profile4updated = Profile(id = 4, first_name = "Test", last_name = "test", gender = 'M',
                    dob = DateTime.parse("1998-06-11"), created_at = DateTime.now(), userId = 1)
                profileDAO.updateByProfileId(4, profile4updated)
                assertEquals(null, profileDAO.findByProfileId(4))
                assertEquals(3, profileDAO.getAll().size)
            }
        }
    }

    @Nested
    inner class DeleteUserProfile {

        @Test
        fun `deleting a non-existant profile (by id) in table results in no deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three userprofile
                populateUserTable()
                val profileDAO = populateProfileTable()

                //Act & Assert
                assertEquals(3, profileDAO.getAll().size)
                profileDAO.deleteByProfileId(4)
                assertEquals(3, profileDAO.getAll().size)
            }
        }

        @Test
        fun `deleting an existing profile (by id) in table results in record being deleted`() {
            transaction {
                //Arrange - create and populate tables with three users and three userprofile
                populateUserTable()
                val profileDAO = populateProfileTable()

                //Act & Assert
                assertEquals(3, profileDAO.getAll().size)
                profileDAO.deleteByProfileId(profile3.id)
                assertEquals(2, profileDAO.getAll().size)
            }
        }


        @Test
        fun `deleting userprofile when none exist for user id results in no deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three userprofile
                populateUserTable()
                val profileDAO = populateProfileTable()

                //Act & Assert
                assertEquals(3, profileDAO.getAll().size)
                profileDAO.deleteByUserId(3)
                assertEquals(2, profileDAO.getAll().size)
            }
        }

        @Test
        fun `deleting userprofile when 1 or more exist for user id results in deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three userprofile
                populateUserTable()
                val profileDAO = populateProfileTable()

                //Act & Assert
                assertEquals(3, profileDAO.getAll().size)
                profileDAO.deleteByUserId(1)
                assertEquals(2, profileDAO.getAll().size)
            }
        }
    }
}