package com.androidlabentryapp.models

data class User(
    val email: String,
    val password: String,
    val name: String = "",
    val surname: String = "",
    val image: String = ""
)
