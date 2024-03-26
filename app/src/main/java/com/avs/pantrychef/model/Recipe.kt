package com.avs.pantrychef.model

data class Recipe(
    val id: String = "",
    val title: String = "",
    val ingredients: List<String> = emptyList(),
    val steps: List<String> = emptyList(),
    val difficulty: String = "",
    val preparationTime: Int = 0,
    val type: String = ""
)
