package com.example.models.responses

data class SearchUserResponse(val message: String, val username: String, val email: String) {
    constructor(message: String) : this(message, "", "")
}

