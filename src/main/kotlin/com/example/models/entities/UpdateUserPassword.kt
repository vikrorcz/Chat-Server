package com.example.models.entities

data class UpdateUserPassword(val username: String, val oldPassword: String, val newPassword: String) {
}