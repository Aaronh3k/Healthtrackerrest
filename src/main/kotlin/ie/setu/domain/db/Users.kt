package ie.setu.domain.db

import org.jetbrains.exposed.sql.Table

object Users : Table("users") {
    val id = integer("id").autoIncrement().primaryKey()
    val user_name = varchar("user_name", 100)
    val email = varchar("email", 255)
}