package ie.setu.domain

import org.joda.time.DateTime

data class Profile(
    var id: Int,
    var userId: Int,
    var first_name: String? = null,
    var last_name: String? = null,
    var dob: DateTime? = null,
    var gender: Char? = null,
    var created_at: DateTime? = null)