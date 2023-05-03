package com.example.report_it

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.example.report_it.ReportActivity.Companion.REQUEST_LOCATION_PERMISSION
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth



class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var searchList: ArrayList<DataClass>
    private lateinit var dataList: ArrayList<DataClass>
    lateinit var imageList:Array<Int>
    lateinit var titleList:Array<String>
    lateinit var descList: ArrayList<String>
    lateinit var detailImageView: ArrayList<Int>
    private lateinit var myAdapter: AdapterClass
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_exit)

        auth = FirebaseAuth.getInstance()

        imageList = arrayOf(
            R.drawable.ic_report,
            R.drawable.ic_donation,
            R.drawable.ic_help,
            R.drawable.ic_profile,
            R.drawable.ic_info,
            R.drawable.ic_sign_out,
        )
            titleList= arrayOf(
                "Report an Incident",
                "About Donation",
                "Call 112",
                "Profile",
                "About Application",
                "Sign Out",
            )

        descList = ArrayList<String>(listOf(getString(R.string.report),
            getString(R.string.donation),
            getString(R.string.call_112),
            getString(R.string.profile),
            getString(R.string.info),
            getString(R.string.out)))


        detailImageView = arrayListOf(
            R.drawable.ic_report,
            R.drawable.ic_donation,
            R.drawable.ic_help,
            R.drawable.ic_profile,
            R.drawable.ic_info,
            R.drawable.ic_sign_out,

        )

        recyclerView =findViewById(R.id.recylerView)
        searchView = findViewById(R.id.search)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        dataList = arrayListOf<DataClass>()
        searchList = arrayListOf<DataClass>()
        getData()

        searchView.clearFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchList.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if(searchText.isNotEmpty()){
                    dataList.forEach(){
                        if(it.dataTitle.toLowerCase(Locale.getDefault()).contains(searchText)){
                            searchList.add(it)
                        }
                    }
                    recyclerView.adapter!!.notifyDataSetChanged()
                }else{
                    searchList.clear()
                    searchList.addAll(dataList)
                    recyclerView.adapter!!.notifyDataSetChanged()
                }
                return false
            }
        })
        myAdapter= AdapterClass(searchList)
        recyclerView.adapter = myAdapter

        myAdapter.onItemClick = { data ->
            when (data.dataTitle) {
                "Report an Incident" -> {
                    val intent = Intent(this, ReportActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                "About Donation" -> {
                    val intent = Intent(this, DonationActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                "Call 112" -> {
                    val phoneNum = "tel:112"
                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse(phoneNum))
                    startActivity(dialIntent)
                    finish()
                }
                "Profile" -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                "About Application" -> {
                    val intent = Intent(this, AppInfoActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                "Sign Out" -> {
                    Toast.makeText(this, "Signed out Sucessfully" ,Toast.LENGTH_SHORT).show()
                    auth.signOut()
                    finish()
                }
            }
        }

    }


    private fun getData(){
        for (i in imageList.indices){
            val dataClass = DataClass(imageList[i],titleList[i])
            dataList.add(dataClass)
        }
        searchList.addAll(dataList)
        recyclerView.adapter = AdapterClass(searchList)
    }
}