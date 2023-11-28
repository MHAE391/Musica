package com.m391.musica.ui.home

import android.graphics.Canvas
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SwitchCompat
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            songsViewModel.refreshSongs()
        }
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
                    it.position,
                    getString(R.string.home)
                )
            )
        }
        binding.imagesRecyclerView.setupLinearRecycler(adapter)
    }

}