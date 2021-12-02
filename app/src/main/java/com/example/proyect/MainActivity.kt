package com.example.proyect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    lateinit var loginButton : Button
    lateinit var signUpButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginButton = findViewById(R.id.login_button)
        signUpButton = findViewById(R.id.signup_button)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null){
            val intent = Intent(this, HOME_ACTIVITY::class.java)
            startActivity(intent)
        }
    }

    fun login(v : View?) {
        val intent = Intent(this, LOGIN_ACTIVITY::class.java)
        startActivity(intent)
    }

    fun signup(v : View?) {
        val intent = Intent(this, SIGNUP_ACTIVITY::class.java)
        startActivity(intent)
    }
}