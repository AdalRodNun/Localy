package com.example.proyect

import android.content.Intent
import android.icu.util.TimeUnit.values
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.sql.BatchUpdateException
import java.time.chrono.JapaneseEra.values

class LOGIN_ACTIVITY : AppCompatActivity() {
    companion object{
        private  const val RC_SIGN_IN = 120
    }

    lateinit var email : EditText
    lateinit var password : EditText
    lateinit var google : Button
    lateinit var facebook : LoginButton
    private lateinit var googleSignInClient : GoogleSignInClient
    private lateinit var mAuth :FirebaseAuth
    var auth : FirebaseAuth ?= null
    var callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.email_login_text_edit)
        password = findViewById(R.id.password_text_edit)
        facebook = findViewById(R.id.login_button)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        mAuth = FirebaseAuth.getInstance()
        auth= FirebaseAuth.getInstance()

        facebook.setReadPermissions("email")
        facebook.setOnClickListener{
            facebooklogin()
        }
    }

    private fun facebooklogin() {
        facebook.registerCallback(callbackManager,object :FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                handleFacebookAccessToken(result!!.accessToken)
            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException?) {

            }
        })
    }

    fun google(v : View){
        signIn()
    }

    fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful){

                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("SignInActivity", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("SignInActivity", "Google sign in failed", e)
                }
            }
            else{
                Log.w("error",exception.toString())
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SignInActivity", "signInWithCredential:success")

                    loginExtra()

                    val intent = Intent(this, HOME_ACTIVITY::class.java)
                    startActivity(intent)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.d("SignInActivity", "signInWithCredential:failure", task.exception)

                }
            }
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
        login()
    }

    fun login(){
        Firebase.auth.signInWithEmailAndPassword(
            email.text.toString(),
            password.text.toString()
        ).addOnCompleteListener(this){
            if(it.isSuccessful){
                Log.d("FIREBASE", "Registro exitoso")
                val intent = Intent(this, HOME_ACTIVITY::class.java)
                startActivity(intent)
            } else {
                Log.e("FIREBASE", "Registro fracasó: ${it.exception?.message}")
                Toast.makeText(this, "Cuenta no existe o contraseña es erronea", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser

    }

    private fun handleFacebookAccessToken(token: AccessToken?) {
       Log.d("tag", "handleFacebookAccessToken:$token")
        val credential = FacebookAuthProvider.getCredential(token!!.token)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    Log.d("TAG", "-----------------funciona!!!!!!!!------------------------")
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SignInActivitF", "signInWithCredential:success")

                    loginExtra()
                    val intent = Intent(this, HOME_ACTIVITY::class.java)
                    startActivity(intent)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.d("TAG", "-----------------No funciona!!!------------------------")
                    Log.w("SignInActivitF", "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }
            }
    }

    fun loginExtra() {
        var idUsuario = FirebaseAuth.getInstance().currentUser?.uid.toString()

        Firebase.firestore.collection("usuarios")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    if(documento.data["user id"] == idUsuario) {
                        return@addOnSuccessListener
                    }
                }

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
            .addOnFailureListener {
                Log.e("FIRESTORE Menu", "Error al leer usuario: ${it.message}")
            }
    }
}
