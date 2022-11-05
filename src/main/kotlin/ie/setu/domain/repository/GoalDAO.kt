package ie.setu.domain.repository

import ie.setu.domain.Goal
import ie.setu.domain.db.Goals
import ie.setu.utils.mapToGoal
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class GoalDAO {

    //Get all the Goals in the database regardless of Goal id
    fun getAll(): ArrayList<Goal> {
        val goalsList: ArrayList<Goal> = arrayListOf()
        transaction {
            Goals.selectAll().map {
                goalsList.add(mapToGoal(it)) }
        }
        return goalsList
    }

    //Find a specific Goal by Goal id
    fun findByGoalId(id: Int): Goal?{
        return transaction {
            Goals
                .select() { Goals.id eq id}
                .map{mapToGoal(it)}
                .firstOrNull()
        }
    }

    //Save a Goal to the database
    fun save(Goal: Goal): Int {
        return transaction {
            Goals.insert {
                it[userId] = Goal.userId
                it[calories] = Goal.calories
                it[standing_hours] = Goal.standing_hours
                it[steps] = Goal.steps
                it[distance] = Goal.distance
                it[created_at] = Goal.created_at
            }
        } get Goals.id
    }

    fun updateByGoalId(goalId: Int, goalDTO: Goal): Int{
        return try {
            transaction {
                Goals.update ({
                    Goals.id eq goalId}) {
                    it[userId] = goalDTO.userId
                    it[calories] = goalDTO.calories
                    it[standing_hours] = goalDTO.standing_hours
                    it[steps] = goalDTO.steps
                    it[distance] = goalDTO.distance
                    it[created_at] = goalDTO.created_at
                }
            }
        }catch (e: Exception){
            0
        }
    }
    fun deleteByGoalId (goalId: Int): Int{
        return transaction{
            Goals.deleteWhere { Goals.id eq goalId }
        }
    }
}