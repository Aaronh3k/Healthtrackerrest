package ie.setu.domain.repository

import ie.setu.domain.Profile
import ie.setu.domain.db.UserProfiles
import ie.setu.utils.mapToProfile
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class ProfileDAO {

    //Get all the UserProfile in the database regardless of Profile id
    fun getAll(): ArrayList<Profile> {
        val profileList: ArrayList<Profile> = arrayListOf()
        transaction {
            UserProfiles.selectAll().map {
                profileList.add(mapToProfile(it)) }
        }
        return profileList
    }

    //Find a specific Profile by Profile id
    fun findByProfileId(id: Int): Profile?{
        return transaction {
            UserProfiles
                .select() { UserProfiles.id eq id}
                .map{mapToProfile(it)}
                .firstOrNull()
        }
    }

    //Save a Profile to the database
    fun save(profile: Profile): Int {
        return transaction {
            UserProfiles.insert {
                it[userId] = profile.userId
                it[first_name] = profile.first_name
                it[last_name] = profile.last_name
                it[dob] = profile.dob
                it[gender] = profile.gender
                it[created_at] = profile.created_at
            }
        } get UserProfiles.id
    }

    fun updateByProfileId(profileId: Int, profileDTO: Profile): Int{
        return try {
            transaction {
                UserProfiles.update ({
                    UserProfiles.id eq profileId}) {
                    it[userId] = profileDTO.userId
                    it[first_name] = profileDTO.first_name
                    it[last_name] = profileDTO.last_name
                    it[dob] = profileDTO.dob
                    it[gender] = profileDTO.gender
                    it[created_at] = profileDTO.created_at
                }
            }
        }catch (e: Exception){
            0
        }
    }
    fun deleteByProfileId(goalId: Int): Int{
        return transaction{
            UserProfiles.deleteWhere { UserProfiles.id eq goalId }
        }
    }
}