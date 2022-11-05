package ie.setu.domain.db
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
object Activities : Table("activities") {
    val id = integer("id").autoIncrement().primaryKey()
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val categoryId = integer("category_id").references(Categories.id, onDelete = ReferenceOption.CASCADE)
    val description = varchar("description", 100)
    val duration = double("duration")
    val calories = double("calories")
    val distance = double("distance")
    val started = datetime("started")
    val created_at = datetime("created_at")
}