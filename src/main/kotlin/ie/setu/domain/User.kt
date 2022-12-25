package ie.setu.domain
data class User (
    var id: Int? = null,
    var user_name:String? = null,
    var email:String,
    var token: String? = null,
    val password: String? = null)