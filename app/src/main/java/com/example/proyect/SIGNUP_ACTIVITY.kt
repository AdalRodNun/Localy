package com.example.proyect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
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

    fun revisarCampos(v : View) {
        if(email.text.toString().isEmpty()){
            Toast.makeText(this, "Falta agregar el correo electronico", Toast.LENGTH_SHORT).show()
            return
        }
        if(password.text.toString().isEmpty()){
            Toast.makeText(this, "Falta agregar la contraseña", Toast.LENGTH_SHORT).show()
            return
        }
        registro()
    }

    fun registro(){
        Firebase.auth.createUserWithEmailAndPassword(
            email.text.toString(),
            password.text.toString()).addOnCompleteListener(this){

            if(it.isSuccessful){
                Log.d("FIREBASE", "Registro exitoso")
                registroExtra()
            } else {
                Log.e("FIREBASE", "Registro fracasó: ${it.exception?.message}")
                Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()            }
        }
    }

    fun registroExtra() {

        val usuario = hashMapOf(
            "user id" to FirebaseAuth.getInstance().currentUser?.uid.toString(),
            "mis servicios" to arrayListOf<String>(),
            "servicios guardados" to arrayListOf<String>()
        )

        Firebase.firestore.collection("usuarios")
            .add(usuario)
            .addOnSuccessListener {
                Log.d("FIREBASE", "id: ${it.id}")
                val intent = Intent(this, HOME_ACTIVITY::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Log.e("FIREBASE", "exception: ${it.message}")
            }
    }
}