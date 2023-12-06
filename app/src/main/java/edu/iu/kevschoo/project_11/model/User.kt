package edu.iu.kevschoo.project_11.model

import java.util.Date

data class User(
    val id: String = "",
    val name: String = "",
    val lastname: String = "",
    val email: String = "",
    val signUpDate: Date = Date(),
    val tripLocations: List<String> = listOf()
)