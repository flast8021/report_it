package com.example.report_it


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_detailed.*


class DetailedActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)

        firebaseAuth = FirebaseAuth.getInstance()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        firestore = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        button_next.setOnClickListener {
                val answer1 = q1_textBox.text.toString()
                val answer2 = q2_textBox.text.toString()
                val answer3 = q3_textBox.text.toString()
                val answer4 = q4_textBox.text.toString()
                val answer5 = q5_textBox.text.toString()

                if (checkbox_correct_details.isChecked && answer1.isNotEmpty() && answer2.isNotEmpty() && answer3.isNotEmpty() && answer4.isNotEmpty() && answer5.isNotEmpty()) {
                    val userAns = AnswerClass(answer1, answer2, answer3, answer4, answer5)
                    firestore.collection("Answers")
                        .document(FirebaseAuth.getInstance().currentUser!!.uid).set(userAns)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Please Attach Proofs of Incident!", Toast.LENGTH_SHORT)
                                .show()

                            val intent = Intent(this, ProofActivity::class.java)
                            startActivity(intent)
                            finish()
                        }.addOnFailureListener { exception ->
                            Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "OOPS! Some fields are missing.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            bottom_navigation2.setOnNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.navigation_exit -> {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                        true
                    }
                    R.id.navigation_profile -> {
                        val intent = Intent(this, ProfileActivity::class.java)
                        startActivity(intent)
                        finish()
                        true
                    }
                    R.id.navigation_donation -> {
                        val intent = Intent(this, DonationActivity::class.java)
                        startActivity(intent)
                        finish()
                        true
                    }
                    R.id.navigation_help -> {
                        val phoneNum = "tel:112"
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse(phoneNum))
                        startActivity(dialIntent)
                        finish()
                        true
                    }
                    R.id.navigation_info -> {
                        val intent = Intent(this, AppInfoActivity::class.java)
                        startActivity(intent)
                        finish()
                        true
                    }
                    else -> false
                }
            }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, ReportActivity::class.java)
        startActivity(intent)
        finish()
    }
}
