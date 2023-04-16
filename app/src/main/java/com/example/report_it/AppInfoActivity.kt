package com.example.report_it

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_app_info.*

class AppInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_info)

        bottom_navigati.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_exit -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_donation -> {
                    val intent = Intent(this, DonationActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_help -> {
                    val phoneNum = "tel:112"
                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse(phoneNum))
                    startActivity(dialIntent)
                    true
                }R.id.navigation_info -> {
                true
            }
                else -> false
            }
        }
    }
}