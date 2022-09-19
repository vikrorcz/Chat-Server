package com.example.util

import io.ktor.websocket.*

class ConnectedUser(var session: DefaultWebSocketSession, var username: String)

//data class ConnectedUser(val username: String) {
//    var session: DefaultWebSocketSession? = null
//}

//data class ConnectedUser(val session: DefaultWebSocketSession?, val username: String) {
//    constructor(username: String): this(null, username)
//}