package com.example.plugins


import com.example.models.ProfileType
import com.example.models.requests.*
import com.example.models.responses.AutoLoginResponse
import com.example.models.responses.LoginRegisterResponse
import com.example.models.responses.SearchUserResponse
import com.example.services.ProfileService
import com.example.models.websocket.ChatMessage
import com.example.util.ConnectedUser
import com.example.util.JWTUtil
import com.google.gson.Gson
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.ktor.websocket.serialization.*
import org.mindrot.jbcrypt.BCrypt
import java.util.*
import kotlin.collections.LinkedHashSet

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
                call.respond(LoginRegisterResponse("Current password is invalid", null))
                return@post
            }

            //proceed with changing the password
            profileService.updateProfilePassword(creds.username, newPassword = creds.newPassword)
            call.respond(LoginRegisterResponse("Successfully changed password", null))
        }

        post("/register") {
            val creds = call.receive<RegisterUser>()

            var profile: ProfileType? = profileService.getProfileByEmail(creds.email)
            for (i in profileService.getAllUsers()) {
                if (profile?.email == i.email) {
                    call.respond(LoginRegisterResponse("Email address already in use", null))
                    return@post
                }
            }

            profile = profileService.getProfileByUsername(creds.username)
            for (i in profileService.getAllUsers()) {
                if (profile?.username == i.username) {
                    call.respond(LoginRegisterResponse("Username already in use", null))
                    return@post
                }
            }

            profileService.registerProfile(creds.email, creds.username, BCrypt.hashpw(creds.password, BCrypt.gensalt()))

            call.respond(LoginRegisterResponse("Successfully registered", null))
        }

        post("/login") {

            val creds = call.receive<LoginUser>()

            var profile: ProfileType? = profileService.getProfileByUsername(creds.username)
            if (profile == null || !BCrypt.checkpw(creds.password, profile!!.password)) {
                //username does not exist try login with email
                profile = profileService.getProfileByEmail(creds.username)
                if (profile == null || !BCrypt.checkpw(creds.password, profile!!.password)) {
                    call.respond(LoginRegisterResponse("Invalid credentials", null))
                    return@post
                }
            }

            val jwt = JWTUtil.createJwtToken(creds.username).toString()
            call.respond(LoginRegisterResponse("Logged in", jwt))
        }

        authenticate {
            get("/auto-login") {
                val principal = call.principal<UserIdPrincipal>()
                val email = principal?.name
                call.respond(AutoLoginResponse(email.toString()))
            }
        }

        post("/search-user"){
            val user = call.receive<SearchUser>()

            var profile: ProfileType? = profileService.getProfileByUsername(user.user)
            if (profile == null) {
                //username does not exist try finding user by email
                profile = profileService.getProfileByEmail(user.user)
                if (profile == null) {
                    call.respond(SearchUserResponse("User not found"))
                    return@post
                }
            }
            call.respond(SearchUserResponse("User found", profile!!.username, profile!!.email))
        }

        val connections = Collections.synchronizedSet<ConnectedUser>(LinkedHashSet())

        webSocket("/chat") {
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                val messageDeserialized = Gson().fromJson(receivedText, ChatMessage::class.java)

                println(
                    "Received chat message: Message: ${messageDeserialized.message}," +
                            " SendingUser: ${messageDeserialized.username}," +
                            " ReceivingUser: ${messageDeserialized.receivingUsername}"
                )

                //Add user to connections
                if (!(connections.any { it.username == messageDeserialized.username })) {
                    val connectedUser = ConnectedUser(this, messageDeserialized.username)
                    connections += connectedUser
                    println("There are ${connections.count()} users here.")
                } else {
                    for (connection in connections) {
                        if (connection.username == messageDeserialized.username)  {
                            connection.session = this
                        }
                    }
                }

                connections.forEach {
                    if (it.username == messageDeserialized.receivingUsername) {
                        it.session.send(receivedText)
                        println("Sending to ${it.username}")
                    }
                }
            }
        }
    }
}
