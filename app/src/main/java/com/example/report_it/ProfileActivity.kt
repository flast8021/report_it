package com.example.report_it

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profile.*


class ProfileActivity : AppCompatActivity() {

    private val TAG = "ProfileActivity"
    private val db = FirebaseFirestore.getInstance()
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)


        profile_button_ok.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


        firebaseAuth = FirebaseAuth.getInstance()

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        getUserData(userId)
    }

    private fun getUserData(userId: String) {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Retrieve the user registration data
                    val email = document.getString("email")
                    val name = document.getString("name")
                    val gender = document.getString("gender")
                    val phone = document.getString("phone")

                    // update the UI with the user's information
                    updateUI(email, name, gender,phone)
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting document: $exception")
            }
    }

    private fun updateUI(email: String?, name: String?, gender: String?, phone: String?) {
        // Update the UI with the user's information
        findViewById<TextView>(R.id.profile_email).text = email
        findViewById<TextView>(R.id.profile_name).text = name
        findViewById<TextView>(R.id.profile_gender).text = gender
        findViewById<TextView>(R.id.profile_phone).text = phone   }
}