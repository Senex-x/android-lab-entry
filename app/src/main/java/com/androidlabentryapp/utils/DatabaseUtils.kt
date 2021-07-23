package com.androidlabentryapp.utils

import com.androidlabentryapp.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private val usersRef = FirebaseDatabase.getInstance().getReference("users")

internal fun saveUserToCloud(user: User) =
    with(user) {
        log("Saving user to database")
        usersRef.child(encodeString(email)).setValue(this)
    }

internal fun getUserOrNull(email: String, password: String, callback: (User?) -> Unit) =
    usersRef.orderByChild("email").equalTo(email)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot in dataSnapshot.children) {
                    if (password == userSnapshot.child("password").getValue(String::class.java)) {
                        log("Got matching user: $userSnapshot")
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
        .addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                log("Found duplicate email for user: $snapshot")
                callback.invoke(true)
            }

            override fun onCancelled(error: DatabaseError) {
                callback.invoke(false)
            }
        })
}
