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
import androidx.lifecycle.lifecycleScope
import com.m391.musica.database.AppDatabase
import com.m391.musica.database.MusicDAO
import com.m391.musica.services.SongService
import com.m391.musica.ui.shared_view_models.SongsViewModel
import com.m391.musica.ui.shared_view_models.SongsViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}