package ie.setu.domain

data class UserDTO(val user: User? = null)

data class User (
    var id: Int,
    var name:String,
    var email:String)