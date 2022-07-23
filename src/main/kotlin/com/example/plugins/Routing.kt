package com.example.plugins


import com.example.models.ProfileType
import com.example.models.entities.LoginRegisterResponse
import com.example.models.entities.LoginUser
import com.example.models.entities.RegisterUser
import com.example.services.ProfileService
import com.example.util.SimpleJWT
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.mindrot.jbcrypt.BCrypt

/*
data class LoginRegister(

    val email: String,
    val password: String
    )*/

fun Application.configureRouting() {

    val profileService = ProfileService()

    routing {
        get("/") {
            call.respondText("Hello Chat Server!")
        }

        post("/register") {
            val creds = call.receive<RegisterUser>()

            val profile = profileService.getProfileByEmail(creds.email)

            //Only solution that applies id correctly?
            for (i in profileService.getAllUsers()) {
                if (profile?.email == i.email) {
                    call.respond(LoginRegisterResponse("Email address already in use"))
                    return@post
                }
            }

            for (i in profileService.getAllUsers()) {
                if (profile?.username == i.username) {
                    call.respond(LoginRegisterResponse("Username already in use"))
                    return@post
                }
            }


            //Check if username is available
            /*
            var profile: ProfileType? = profileService.getProfileByUsername(creds.username)
            if (profile != null) {
                call.respond(LoginRegisterResponse("Username already in use"))
                return@post
            }

            //Check if username is available
            profile = profileService.getProfileByEmail(creds.email)
            if (profile != null) {
                call.respond(LoginRegisterResponse("Email already in use"))
                return@post
            }
            */

            profileService.registerProfile(creds.email, creds.username, BCrypt.hashpw(creds.password, BCrypt.gensalt()))
            call.respond(LoginRegisterResponse("Successfully registered"))
        }

        post("/login") {

            val creds = call.receive<LoginUser>()

            /*
            //1st solution probably better
            //don't know if for loop is good solution - investigate
            var isEmail = false
            for (i in profileService.getAllUsers()) {
                if (creds.username != i.username) {
                    println("Username does not exist trying email")
                    isEmail = true
                }
            }

            val profile: ProfileType? = if (isEmail) {
                profileService.getProfileByEmail(creds.username)
            } else {
                profileService.getProfileByUsername(creds.username)
            }

            if (profile == null || !BCrypt.checkpw(creds.password, profile.password)) {
                call.respond("User does not exist")
            }

             val token = SimpleJWT.createJwtToken(profile.email)
            */

            //2nd solution probably better

            var profile: ProfileType?
            profile = profileService.getProfileByUsername(creds.username)
            if (profile == null || !BCrypt.checkpw(creds.password, profile.password)) {
                //username does not exist try login with email
                profile = profileService.getProfileByEmail(creds.username)
                if (profile == null || !BCrypt.checkpw(creds.password, profile.password)) {
                    call.respond(LoginRegisterResponse("Invalid credentials"))
                    return@post
                }
            }

            call.respond(LoginRegisterResponse("Logged in"))
            //val token = SimpleJWT.createJwtToken(profile.email)
            //call.respond(hashMapOf("token" to token))
        }


        //token  email abc, password 123
        //token ex: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJqd3QtYXVkaWVuY2UiLCJpc3MiOiJodHRwOi8vMC4wLjAuMDo4MDgwLyIsImV4cCI6MTY1NzQ3ODQ3MCwiZW1haWwiOiJhYmMifQ.i0f_htCypJlQxOINJXPC1UznoHd8gQFOnCi6qFO1ruc
        authenticate {
            get("/profiles") {
                val users = profileService.getAllUsers()
                call.respond(users)
            }
        }
    }
}
