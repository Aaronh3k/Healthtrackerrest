package ie.setu.domain.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.ReferenceOption

object UserProfiles : Table("user_profile") {
    val id = integer("id").autoIncrement().primaryKey()
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val first_name = varchar("first_name", 100)
    val last_name = varchar("last_name", 100)
    val dob = datetime("dob")
    val gender = char("gender")
    val created_at = datetime("created_at")
}