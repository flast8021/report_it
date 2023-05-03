package com.example.report_it

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_donation.*

class DonationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        ok_button.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
            donation_url_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://gardabenevolent.ie/")
            startActivity(intent)
        }

        bottom_navigatio.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_exit -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                    false
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                    false
                }
                R.id.navigation_donation -> {
                    true
                }
                R.id.navigation_help -> {
                    val phoneNum = "tel:112"
                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse(phoneNum))
                    startActivity(dialIntent)
                    false
                }R.id.navigation_info -> {
                val intent = Intent(this, AppInfoActivity::class.java)
                startActivity(intent)
                finish()
                false
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