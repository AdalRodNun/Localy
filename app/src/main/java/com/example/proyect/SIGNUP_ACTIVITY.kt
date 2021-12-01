package com.example.proyect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SIGNUP_ACTIVITY : AppCompatActivity() {

    lateinit var email : EditText
    lateinit var password : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        email = findViewById(R.id.email_text_edit_signup)
        password = findViewById(R.id.password_edit_text_signup)
    }

    fun registro(view : View?){
        Firebase.auth.createUserWithEmailAndPassword(
            email.text.toString(),
            password.text.toString()).addOnCompleteListener(this){

            if(it.isSuccessful){
                Log.d("FIREBASE", "Registro exitoso")
                val intent = Intent(this, HOME_ACTIVITY::class.java)
                startActivity(intent)
            } else {
                Log.e("FIREBASE", "Registro fracasó: ${it.exception?.message}")
                Toast.makeText(this, "Correo ya registrado o es incorrecto", Toast.LENGTH_SHORT).show();
            }
        }
    }
}