package com.example.plugins


import com.example.models.ProfileType
import com.example.models.entities.*
import com.example.services.ProfileService
import com.example.util.SimpleJWT
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.mindrot.jbcrypt.BCrypt

fun Application.configureRouting() {

    val profileService = ProfileService()

    routing {
        get("/") {
            call.respondText("Hello Chat Server!")
        }

        post("/update") {
            val creds = call.receive<UpdateUserPassword>()

            //first try to login
            val profile: ProfileType? = profileService.getProfileByUsername(creds.username)
            if (profile == null || !BCrypt.checkpw(creds.oldPassword, profile.password)) {
                //login failed
                call.respond(LoginRegisterResponse("Current password is invalid"))
                return@post
            }

            //proceed with changing the password
            profileService.updateProfilePassword(creds.username, newPassword = creds.newPassword)
            call.respond(LoginRegisterResponse("Successfully changed password"))
        }

        post("/register") {
            val creds = call.receive<RegisterUser>()

            var profile: ProfileType? = profileService.getProfileByEmail(creds.email)
            for (i in profileService.getAllUsers()) {
                if (profile?.email == i.email) {
                    call.respond(LoginRegisterResponse("Email address already in use"))
                    return@post
                }
            }

            profile = profileService.getProfileByUsername(creds.username)
            for (i in profileService.getAllUsers()) {
                if (profile?.username == i.username) {
                    call.respond(LoginRegisterResponse("Username already in use"))
                    return@post
                }
            }

            profileService.registerProfile(creds.email, creds.username, BCrypt.hashpw(creds.password, BCrypt.gensalt()))
            call.respond(LoginRegisterResponse("Successfully registered"))
        }

        post("/login") {

            val creds = call.receive<LoginUser>()

            var profile: ProfileType? = profileService.getProfileByUsername(creds.username)
            if (profile == null || !BCrypt.checkpw(creds.password, profile!!.password)) {
                //username does not exist try login with email
                profile = profileService.getProfileByEmail(creds.username)
                if (profile == null || !BCrypt.checkpw(creds.password, profile!!.password)) {
                    call.respond(LoginRegisterResponse("Invalid credentials"))
                    return@post
                }
            }

            call.respond(LoginRegisterResponse("Logged in"))
        }


        //token  email abc, password 123
        //token ex: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJqd3QtYXVkaWVuY2UiLCJpc3MiOiJodHRwOi8vMC4wLjAuMDo4MDgwLyIsImV4cCI6MTY1NzQ3ODQ3MCwiZW1haWwiOiJhYmMifQ.i0f_htCypJlQxOINJXPC1UznoHd8gQFOnCi6qFO1ruc
        //authenticate {
            get("/profiles") {
                val users = profileService.getAllUsers()
                call.respond(users)
            }
        //}
    }
}
