package com.example.permissoins

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.permissoins.databinding.ActivityMainApiBinding

class MainActivityResultAPI: AppCompatActivity() {

    val  cameraPermissionLauncher = registerForActivityResult(
        RequestPermission(),
        ::gotCameraPermission
    )

    val  audioLocationPermissionLauncher = registerForActivityResult(
        RequestMultiplePermissions(),
        ::gotAudioLocationPermission
        )

        private lateinit var binding: ActivityMainApiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainApiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cameraButton.setOnClickListener {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.audioLocationButton.setOnClickListener {
            audioLocationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.RECORD_AUDIO)
            )
        }
    }

    private fun onCameraPermissionGranted(){
        Toast.makeText(this, "CAMERA", Toast.LENGTH_LONG).show()
    }

    private fun onAudioLocationPermissionGranted(){
        Toast.makeText(this, "AUDIO LOCATION", Toast.LENGTH_LONG).show()
    }

    private fun gotCameraPermission(granted: Boolean){
        if (granted){
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

    private fun gotAudioLocationPermission(grantResults: Map<String, Boolean>){
        if (grantResults.entries.all { it.value }){
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
}