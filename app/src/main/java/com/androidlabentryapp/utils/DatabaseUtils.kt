package com.androidlabentryapp.utils

import com.androidlabentryapp.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.math.BigInteger
import java.security.MessageDigest

private val usersRef = FirebaseDatabase.getInstance().getReference("users")

internal fun getUserKey(userEmail: String) =
    BigInteger(
        1,
        MessageDigest.getInstance("MD5")
            .digest(userEmail.toByteArray())
    )
        .toString(16)
        .padStart(32, '0')

internal fun saveUserImageToCloud(userEmail: String, imageString: String) =
    with(imageString) {
        log("Saving user image to database")
        usersRef.child(getUserKey(userEmail)).child("image").setValue(imageString)
    }

internal fun User.saveToCloud() =
    saveUserToCloud(this)

internal fun saveUserToCloud(user: User) =
    with(user) {
        log("Saving user to database")
        usersRef.child(getUserKey(email)).setValue(this)
    }

internal fun getUserOrNull(email: String, password: String, callback: User?.() -> Unit) =
    usersRef.orderByChild("email").equalTo(email)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                log("Got data from Firebase:\n$dataSnapshot")
                with(dataSnapshot.children.iterator().next()) {
                        if (password == child("password").getData<String>()) {
                            log("Got matching user:\n$this")
                            User(
                                email,
                                password,
                                child("name").getData<String>() ?: "",
                                child("surname").getData<String>() ?: "",
                                child("image").getData<String>() ?: ""
                            )
                        } else {
                            null
                        }.callback()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                log("Error while getting user from Firebase:\n$databaseError")
                null.callback()
            }
        })

internal fun isEmailPresent(email: String, callback: Boolean.() -> Unit) =
    usersRef.orderByChild("email").equalTo(email)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) =
                if (snapshot.value == null) {
                    false
                } else {
                    log("Found duplicate email for user: $snapshot")
                    true
                }.callback()

            override fun onCancelled(error: DatabaseError) =
                false.callback()
        })

private inline fun <reified T> DataSnapshot.getData() =
    this.getValue(T::class.java)