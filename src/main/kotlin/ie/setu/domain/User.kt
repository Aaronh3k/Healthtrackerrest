package ie.setu.domain

data class UserDTO(val user: User? = null)

data class User (val id: Int,
                 val name:String,
                 val email:String)