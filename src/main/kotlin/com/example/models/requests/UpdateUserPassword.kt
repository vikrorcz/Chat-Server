package com.example.models.requests

data class UpdateUserPassword(val username: String, val oldPassword: String, val newPassword: String)