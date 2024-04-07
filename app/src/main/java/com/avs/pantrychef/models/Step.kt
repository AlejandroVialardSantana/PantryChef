package com.avs.pantrychef.models

data class Step(
    val id: String,
    val description: String,
    val order: Int,
    val recipeId: String,
    val time: Int?
)