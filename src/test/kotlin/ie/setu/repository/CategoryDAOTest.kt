package ie.setu.repository

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import ie.setu.domain.db.Categories
import ie.setu.domain.Category
import ie.setu.domain.repository.CategoryDAO
import ie.setu.helpers.categories
import ie.setu.helpers.populateCategoryTable
import kotlin.test.assertEquals

//retrieving some test data from Fixtures
private val category1 = categories[0]
private val category2 = categories[1]
private val category3 = categories[2]

class CategoryDAOTest {

    companion object {
        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    @Nested
    inner class CreateCategories {

//        @Test
//        fun `multiple categories added to table can be retrieved successfully`() {
//            transaction {
//                //Arrange - create and populate tables three categories
//                val categoryDAO = populateCategoryTable()
//                //Act & Assert
//                assertEquals(3, categoryDAO.getAll().size)
//                assertEquals(category1, categoryDAO.findByCategoryId(category1.id))
//                assertEquals(category2, categoryDAO.findByCategoryId(category2.id))
//                assertEquals(category3, categoryDAO.findByCategoryId(category3.id))
//            }
//        }
    }

    @Nested
    inner class ReadCategories {

        @Test
        fun `getting all categories from a populated table returns all rows`() {
            transaction {
                //Arrange - create and populate tables three categories
                val categoryDAO = populateCategoryTable()
                //Act & Assert
                assertEquals(3, categoryDAO.getAll().size)
            }
        }

        @Test
        fun `get all categories over empty table returns none`() {
            transaction {

                //Arrange - create and setup categoryDAO object
                SchemaUtils.create(Categories)
                val categoryDAO = CategoryDAO()

                //Act & Assert
                assertEquals(0, categoryDAO.getAll().size)
            }
        }

//        @Test
//        fun `get category by category id that has no records, results in no record returned`() {
//            transaction {
//                //Arrange - create and populate tables three categories
//                val categoryDAO = populateCategoryTable()
//                //Act & Assert
//                assertEquals(null, categoryDAO.findByCategoryId(4))
//            }
//        }

//        @Test
//        fun `get category by category id that exists, results in a correct category returned`() {
//            transaction {
//                //Arrange - create and populate tables three categories
//                val categoryDAO = populateCategoryTable()
//                //Act & Assert
//                assertEquals(category1, categoryDAO.findByCategoryId(1))
//                assertEquals(category3, categoryDAO.findByCategoryId(3))
//            }
//        }
    }

    @Nested
    inner class UpdateCategories {

        @Test
        fun `updating existing category in table results in successful update`() {
            transaction {

                //Arrange - create and populate tables three categories
                val categoryDAO = populateCategoryTable()

                //Act & Assert
                val category3updated = Category(id = 3, name= "Health", description = "Cardio", created_at = DateTime.now())
                categoryDAO.updateByCategoryId(category3updated.id, category3updated)
                assertEquals(category3updated, categoryDAO.findByCategoryId(3))
            }
        }

        @Test
        fun `updating non-existant category in table results in no updates`() {
            transaction {

                //Arrange - create and populate tables three categories
                val categoryDAO = populateCategoryTable()

                //Act & Assert
                val category4updated = Category(id = 4, name = "Health", description = "Cardio", created_at = DateTime.now())
                categoryDAO.updateByCategoryId(4, category4updated)
                assertEquals(null, categoryDAO.findByCategoryId(4))
                assertEquals(3, categoryDAO.getAll().size)
            }
        }
    }

    @Nested
    inner class DeleteActivities {

        @Test
        fun `deleting a non-existant category (by id) in table results in no deletion`() {
            transaction {

                //Arrange - create and populate tables three categories
                val categoryDAO = populateCategoryTable()

                //Act & Assert
                assertEquals(3, categoryDAO.getAll().size)
                categoryDAO.deleteByCategoryId(4)
                assertEquals(3, categoryDAO.getAll().size)
            }
        }

        @Test
        fun `deleting an existing category (by id) in table results in record being deleted`() {
            transaction {
                //Arrange - create and populate tables three categories
                val categoryDAO = populateCategoryTable()

                //Act & Assert
                assertEquals(3, categoryDAO.getAll().size)
                categoryDAO.deleteByCategoryId(category3.id)
                assertEquals(2, categoryDAO.getAll().size)
            }
        }
    }
}