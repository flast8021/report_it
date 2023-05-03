package com.example.report_it

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.report_it.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.checkerframework.checker.units.qual.Length


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    //private lateinit var permissionCheck: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.txtBtnForgetPassword.setOnClickListener{
            val intent= Intent(this@LoginActivity, ResetActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.txtBtnRegister.setOnClickListener{
            val intent= Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnLogin.setOnClickListener{
            val email = binding.etLoginEmail.text.toString()
            val pass = binding.edtLoginPassword.text.toString()

            if(email.isNotEmpty() && pass.isNotEmpty()){
                    firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener{
                        if(it.isSuccessful){
                            //check if pass here is same/ not otherwise update password
                            val userDocRef = FirebaseFirestore.getInstance().collection("users").document(firebaseAuth.currentUser!!.uid)
                            val newPass = binding.edtLoginPassword.text.toString();

                            userDocRef.update("Login Pass", newPass)
                                .addOnSuccessListener {
                                }

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(this, "Logged in Successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        }else{
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

            }else{
                Toast.makeText(this, "Empty fields are not allowed.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}