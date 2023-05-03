package com.example.report_it

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_proof.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ProofActivity : AppCompatActivity() {

    companion object {
        private const val CAMERA_PERMISSION_CODE = 1
        private const val CAMERA_REQUEST_CODE = 2
        private const val VIDEO_REQUEST_CODE = 5
    }

    private lateinit var videoView: VideoView
    private lateinit var progressBar: ProgressBar
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private var imageUri: Uri? = null
    private var videoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proof)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val toolbar = findViewById<Toolbar>(R.id.toolbarIncident)
        setSupportActionBar(toolbar)

        capture_imageView.setImageResource(R.drawable.default_image)

        firebaseStorage = FirebaseStorage.getInstance()
        storageRef = firebaseStorage.reference

        videoView = findViewById(R.id.record_videoView)
        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
        }

        capture_button.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            }
        }

        record_button.setOnClickListener {
            startVideo(it)
        }
        record_videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
        }

        submit_button.setOnClickListener {
            Toast.makeText(
                this, "Your Report is submitted Successfully.",
                Toast.LENGTH_SHORT
            ).show()

            if (imageUri == null || videoUri == null) {
                Toast.makeText(this, "Please attach both an image and a video", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Your Report is submitted Successfully.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            } else {
                Toast.makeText(
                    this, "Camera Permissions are denied" +
                            "Dont Worry go to Settings > Permission and turn Camera on.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap
                    capture_imageView.setImageBitmap(thumbnail)

                    // Create a unique filename for the image
                    val filename = "IMG_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()) + ".jpg"

                    // Get a reference to the storage location
                    val imageRef = storageRef.child("images/$filename")

                    // Convert the bitmap to a byte array
                    val baos = ByteArrayOutputStream()
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()

                    // Upload the byte array to Firebase Storage
                    val uploadTask = imageRef.putBytes(data)

                    // Add a listener to track the progress of the upload
                    uploadTask.addOnProgressListener { taskSnapshot ->
                        val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                        progress_bar.progress = progress.toInt()
                    }

                    // Add a listener to handle the upload success/failure
                    uploadTask.addOnSuccessListener { taskSnapshot ->
                        // Get the download URL from the task snapshot
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            // Store the URI in the class variable for later use
                            imageUri = uri
                            Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                    }
                }

                VIDEO_REQUEST_CODE -> {

                    videoUri = data?.data
                    videoView.setVideoURI(videoUri)
                    record_videoView.setVideoURI(videoUri)
                    record_videoView.start()
                }
            }
        }
    }

    private fun startVideo(view: View) {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, VIDEO_REQUEST_CODE)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, DetailedActivity::class.java)
        startActivity(intent)
        finish()
    }
}
