package com.example.services

import com.example.util.DatabaseFactory.dbQuery
import com.example.models.Profile
import com.example.models.ProfileType
import org.jetbrains.exposed.sql.*
import org.mindrot.jbcrypt.BCrypt

class ProfileService {

    //https://www.baeldung.com/kotlin/exposed-persistence

    suspend fun getAllUsers(): List<ProfileType> = dbQuery {
        Profile.selectAll().map { toProfileType(it) }
    }

    suspend fun getProfileByEmail(email: String): ProfileType? = dbQuery {
        Profile.select {
            (Profile.email eq email)
        }.mapNotNull { toProfileType(it) }
            .singleOrNull()
    }

    suspend fun getProfileByUsername(username: String): ProfileType? = dbQuery {
        Profile.select {
            (Profile.username eq username)
        }.mapNotNull { toProfileType(it) }
            .singleOrNull()
    }

    suspend fun registerProfile(email: String, username: String, passwordHash: String) = dbQuery {
        Profile.insert {
            it[Profile.email] = email
            it[Profile.username] = username
            it[password] = passwordHash
        }
    }

    suspend fun updateProfilePassword(username: String, newPassword: String) = dbQuery {
        Profile.update ({ Profile.username eq username }) {
            it[password] = BCrypt.hashpw(newPassword, BCrypt.gensalt())
        }
    }

    //suspend fun deleteProfile(username: String) {
    //    val profile = getProfileByUsername(username)
//
    //    Profile.deleteWhere({ Profile.id eq })
    //}

    private fun toProfileType(row: ResultRow): ProfileType =
        ProfileType(
            id = row[Profile.id],
            email = row[Profile.email],
            username = row[Profile.username],
            password = row[Profile.password]
        )
}