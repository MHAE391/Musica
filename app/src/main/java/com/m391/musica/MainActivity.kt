package com.m391.musica

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.m391.musica.databinding.ActivityMainBinding
import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import com.m391.musica.database.AppDatabase
import com.m391.musica.database.MusicDAO
import com.m391.musica.services.SongService
import com.m391.musica.ui.shared_view_models.SongsViewModel
import com.m391.musica.ui.shared_view_models.SongsViewModelFactory

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(this, "You Have to Accept Media Permission", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        )
            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)

    }
}