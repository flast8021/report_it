package com.example.report_it

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.report_it.databinding.ActivityResetBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ResetActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityResetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset)

        firebaseAuth = FirebaseAuth.getInstance()
        binding = ActivityResetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Reset button functionality
        binding.btnResetLink.setOnClickListener{

            val email = binding.edtResetLoginEmail.text.toString();

            if (email.isEmpty())
            {
                Toast.makeText(this,"Input Valid Email to Continue",Toast.LENGTH_SHORT).show()
            }else{
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener{
                    if (it.isSuccessful) {
                        Toast.makeText(this,"Resent Link is send.",Toast.LENGTH_SHORT).show()

                        val userDocRef = FirebaseFirestore.getInstance().collection("users").document(firebaseAuth.currentUser!!.uid)
                        val updates = hashMapOf<String, Any>(
                            "password_reset" to true // set the password_reset flag to true
                        )
                        userDocRef.update(updates)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Password updated in Firestore.", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error updating password in Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                            }


                        val newPass = FirebaseFirestore.getInstance().collection("users").document(firebaseAuth.currentUser!!.uid)

                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{ Toast.makeText(this, it.exception.toString(),Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        //Register button functionality
        binding.textButtonRegister.setOnClickListener{
            val intent =Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        //Login button functionality
        binding.txtButtonLogin.setOnClickListener{
            val intent =Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}