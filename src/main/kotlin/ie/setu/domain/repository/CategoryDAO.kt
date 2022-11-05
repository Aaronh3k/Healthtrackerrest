package ie.setu.domain.repository

import ie.setu.domain.Category
import ie.setu.domain.db.Categories
import ie.setu.utils.mapToCategory
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class CategoryDAO {

    //Get all the Categories in the database regardless of category id
    fun getAll(): ArrayList<Category> {
        val categoriesList: ArrayList<Category> = arrayListOf()
        transaction {
            Categories.selectAll().map {
                categoriesList.add(mapToCategory(it)) }
        }
        return categoriesList
    }

    //Find a specific Category by Category id
    fun findByCategoryId(id: Int): Category?{
        return transaction {
            Categories
                .select() { Categories.id eq id}
                .map{mapToCategory(it)}
                .firstOrNull()
        }
    }

    //Save a Category to the database
    fun save(Category: Category): Int {
        return transaction {
            Categories.insert {
                it[name] = Category.name
                it[description] = Category.description
                it[created_at] = Category.created_at
            }
        } get Categories.id
    }

    fun updateByCategoryId(categoryId: Int, categoryDTO: Category): Int{
        return try {
            transaction {
                Categories.update ({
                    Categories.id eq categoryId}) {
                    it[name] = categoryDTO.name
                    it[description] = categoryDTO.description
                    it[created_at] = categoryDTO.created_at
                }
            }
        }catch (e: Exception){
            0
        }
    }
    fun deleteByCategoryId (CategoryId: Int): Int{
        return transaction{
            Categories.deleteWhere { Categories.id eq CategoryId }
        }
    }
}