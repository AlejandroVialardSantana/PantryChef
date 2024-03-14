package com.avs.pantrychef.model

data class Recipe(
    val id: String,
    val title: String,
    val ingredients: List<RecipeIngredient>,
    val steps: List<Step>,
    val difficulty: String,
    val preparationTime: Int,
    val type: String
)
