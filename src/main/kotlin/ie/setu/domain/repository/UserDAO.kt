package ie.setu.domain.repository

import ie.setu.domain.User
import ie.setu.domain.db.Users
import ie.setu.utils.mapToUser
import io.javalin.http.HttpResponseException
import org.eclipse.jetty.http.HttpStatus
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ie.setu.utils.JwtProvider
import ie.setu.config.Roles

class UserDAO {

    private val jwtProvider = JwtProvider()

    fun getAll(): ArrayList<User> {
        val userList: ArrayList<User> = arrayListOf()
        transaction {
            Users.selectAll().map {
                userList.add(mapToUser(it)) }
        }
        return userList
    }

    fun findById(id: Int): User?{
        return transaction {
            Users.select() {
                Users.id eq id}
                .map{mapToUser(it)}
                .firstOrNull()
        }
    }

    fun create(user: User) :  Int? {
        return transaction {
            Users.insert { row ->
                row[Users.email] = user.email
                row[Users.user_name] = user.user_name!!
                row[Users.password] = user.password!!
            } get Users.id
        }
    }

    fun generateJwtToken(user: User): String? {
        return if (user.email == "admin@healthtracker.com")
            jwtProvider.createJWT(user, Roles.ADMIN)
        else
            jwtProvider.createJWT(user, Roles.USER)
    }

    fun findByEmail(email: String) :User?{
        return transaction {
            Users.select() {
                Users.email eq email}
                .map{mapToUser(it)}
                .firstOrNull()
        }
    }

    fun delete(id: Int):Int{
        return transaction{
            Users.deleteWhere{
                Users.id eq id
            }
        }
    }

    fun update(id: Int, user: User): Int{
        return try{
            transaction {
                Users.update ({
                    Users.id eq id}) {
                    it[user_name] = user.user_name!!
                    it[email] = user.email
                }
            }
        }catch (e: Exception){
            0
        }
    }

    fun updatebyemail(Email: String, user: User): Int{
        return try{
            transaction {
                Users.update ({
                    Users.email eq Email}) {
                    it[user_name] = user.user_name!!
                    it[email] = user.email
                }
            }
        }catch (e: Exception){
            0
        }
    }
}