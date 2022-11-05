package ie.setu.domain.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.ReferenceOption

object Goals : Table("goals") {
    val id = integer("id").autoIncrement().primaryKey()
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val calories = integer("calories")
    val standing_hours = float("standing_hours")
    val steps = integer("steps")
    val distance = integer("distance")
    val created_at = datetime("created_at")
}