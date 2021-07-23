package com.androidlabentryapp.utils

import com.androidlabentryapp.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private val usersRef = FirebaseDatabase.getInstance().getReference("users")

internal fun getUserOrNull(email: String, password: String, callback: (User?) -> Unit) {
    usersRef.orderByChild("email").equalTo(email)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot in dataSnapshot.children) {
                    if (password == userSnapshot.child("password").getValue(String::class.java))
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
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                log("Matching user not found")
                callback.invoke(null)
            }
        })
}