package com.avs.pantrychef.models

data class User(
    val userId: String,
    val username: String,
    val email: String,
    val favoriteRecipes: List<String>
)
