package ie.setu.domain

import org.joda.time.DateTime

data class Profile(
    var id: Int,
    var userId: Int,
    var first_name: String,
    var last_name: String,
    var dob: DateTime,
    var gender: Char,
    var created_at: DateTime? = null)