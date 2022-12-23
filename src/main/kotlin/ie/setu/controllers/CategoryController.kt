package ie.setu.controllers

import ie.setu.domain.Category
import ie.setu.domain.repository.CategoryDAO
import ie.setu.utils.jsonToObject
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.*
import org.joda.time.DateTime

object CategoryController {

    private val categoryDAO = CategoryDAO()

    @OpenApi(
        summary = "Get all Categories",
        operationId = "getAllCategories",
        tags = ["Category"],
        path = "/api/categories",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Category>::class)])]
    )
    fun getAllCategories(ctx: Context) {
        val categories = CategoryController.categoryDAO.getAll()
        if (categories.size != 0) {
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(categories)
    }

    @OpenApi(
        summary = "Get category by ID",
        operationId = "getCategoriesByCategoryId",
        tags = ["Category"],
        path = "/api/categories/{category-id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("category-id", Int::class, "The category ID")],
        responses  = [OpenApiResponse("200", [OpenApiContent(Category::class)])]
    )
    fun getCategoriesByCategoryId(ctx: Context) {

        val category = CategoryController.categoryDAO.findByCategoryId(ctx.pathParam("category-id").toInt())
        if (category != null) {
            ctx.json(category)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }

    @OpenApi(
        summary = "Add Category",
        operationId = "addCategories",
        tags = ["Category"],
        path = "/api/categories",
        method = HttpMethod.POST,
        pathParams = [OpenApiParam("category-id", Int::class, "The category ID")],
        responses  = [OpenApiResponse("200")]
    )
    fun addCategories(ctx: Context) {
        val category : Category = jsonToObject(ctx.body())
        category.created_at = DateTime.now()
        val categoryId = CategoryController.categoryDAO.save(category)
        if (categoryId != null) {
            category.id = categoryId
            ctx.json(category)
            ctx.status(201)
        }
        else{
            ctx.status(404)
        }
    }

    @OpenApi(
        summary = "Delete category by ID",
        operationId = "deleteCategoryByCategoryId",
        tags = ["Category"],
        path = "/api/categories/{category-id}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("category-id", Int::class, "The category ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun deleteCategoryByCategoryId(ctx: Context){
        if (CategoryController.categoryDAO.deleteByCategoryId(ctx.pathParam("category-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    @OpenApi(
        summary = "Delete category by user ID",
        operationId = "deleteCategoryByUserId",
        tags = ["Category"],
        path = "/api/categories/{category-id}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("category-id", Int::class, "The category ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun deleteCategoryByUserId(ctx: Context){
        if (CategoryController.categoryDAO.deleteByCategoryId(ctx.pathParam("category-id").toInt()) != 0)
            ctx.status(200)
        else
            ctx.status(404)
    }

    @OpenApi(
        summary = "Update category by category ID",
        operationId = "updateCategoryByCategoryId",
        tags = ["Category"],
        path = "/api/categories/{category-id}",
        method = HttpMethod.PATCH,
        pathParams = [OpenApiParam("category-id", Int::class, "The category ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun updateCategoryByCategoryId(ctx: Context){
        val category : Category = jsonToObject(ctx.body())
        val categories = CategoryController.categoryDAO.findByCategoryId(ctx.pathParam("category-id").toInt())
        if (categories != null) {
            category.created_at = categories.created_at
        }
        else{
            ctx.status(404)
        }

        if (CategoryController.categoryDAO.updateByCategoryId(categoryId = ctx.pathParam("category-id").toInt(), categoryDTO =category) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }
}