package com.example.report_it

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.report_it.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore

import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val gender = resources.getStringArray(R.array.Gender)

        firestore=Firebase.firestore
        //Spinner for dropdown menu
        // access the spinner
        val spinner = findViewById<Spinner>(R.id.spinner)
        if (spinner != null) {
            val items = listOf("Male", "Female", "Prefer Not to Say")
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, items)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                view: View, position: Int, id: Long)
                {
                    val selectedItem = items[position]
                    val docRef = firestore.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
                    docRef.update("gender", selectedItem)
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                        // write code to perform some action

                    }
                }
        }//end of if statment

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        textButton_Login.setOnClickListener{
            val intent= Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.buttonRegister.setOnClickListener{
            val name = binding.edtRegisterName.text.toString()
            val pass = binding.edtRegisterPassword.text.toString()
            val email = binding.edtRegisterEmail.text.toString()
            val phone = binding.edtPhoneNumber.text.toString()
            val gender = binding.spinner.selectedItem.toString()

            if(email.isNotEmpty() && name.isNotEmpty() && pass.isNotEmpty() && phone.isNotEmpty() && checkbox_terms_and_condition.isChecked){

                firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener{
                    if(it.isSuccessful)
                    {
                        val userProfile = UserProfile(name, email, pass, phone, gender)

                        // Add user's registration data to Firestore
                        firestore.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).set(userProfile)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Registered Successfully! ", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, LoginActivity::class.java)
                                firebaseAuth.signOut()
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show()
                            }
                    }else{
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }else{
                Toast.makeText(this, " All fields are mandatory.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}