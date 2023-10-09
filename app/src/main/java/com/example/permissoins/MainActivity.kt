package com.example.permissoins

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.permissoins.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cameraButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED) {
                // разрешение уже есть
                onCameraPermissionGranted()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    RQ_PERMISSION_FOR_CAMERA
                )
            }
        }

        binding.audioLocationButton.setOnClickListener {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
                        PackageManager.PERMISSION_GRANTED)) {
                // разрешения уже есть
                onAudioLocationPermissionGranted()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO),
                    RQ_PERMISSION_FOR_AUDIO_LOCATION
                )
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            RQ_PERMISSION_FOR_CAMERA -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // разрешение получили
                    onCameraPermissionGranted()
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                        askUserForOpeningAppSettings()
                    } else {
                        Toast.makeText(this, "Denied", Toast.LENGTH_LONG).show()
                    }
                }
            }
            RQ_PERMISSION_FOR_AUDIO_LOCATION -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }){
                    onAudioLocationPermissionGranted()
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) &&
                        !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                        askUserForOpeningAppSettings()
                    } else {
                        Toast.makeText(this, "Denied", Toast.LENGTH_LONG).show()
                    }

                }
            }
        }
    }

    private fun onCameraPermissionGranted(){
        Toast.makeText(this, "CAMERA", Toast.LENGTH_LONG).show()
    }

    private fun onAudioLocationPermissionGranted(){
        Toast.makeText(this, "AUDIO LOCATION", Toast.LENGTH_LONG).show()
    }

    private fun askUserForOpeningAppSettings(){
        val appSettingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        if (packageManager.resolveActivity(appSettingsIntent, PackageManager.MATCH_DEFAULT_ONLY) == null){
            Toast.makeText(this, "Permission are denied forever", Toast.LENGTH_LONG).show()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Permission denied")
                .setMessage("Permission are denied forever" +
                "Would you like to change permission in settings?")
                .setPositiveButton("Open") {_, _ ->
                    startActivity(appSettingsIntent)
                }
                .create()
                .show()
        }
    }

    companion object{
        const val RQ_PERMISSION_FOR_CAMERA = 1
        const val RQ_PERMISSION_FOR_AUDIO_LOCATION = 2
    }
}