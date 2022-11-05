package ie.setu.utils

import ie.setu.domain.*
import ie.setu.domain.db.*
import org.jetbrains.exposed.sql.ResultRow

fun mapToUser(it: ResultRow) = User(
    id = it[Users.id],
    user_name = it[Users.user_name],
    email = it[Users.email]
)
fun mapToActivity(it: ResultRow) = Activity(
        id = it[Activities.id],
        userId = it[Activities.userId],
        categoryId = it[Activities.categoryId],
        description = it[Activities.description],
        duration = it[Activities.duration],
        started = it[Activities.started],
        calories = it[Activities.calories],
        distance = it[Activities.distance],
        created_at = it[Activities.created_at]
    )

fun mapToProfile(it: ResultRow) = Profile(
    id = it[UserProfiles.id],
    userId = it[UserProfiles.userId],
    first_name = it[UserProfiles.first_name],
    last_name = it[UserProfiles.last_name],
    dob = it[UserProfiles.dob],
    gender = it[UserProfiles.gender],
    created_at = it[UserProfiles.created_at],
    updated_at = it[UserProfiles.updated_at]
)
fun mapToGoal(it: ResultRow) = Goal(
    id = it[Goals.id],
    userId = it[Goals.userId],
    calories = it[Goals.calories],
    distance = it[Goals.distance],
    standing_hours = it[Goals.standing_hours],
    steps = it[Goals.steps],
    created_at = it[Goals.created_at],
    updated_at = it[Goals.updated_at]
)

fun mapToCategory(it: ResultRow) = Category(
    id = it[Categories.id],
    name = it[Categories.name],
    description = it[Categories.description],
    created_at = it[Categories.created_at]
)
