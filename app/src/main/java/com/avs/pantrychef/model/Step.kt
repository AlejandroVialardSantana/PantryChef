package com.avs.pantrychef.model

data class Step(
    val id: String,
    val description: String,
    val order: Int,
    val time: Int
)