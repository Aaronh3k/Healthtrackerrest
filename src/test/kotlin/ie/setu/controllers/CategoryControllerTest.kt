package ie.setu.controllers

import ie.setu.config.DbConfig
import ie.setu.domain.Category
import ie.setu.helpers.*
import ie.setu.utils.jsonNodeToObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CategoryControllerTest {

    private val db = DbConfig().getDbConnection()

    @Nested
    inner class CreateCategories {

        @Test
        fun `add an category, returns a 201 response`() {

            val addCategoryResponse = categories[0].description?.let {
                addCategory(
                    categories[0].name, it
                )
            }
            if (addCategoryResponse != null) {
                assertEquals(201, addCategoryResponse.status)
            }

            //delete added category
            if (addCategoryResponse != null) {
                deleteCategoryByCategoryId(addCategoryResponse.body.`object`.get("id") as Int)
            }
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
            assertEquals(204, response.status)
        }


        @Test
        fun `get category by category id when category exists returns 200 response`() {
            val addCategoryResponse = categories[0].description?.let {
                addCategory(
                    categories[0].name, it
                )
            }
            if (addCategoryResponse != null) {
                assertEquals(201, addCategoryResponse.status)
            }
            val addedCategory = addCategoryResponse?.let { jsonNodeToObject<Category>(it) }

            //Act & Assert - retrieve the category by category id
            val response = addedCategory?.let { retrieveCategoryById(it.id) }
            if (response != null) {
                assertEquals(200, response.status)
            }

            //After - delete the added category a 204 is returned
            if (response != null) {
                assertEquals(204, deleteCategoryByCategoryId(response.body.`object`.get("id") as Int).status)
            }
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
            val addCategoryResponse = categories[0].description?.let {
                addCategory(
                    categories[0].name, it
                )
            }
            if (addCategoryResponse != null) {
                assertEquals(201, addCategoryResponse.status)
            }
            val addedCategory = addCategoryResponse?.let { jsonNodeToObject<Category>(it) }

            //Act & Assert - update the added category and assert a 204 is returned
            val updatedCategoryResponse = addedCategory?.let { updateCategory(it.id, category_name, updatedDescription) }
            if (updatedCategoryResponse != null) {
                assertEquals(204, updatedCategoryResponse.status)
            }

            //Assert that the individual fields were all updated as expected
            val retrievedCategoryResponse = addedCategory?.let { retrieveCategoryById(it.id) }
            val updatedCategory = retrievedCategoryResponse?.let { jsonNodeToObject<Category>(it) }
            if (updatedCategory != null) {
                assertEquals(updatedDescription,updatedCategory.description)
            }
            if (updatedCategory != null) {
                assertEquals(category_name, updatedCategory.name)
            }

            //delete created category
            if (addedCategory != null) {
                deleteCategoryByCategoryId(addedCategory.id)
            }
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
            val addCategoryResponse = categories[0].description?.let {
                addCategory(
                    categories[0].name, it
                )
            }
            if (addCategoryResponse != null) {
                assertEquals(201, addCategoryResponse.status)
            }

            //Act & Assert - delete the added category and assert a 204 is returned
            val addedCategory = addCategoryResponse?.let { jsonNodeToObject<Category>(it) }
            if (addedCategory != null) {
                assertEquals(204, deleteCategoryByCategoryId(addedCategory.id).status)
            }

            //After - delete the category
            if (addedCategory != null) {
                deleteCategoryByCategoryId(addedCategory.id)
            }
        }
    }
}