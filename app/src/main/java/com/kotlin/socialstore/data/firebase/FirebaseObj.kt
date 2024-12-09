package com.kotlin.socialstore.data.firebase

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth


object FirebaseObj {
    private lateinit var auth: FirebaseAuth

    fun startFirebase() {
        auth = Firebase.auth
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun createAccount(email: String, password: String, baseContext: Context) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    Toast.makeText(
                        baseContext,
                        "Register Success",
                        Toast.LENGTH_SHORT,
                    ).show()
                    val user = auth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Register failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    fun loginAccount(email: String, password: String, baseContext: Context) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    Toast.makeText(
                        baseContext,
                        "Authentication success",
                        Toast.LENGTH_SHORT,
                    ).show()
                    val user = auth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    fun sendPasswordResetEmail(email: String, baseContext: Context){
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Log.d(TAG, "Email enviado")
                    Toast.makeText(
                        baseContext,
                        "Email enviado",
                        Toast.LENGTH_SHORT,
                    ).show()
                }else {
                    Toast.makeText(
                        baseContext,
                        "Falha ao enviar email",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }
}