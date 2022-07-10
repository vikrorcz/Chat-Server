package com.example.plugins


import com.example.services.ProfileService
import com.example.util.SimpleJWT
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.mindrot.jbcrypt.BCrypt
import org.postgresql.core.TypeInfo

data class LoginRegister(

    val email: String,
    val password: String
    )

fun Application.configureRouting() {

    val profileService = ProfileService()

    routing {
        get("/") {
            call.respondText("Hello Chat Server!")
        }

        post("/register") {
            val creds = call.receive<LoginRegister>()

            val profile = profileService.getProfileByEmail(creds.email)

            for (i in profileService.getAllUsers()) {
                if (profile?.email == i.email) {
                    call.respond("Email address already in use.")
                    return@post
                }
            }

            profileService.registerProfile(creds.email, BCrypt.hashpw(creds.password, BCrypt.gensalt()))
            call.respond("Success!")
        }

        post("/login") {

            val creds = call.receive<LoginRegister>()

            val profile = profileService.getProfileByEmail(creds.email)
            if (profile == null || !BCrypt.checkpw(creds.password, profile.password)) {
                call.respond("Invalid credentials.")
                return@post
            }
            val token = SimpleJWT.createJwtToken(profile.email)
            call.respond(hashMapOf("token" to token))
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
