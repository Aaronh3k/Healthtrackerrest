package ie.setu.domain.repository

import ie.setu.domain.User
import ie.setu.domain.db.Users
import ie.setu.utils.mapToUser
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserDAO {

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

    fun save(user: User) : Int?{
        return try {
            transaction {
                Users.insert {
                    it[user_name] = user.user_name
                    it[email] = user.email
                } get Users.id
            }
        }catch (e: Exception){
            0
        }
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
                    it[user_name] = user.user_name
                    it[email] = user.email
                }
            }
        }catch (e: Exception){
            0
        }
    }
}