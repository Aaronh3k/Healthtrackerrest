package ie.setu.domain.db

import org.jetbrains.exposed.sql.Table

object Categories : Table("categories") {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", 100)
    val description = varchar("description", 100)
    val created_at = datetime("created_at")
}