package com.example.util

import io.ktor.websocket.*

//data class ConnectedUser(val session: DefaultWebSocketSession, val username: String)

data class ConnectedUser(val username: String) {
    var session: DefaultWebSocketSession? = null
}

//data class ConnectedUser(val session: DefaultWebSocketSession?, val username: String) {
//    constructor(username: String): this(null, username)
//}