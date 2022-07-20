package com.example.services

import com.example.DatabaseFactory.dbQuery
import com.example.models.Profile
import com.example.models.ProfileType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class ProfileService {

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

    private fun toProfileType(row: ResultRow): ProfileType =
        ProfileType(
            id = row[Profile.id],
            email = row[Profile.email],
            username = row[Profile.username],
            password = row[Profile.password]
        )
}