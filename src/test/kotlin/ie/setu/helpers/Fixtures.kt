package ie.setu.helpers

import ie.setu.domain.Activity
import ie.setu.domain.User
import ie.setu.domain.Category
import ie.setu.domain.db.Activities
import ie.setu.domain.db.Users
import ie.setu.domain.db.Categories
import ie.setu.domain.repository.ActivityDAO
import ie.setu.domain.repository.CategoryDAO
import ie.setu.domain.repository.UserDAO
import org.jetbrains.exposed.sql.SchemaUtils
import org.joda.time.DateTime

const val nonExistingEmail = "112233445566778testUser@xxxxx.xx"
const val validUserName = "Test User 1"
const val validEmail = "testuser1@test.com"
const val updatedUserName = "Updated Name"
const val updatedEmail = "Updated Email"

const val updatedDescription = "Updated Description"
const val updatedDuration = 30.0
const val updatedCalories = 945.0
val updatedStarted: DateTime = DateTime.parse("2020-06-11T05:59:27.258Z")
const val updatedDistance = 5.5

const val category_name = "Indoor"
const val category_description = "Indoor Desc"
val updateCreatedAt: DateTime = DateTime.now()

val users = arrayListOf<User>(
    User(user_name = "Alice Wonderland", email = "alice@wonderland.com", id = 1),
    User(user_name = "Bob Cat", email = "bob@cat.ie", id = 2),
    User(user_name = "Mary Contrary", email = "mary@contrary.com", id = 3),
    User(user_name = "Carol Singer", email = "carol@singer.com", id = 4)
)

val categories = arrayListOf<Category>(
    Category(id = 1, name = "Indoor", description = "Indoor Desc", created_at = DateTime.now()),
    Category(id = 2, name = "Outdoor", description = "Outdoor Desc", created_at = DateTime.now()),
    Category(id = 3, name = "Semi", description = "Semi Desc", created_at = DateTime.now()),
)

val activities = arrayListOf<Activity>(
    Activity(id = 1, description = "Running", duration = 22.0, calories = 230.0, started = DateTime.now(), userId = 1, categoryId = 2, distance = 3.5, created_at = DateTime.now()),
    Activity(id = 2, description = "Hopping", duration = 10.5, calories = 80.0, started = DateTime.now(), userId = 3, categoryId = 1, distance = 5.5, created_at = DateTime.now()),
    Activity(id = 3, description = "Walking", duration = 12.0, calories = 120.0, started = DateTime.now(), userId = 2, categoryId = 3, distance = 6.5, created_at = DateTime.now())
)

fun populateUserTable(): UserDAO {
    SchemaUtils.create(Users)
    val userDAO = UserDAO()
    userDAO.save(users[0])
    userDAO.save(users[1])
    userDAO.save(users[2])
    return userDAO
}
fun populateActivityTable(): ActivityDAO {
    SchemaUtils.create(Activities)
    val activityDAO = ActivityDAO()
    activityDAO.save(activities[0])
    activityDAO.save(activities[1])
    activityDAO.save(activities[2])
    return activityDAO
}

fun populateCategoryTable(): CategoryDAO {
    SchemaUtils.create(Categories)
    val categoryDAO = CategoryDAO()
    categoryDAO.save(categories[0])
    categoryDAO.save(categories[1])
    categoryDAO.save(categories[2])
    return categoryDAO
}