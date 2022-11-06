package ie.setu.repository

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import ie.setu.domain.db.Goals
import ie.setu.domain.Goal
import ie.setu.domain.repository.GoalDAO
import ie.setu.helpers.*
import kotlin.test.assertEquals

//retrieving some test data from Fixtures
private val goal1 = goals[0]
private val goal2 = goals[1]
private val goal3 = goals[2]

class GoalDAOTest {

    companion object {
        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    @Nested
    inner class CreateGoals {

        @Test
        fun `multiple goals added to table can be retrieved successfully`() {
            transaction {
                //Arrange - create and populate tables with three users and three goals
                populateUserTable()
                val goalDAO = populateGoalTable()
                //Act & Assert
                assertEquals(3, goalDAO.getAll().size)
                assertEquals(goal1, goalDAO.findByGoalId(goal1.id))
                assertEquals(goal2, goalDAO.findByGoalId(goal2.id))
                assertEquals(goal3, goalDAO.findByGoalId(goal3.id))
            }
        }
    }

    @Nested
    inner class ReadGoals {

        @Test
        fun `getting all goals from a populated table returns all rows`() {
            transaction {
                //Arrange - create and populate tables with three users and three goals
                populateUserTable()
                val goalDAO = populateGoalTable()
                //Act & Assert
                assertEquals(3, goalDAO.getAll().size)
            }
        }

        @Test
        fun `get goal by user id that has no goals, results in no record returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three goals
                populateUserTable()
                val goalDAO = populateGoalTable()
                //Act & Assert
                assertEquals(1, goalDAO.findByUserId(3).size)
            }
        }

        @Test
        fun `get goal by user id that exists, results in a correct activitie(s) returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three goals
                populateUserTable()
                val goalDAO = populateGoalTable()
                //Act & Assert
                assertEquals(goal1, goalDAO.findByUserId(1).get(0))
                assertEquals(goal2, goalDAO.findByUserId(3).get(0))
                assertEquals(goal3, goalDAO.findByUserId(2).get(0))
            }
        }

        @Test
        fun `get all goals over empty table returns none`() {
            transaction {

                //Arrange - create and setup goalDAO object
                SchemaUtils.create(Goals)
                val goalDAO = GoalDAO()

                //Act & Assert
                assertEquals(0, goalDAO.getAll().size)
            }
        }

        @Test
        fun `get goal by goal id that has no records, results in no record returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three goals
                populateUserTable()
                val goalDAO = populateGoalTable()
                //Act & Assert
                assertEquals(null, goalDAO.findByGoalId(4))
            }
        }

        @Test
        fun `get goal by goal id that exists, results in a correct goal returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three goals
                populateUserTable()
                val goalDAO = populateGoalTable()
                //Act & Assert
                assertEquals(goal1, goalDAO.findByGoalId(1))
                assertEquals(goal3, goalDAO.findByGoalId(3))
            }
        }
    }

    @Nested
    inner class UpdateGoals {

        @Test
        fun `updating existing goal in table results in successful update`() {
            transaction {

                //Arrange - create and populate tables with three users and three goals
                populateUserTable()
                val goalDAO = populateGoalTable()

                //Act & Assert
                val goal3updated = Goal(id = 3, steps = 1200, standing_hours = 4.0,
                    calories = 220, userId = 2, distance = 5, created_at = DateTime.now())
                goalDAO.updateByGoalId(goal3updated.id, goal3updated)
                assertEquals(goal3updated, goalDAO.findByGoalId(3))
            }
        }

        @Test
        fun `updating non-existant goal in table results in no updates`() {
            transaction {

                //Arrange - create and populate tables with three users and three goals
                populateUserTable()
                val goalDAO = populateGoalTable()

                //Act & Assert
                val goal4updated = Goal(id = 3, steps = 1200, standing_hours = 4.0,
                    calories = 220, userId = 2, distance = 5, created_at = DateTime.now())
                goalDAO.updateByGoalId(4, goal4updated)
                assertEquals(null, goalDAO.findByGoalId(4))
                assertEquals(3, goalDAO.getAll().size)
            }
        }
    }

    @Nested
    inner class DeleteGoals {

        @Test
        fun `deleting a non-existant goal (by id) in table results in no deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three goals
                populateUserTable()
                val goalDAO = populateGoalTable()

                //Act & Assert
                assertEquals(3, goalDAO.getAll().size)
                goalDAO.deleteByGoalId(4)
                assertEquals(3, goalDAO.getAll().size)
            }
        }

        @Test
        fun `deleting an existing goal (by id) in table results in record being deleted`() {
            transaction {
                //Arrange - create and populate tables with three users and three goals
                populateUserTable()
                val goalDAO = populateGoalTable()

                //Act & Assert
                assertEquals(3, goalDAO.getAll().size)
                goalDAO.deleteByGoalId(goal3.id)
                assertEquals(2, goalDAO.getAll().size)
            }
        }


        @Test
        fun `deleting goals when none exist for user id results in no deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three goals
                populateUserTable()
                val goalDAO = populateGoalTable()

                //Act & Assert
                assertEquals(3, goalDAO.getAll().size)
                goalDAO.deleteByUserId(3)
                assertEquals(2, goalDAO.getAll().size)
            }
        }

        @Test
        fun `deleting goals when 1 or more exist for user id results in deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three goals
                populateUserTable()
                val goalDAO = populateGoalTable()

                //Act & Assert
                assertEquals(3, goalDAO.getAll().size)
                goalDAO.deleteByUserId(1)
                assertEquals(2, goalDAO.getAll().size)
            }
        }
    }
}