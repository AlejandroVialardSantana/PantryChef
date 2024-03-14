package com.avs.pantrychef.model

data class User(
    val userId: String,
    val username: String,
    val email: String,
    val favoriteRecipes: List<String>
)
