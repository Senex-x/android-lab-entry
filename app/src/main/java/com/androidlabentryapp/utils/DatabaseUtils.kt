package com.androidlabentryapp.utils

import com.androidlabentryapp.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.math.BigInteger
import java.security.MessageDigest

private val usersRef = FirebaseDatabase.getInstance().getReference("users")

internal fun getUserKey(userEmail: String): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(userEmail.toByteArray()))
        .toString(16)
        .padStart(32, '0')
}

internal fun saveUserImageToCloud(userEmail: String, imageString: String) =
    with(imageString) {
        log("Saving user image to database")
        usersRef.child(getUserKey(userEmail)).child("image").setValue(imageString)
    }


internal fun saveUserToCloud(user: User) =
    with(user) {
        log("Saving user to database")
        usersRef.child(getUserKey(email)).setValue(this)
    }

internal fun getUserOrNull(email: String, password: String, callback: (User?) -> Unit) =
    usersRef.orderByChild("email").equalTo(email)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot in dataSnapshot.children) {
                    if (password == userSnapshot.child("password").getValue(String::class.java)) {
                        log(
                            """Got matching user: 
                            |$userSnapshot""".trimMargin()
                        )
                        callback.invoke(
                            User(
                                email,
                                password,
                                userSnapshot.child("name").getValue(String::class.java) ?: "",
                                userSnapshot.child("surname").getValue(String::class.java) ?: "",
                                userSnapshot.child("image").getValue(String::class.java) ?: ""
                            )
                        )
                    } else {
                        callback.invoke(null)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                log("Matching user not found")
                callback.invoke(null)
            }
        })

internal fun isEmailPresent(email: String, callback: (Boolean) -> Unit) {
    usersRef.orderByChild("email").equalTo(email)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) =
                callback.invoke(
                    if (snapshot.value == null) {
                        false
                    } else {
                        log("Found duplicate email for user: $snapshot")
                        true
                    }
                )

            override fun onCancelled(error: DatabaseError) =
                callback.invoke(false)
        })
}
