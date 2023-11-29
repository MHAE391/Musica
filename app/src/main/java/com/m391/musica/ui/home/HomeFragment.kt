package com.m391.musica.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.m391.musica.R
import com.m391.musica.database.AppDatabase
import com.m391.musica.database.MusicDAO
import com.m391.musica.databinding.FragmentHomeBinding
import com.m391.musica.services.SongService
import com.m391.musica.ui.shared_view_models.SongsViewModel
import com.m391.musica.ui.shared_view_models.SongsViewModelFactory
import com.m391.musica.utils.setupLinearRecycler
import kotlinx.coroutines.launch
import java.lang.Math.abs

class HomeFragment : Fragment() {

    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var flag = false
            permissions.entries.forEach { entry ->
                val isGranted = entry.value
                if (!isGranted) {
                    Toast.makeText(
                        requireContext(),
                        "You Have To accept Media & Notification Permissions",
                        Toast.LENGTH_SHORT
                    ).show()
                    flag = true
                }
            }
            if (!flag) {
                lifecycleScope.launch {
                    songsViewModel.refreshSongs()
                    songsViewModel.refreshFavouriteSongs()
                }
            }
        }

    private val musicDAO: MusicDAO by lazy {
        AppDatabase.getDatabase(requireActivity().applicationContext).musicDao()
    }
    private val songsViewModel: SongsViewModel by activityViewModels {
        SongsViewModelFactory(
            requireActivity().application,
            SongService(),
            musicDAO
        )
    }
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(songsViewModel.deviceSongs)
    }


    @SuppressLint("InlinedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        if (!checkPermissions()) requestPermissions()
        return binding.root
    }

    @SuppressLint("InlinedApi")
    private fun requestPermissions() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.POST_NOTIFICATIONS
            )
        )
    }

    @SuppressLint("InlinedApi")
    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_MEDIA_AUDIO
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onResume() {
        super.onResume()
        if (checkPermissions())
            lifecycleScope.launch {
                songsViewModel.refreshSongs()
            }
        else requestPermissions()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopRefresh(viewLifecycleOwner)
    }

    override fun onStart() {
        super.onStart()
        val expandAnimation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.expand_searchview)
        binding.searchView.setOnSearchClickListener {
            it.startAnimation(expandAnimation)
            binding.text.visibility = View.GONE
            binding.favorite.visibility = View.GONE
        }
        binding.searchView.setOnCloseListener {
            binding.text.visibility = View.VISIBLE
            binding.favorite.visibility = View.VISIBLE
            false
        }
        binding.favorite.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFavouriteFragment())
        }
        viewModel.refreshSongs(viewLifecycleOwner)
        viewModel.searchListener(binding.searchView)
        setupRecycle()
    }


    private fun setupRecycle() {
        val adapter = SongAdapter {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToPlayerFragment(
                    songsViewModel.deviceSongs.value!!.indexOf(it),
                    getString(R.string.home)
                )
            )
        }
        binding.imagesRecyclerView.setupLinearRecycler(adapter)
    }
}