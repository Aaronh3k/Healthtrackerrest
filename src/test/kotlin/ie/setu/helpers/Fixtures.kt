package ie.setu.helpers

import ie.setu.domain.*
import ie.setu.domain.db.Activities
import ie.setu.domain.db.UserProfiles
import ie.setu.domain.db.Goals
import ie.setu.domain.db.Users
import ie.setu.domain.db.Categories
import ie.setu.domain.repository.ActivityDAO
import ie.setu.domain.repository.GoalDAO
import ie.setu.domain.repository.ProfileDAO
import ie.setu.domain.repository.CategoryDAO
import ie.setu.domain.repository.UserDAO
import org.jetbrains.exposed.sql.SchemaUtils
import org.joda.time.DateTime

const val nonExistingEmail = "112233445566778testUser@xxxxx.xx"
const val validUserName = "Test User 1"
const val validEmail = "testuser1@test.com"
const val validPassword = "password"
const val validToken = "tokennnn"
const val validRole = "ROLE_USER"
const val updatedUserName = "Updated Name"
const val updatedEmail = "Updated Email"
const val updatedPassword = "updated password"
const val updatedToken = "updated tokennnn"
const val updatedRole = "ROLE_USER"

const val validFirstname = "First Name"
const val validlastname = "Last Name"
val validdob = DateTime.parse("2020-06-11T05:59:27.258Z")
const val validGender = 'M'

const val updatedDescription = "Updated Description"
const val updatedDuration = 30.0
const val updatedCalories = 945.0
val updatedStarted: DateTime = DateTime.parse("2020-06-11T05:59:27.258Z")
const val updatedDistance = 5.5

const val category_name = "Indoor"
const val category_description = "Indoor Desc"
val updateCreatedAt: DateTime = DateTime.now()

const val goal_standing_hours = 5.0
const val goal_steps = 2100
const val goal_calories = 500
const val goal_distance = 7

val users = arrayListOf<User>(
    User(user_name = "alice_wonderland", email = "alice@wonderland.com", id = 1, password = "password1", role = "ROLE_USER", token = "token"),
    User(user_name = "bob_cat", email = "bob@cat.ie", id = 2, password = "password1", role = "ROLE_USER", token = "token"),
    User(user_name = "mary_contrary", email = "mary@contrary.com", id = 3,  password = "password1", role = "ROLE_USER", token = "token"),
    User(user_name = "carol_singer", email = "carol@singer.com", id = 4, password = "password1", role = "ROLE_USER", token = "token")
)

val categories = arrayListOf<Category>(
    Category(id = 1, name = "Indoor", description = "Indoor Desc", created_at = DateTime.now()),
    Category(id = 2, name = "Outdoor", description = "Outdoor Desc", created_at = DateTime.now()),
    Category(id = 3, name = "Semi", description = "Semi Desc", created_at = DateTime.now()),
)

val activities = arrayListOf<Activity>(
    Activity(id = 1, description = "Running", duration = 22.0, calories = 230.0, started = DateTime.now(), userId = 1, categoryId = 2, distance = 3.5, created_at = DateTime.parse("1998-06-11")),
    Activity(id = 2, description = "Hopping", duration = 10.5, calories = 80.0, started = DateTime.now(), userId = 3, categoryId = 1, distance = 5.5, created_at = DateTime.parse("1998-06-11")),
    Activity(id = 3, description = "Walking", duration = 12.0, calories = 120.0, started = DateTime.now(), userId = 2, categoryId = 3, distance = 6.5, created_at = DateTime.parse("1998-06-11"))
)

val goals = arrayListOf<Goal>(
    Goal(id = 1, standing_hours = 2.0, steps = 2200, calories = 230,  userId = 1, distance = 3, created_at = DateTime.now()),
    Goal(id = 2, standing_hours = 5.0, steps = 1000, calories = 80, userId = 3, distance = 5, created_at = DateTime.now()),
    Goal(id = 3, standing_hours = 4.5, steps = 1200, calories = 120, userId = 2, distance = 6, created_at = DateTime.now())
)

val userprofile = arrayListOf<Profile>(
    Profile(id = 1, first_name = "Test", last_name = "test", gender = 'M',  dob = DateTime.parse("1998-06-11"), userId = 1, created_at = DateTime.parse("1998-06-11")),
    Profile(id = 2, first_name = "Aaron", last_name = "Pinto", gender = 'M',  dob = DateTime.parse("1990-06-11"), userId = 3, created_at = DateTime.parse("1998-06-11")),
    Profile(id = 3, first_name = "First", last_name = "Name", gender = 'F',  dob = DateTime.parse("1995-06-11"), userId = 2, created_at = DateTime.parse("1998-06-11")),
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

fun populateGoalTable(): GoalDAO {
    SchemaUtils.create(Goals)
    val goalDAO = GoalDAO()
    goalDAO.save(goals[0])
    goalDAO.save(goals[1])
    goalDAO.save(goals[2])
    return goalDAO
}

fun populateProfileTable(): ProfileDAO {
    SchemaUtils.create(UserProfiles)
    val profileDAO = ProfileDAO()
    profileDAO.save(userprofile[0])
    profileDAO.save(userprofile[1])
    profileDAO.save(userprofile[2])
    return profileDAO
}